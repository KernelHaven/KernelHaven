package net.ssehub.kernel_haven.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     */
    public PipelineArchiver() {
    }
    
    /**
     * Sets the configuration of the pipeline.
     * 
     * @param config The configuration of the pipeline.
     */
    public void setConfig(Configuration config) {
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
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        File archiveTargetDir = config.getArchiveDir();
        File archiveTargetFile = new File(archiveTargetDir, "archived_execution_" + dtf.format(now) + ".zip");
        Zipper zipper = new Zipper(archiveTargetFile);
        
        relativeBase = new File(config.getPropertyFile().getCanonicalPath()).getParentFile();
        
        addFileToZipper(zipper, config.getPropertyFile(), "", "Could not archive configuration");
        addFileToZipper(zipper, config.getPluginsDir(), "", "Could not archive plugin jars");
        if (outputFiles != null) {
            for (File outputFile : outputFiles) {
                addFileToZipper(zipper, outputFile, "/output", "Could not archive output file");
            }
        }
        if (LOGGER.getLogFile() != null) {
            addFileToZipper(zipper, LOGGER.getLogFile(), "/log", "Could not archive log file");
        }
        if (config.isArchiveSourceTree()) {
            addFileToZipper(zipper, config.getSourceTree(), "", "Could not archive source tree");
        }
        if (config.isArchiveResDir()) {
            addFileToZipper(zipper, config.getResourceDir(), "", "Could not archive resource directory");
        }
        if (config.isArchiveCacheDir()) {
            addFileToZipper(zipper, config.getCacheDir(), "", "Could not archive cache directory");
        }
        File kernelHavenJar = kernelHavenJarOverride;
        if (kernelHavenJar == null) {
            kernelHavenJar = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().getFile());
        }
        addFileToZipper(zipper, kernelHavenJar, "", "Could not archive main jar file");
        
        LOGGER.logInfo("Archiving finished");
        return archiveTargetFile;
    }
    
    /**
     * Adds a file to the given zipper. The location inside the filename is based on the relative location to 
     * relativeBase.
     * 
     * @param zipper The zipper to add to.
     * @param toAdd The file to add.
     * @param fallback The fallback location in the zip if toAdd is not inside of relativePath. The filename of toAdd
     *      will be appended to this.
     * @param message A warning message to be displayed if archiving this file failed.
     */
    private void addFileToZipper(Zipper zipper, File toAdd, String fallback, String message) {
        try {
            String fullToAdd = toAdd.getCanonicalPath();
            String fullRelative = relativeBase.getCanonicalPath() + File.separatorChar;
            
            String nameInZip = fallback + File.separatorChar + toAdd.getName();
            
            if (fullToAdd.startsWith(fullRelative)) {
                nameInZip = File.separatorChar + fullToAdd.substring(fullRelative.length());
            }
        
            zipper.copyFileToZip(toAdd, nameInZip);
        } catch (IOException e) {
            LOGGER.logExceptionWarning(message, e);
        }
    }
    
}
