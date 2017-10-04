package net.ssehub.kernel_haven.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Set;

import net.ssehub.kernel_haven.config.Configuration;

/**
 * A class for archiving an execution of KernelHaven.
 *
 * @author Adam
 */
public class PipelineArchiver {
    
    private static final Logger LOGGER = Logger.get();
    
    private Configuration config;

    private Set<File> outputFiles;
    
    private File kernelHavenJarOverride;
    
    private File relativeBase;
    
    /**
     * Creates a new PipelineArchiver for the given pipeline.
     * 
     * @param config The configuration of the pipeline.
     */
    public PipelineArchiver(Configuration config) {
        this.config = config;
    }
    
    /**
     * Sets the list of output files that the analysis created.
     * 
     * @param outputFiles The set of output files created by the analysis.
     */
    public void setAnalysisOutputFiles(Set<File> outputFiles) {
        this.outputFiles = outputFiles;
    }
    
    /**
     * Sets an override for the kernel_haven.jar to be added to the archive. Used in test cases.
     * 
     * @param kernelHavenJarOverride The kernelhaven jar to add.
     */
    void setKernelHavenJarOverride(File kernelHavenJarOverride) {
        this.kernelHavenJarOverride = kernelHavenJarOverride;
    }

    /**
     * Archives the pipeline. This should be called after all setters.
     * 
     * @return The created archive file.
     * 
     * @throws IOException If creating the archive fails.
     */
    public File archive() throws IOException {
        LOGGER.logInfo("Archiving the pipeline...");
        
        File archiveTargetDir = config.getArchiveDir();
        File archiveTargetFile = new File(archiveTargetDir,
                Timestamp.INSTANCE.getFilename("archived_execution", "zip"));
        ZipArchive archive = new ZipArchive(archiveTargetFile);
        
        relativeBase = new File(config.getPropertyFile().getCanonicalPath()).getParentFile();
        
        addFileToArchive(archive, config.getPropertyFile(), new File(""), "Could not archive configuration");
        for (File plugin : config.getPluginsDir().listFiles()) {
            addFileToArchive(archive, plugin, new File("plugins"), "Could not archive plugin " + plugin.getName());
        }
        if (outputFiles != null) {
            for (File outputFile : outputFiles) {
                addFileToArchive(archive, outputFile, new File("output"), "Could not archive output file");
            }
        }
        if (LOGGER.getLogFile() != null) {
            addFileToArchive(archive, LOGGER.getLogFile(), new File("log"), "Could not archive log file");
        }
        if (config.isArchiveSourceTree()) {
            addDirToArchive(archive, config.getSourceTree(), new File("source_tree"),
                    "Could not archive source tree correclty");
        }
        if (config.isArchiveResDir()) {
            addDirToArchive(archive, config.getResourceDir(), new File("res"),
                    "Could not archive resource directory correctly");
        }
        if (config.isArchiveCacheDir()) {
            addDirToArchive(archive, config.getCacheDir(), new File("cache"),
                    "Could not archive cache directory correctly");
        }
        File kernelHavenJar = kernelHavenJarOverride;
        if (kernelHavenJar == null) {
            kernelHavenJar = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().getFile());
        }
        addFileToArchive(archive, kernelHavenJar, new File(""), "Could not archive main jar file");
        
        archive.close();
        
        LOGGER.logInfo("Archiving finished");
        return archiveTargetFile;
    }
    
    /**
     * Adds a file to the given archive. The location inside the filename is based on the relative location to 
     * relativeBase.
     * 
     * @param archive The archive to add to.
     * @param toAdd The file to add.
     * @param fallbackDir The fallback location in the zip if toAdd is not inside of relativePath. The filename of toAdd
     *      will be appended to this.
     * @param message A warning message to be displayed if archiving this file failed.
     */
    private void addFileToArchive(ZipArchive archive, File toAdd, File fallbackDir, String message) {
        try {
            File inZipLocation = new File(fallbackDir, toAdd.getName());
            
            String fullToAdd = toAdd.getCanonicalPath();
            String fullRelative = relativeBase.getCanonicalPath() + File.separatorChar;
            if (fullToAdd.startsWith(fullRelative)) {
                inZipLocation = new File(fullToAdd.substring(fullRelative.length()));
            }
            
            archive.copyFileToArchive(inZipLocation, toAdd);
        } catch (IOException e) {
            LOGGER.logExceptionWarning(message, e);
        }
    }
    
    /**
     * Adds all files from a given directory to the archive, recursively. The location inside the filename is based on
     * the relative location to relativeBase.
     * 
     * @param archive The archive to add to.
     * @param dirToAdd The directory to add.
     * @param fallbackDir The path inside the archive to use instead, if toAdd is not relative to relativeBase.
     * @param message The message to be displayed if archiving this file failed.
     */
    private void addDirToArchive(ZipArchive archive, File dirToAdd, File fallbackDir, String message) {
        try {
            File inZipLocationMutable = fallbackDir;
            String fullToAdd = dirToAdd.getCanonicalPath();
            String fullRelative = relativeBase.getCanonicalPath() + File.separatorChar;
            if (fullToAdd.startsWith(fullRelative)) {
                inZipLocationMutable = new File(fullToAdd.substring(fullRelative.length()));
            }
            File inZipLocation = inZipLocationMutable;
            
            Files.walk(dirToAdd.toPath())
                .forEach((path) -> {
                    if (Files.isRegularFile(path)) {
                        // path contains the file path in the actual file system
                        // create inZip, which contains the path that the file should have in the archive
                        //   this is based on the inZipLocation
                        File inZip = new File(inZipLocation, dirToAdd.toPath().relativize(path).toString());
                        try {
                            archive.copyFileToArchive(inZip, path.toFile());
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                });
            
        } catch (IOException | UncheckedIOException e) {
            LOGGER.logExceptionWarning(message, e);
        }
    }
    
}
