package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Set;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A class for archiving an execution of KernelHaven.
 *
 * @author Adam
 */
public class PipelineArchiver {
    
    private static final Logger LOGGER = Logger.get();
    
    private @NonNull Configuration config;

    private @Nullable Set<@NonNull File> outputFiles;
    
    private @Nullable File kernelHavenJarOverride;
    
    private File relativeBase;
    
    /**
     * Creates a new PipelineArchiver for the given pipeline.
     * 
     * @param config The configuration of the pipeline.
     */
    public PipelineArchiver(@NonNull Configuration config) {
        this.config = config;
    }
    
    /**
     * Sets the list of output files that the analysis created.
     * 
     * @param outputFiles The set of output files created by the analysis.
     */
    public void setAnalysisOutputFiles(@Nullable Set<@NonNull File> outputFiles) {
        this.outputFiles = outputFiles;
    }
    
    /**
     * Sets an override for the kernel_haven.jar to be added to the archive. Used in test cases.
     * 
     * @param kernelHavenJarOverride The kernelhaven jar to add.
     */
    void setKernelHavenJarOverride(@Nullable File kernelHavenJarOverride) {
        this.kernelHavenJarOverride = kernelHavenJarOverride;
    }

    /**
     * Archives the pipeline. This should be called after all setters.
     * 
     * @return The created archive file.
     * 
     * @throws IOException If creating the archive fails.
     */
    public @NonNull File archive() throws IOException {
        LOGGER.logInfo("Archiving the pipeline...");
        
        File archiveTargetDir = config.getValue(DefaultSettings.ARCHIVE_DIR);
        File archiveTargetFile = new File(archiveTargetDir,
                Timestamp.INSTANCE.getFilename("archived_execution", "zip"));
        ZipArchive archive = new ZipArchive(archiveTargetFile);
        
        File propertyFile = config.getPropertyFile();
        if (propertyFile != null) {
            relativeBase = new File(propertyFile.getCanonicalPath()).getParentFile();
            addFileToArchive(archive, propertyFile, new File(""), "Could not archive configuration");
        } else {
            relativeBase = new File("").getCanonicalFile(); // current working dir
        }
        
        for (File plugin : config.getValue(DefaultSettings.PLUGINS_DIR).listFiles()) {
            addFileToArchive(archive, plugin, new File("plugins"), "Could not archive plugin " + plugin.getName());
        }
        if (outputFiles != null) {
            for (File outputFile : outputFiles) {
                addFileToArchive(archive, outputFile, new File("output"), "Could not archive output file");
            }
        }
        File logFile = LOGGER.getLogFile();
        if (logFile != null) {
            addFileToArchive(archive, logFile, new File("log"), "Could not archive log file");
        }
        if (config.getValue(DefaultSettings.ARCHIVE_SOURCE_TREE)) {
            addDirToArchive(archive, config.getValue(DefaultSettings.SOURCE_TREE), new File("source_tree"),
                    "Could not archive source tree correclty");
        }
        if (config.getValue(DefaultSettings.ARCHIVE_RES_DIR)) {
            addDirToArchive(archive, config.getValue(DefaultSettings.RESOURCE_DIR), new File("res"),
                    "Could not archive resource directory correctly");
        }
        if (config.getValue(DefaultSettings.ARCHIVE_CACHE_DIR)) {
            addDirToArchive(archive, config.getValue(DefaultSettings.CACHE_DIR), new File("cache"),
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
    private void addFileToArchive(@NonNull ZipArchive archive, @NonNull File toAdd, @NonNull File fallbackDir,
            @NonNull String message) {
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
    private void addDirToArchive(@NonNull ZipArchive archive, @NonNull File dirToAdd, @NonNull File fallbackDir,
            @NonNull String message) {
        
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
                            archive.copyFileToArchive(inZip, notNull(path.toFile()));
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
