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
     * @throws IOException If writing the archive fails.
     */
    public File archive() throws IOException {
        LOGGER.logInfo("Archiving the pipeline...");
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        File archiveTargetDir = config.getArchiveDir();
        File archiveTargetFile = new File(archiveTargetDir, "archived_execution_" + dtf.format(now) + ".zip");
        Zipper zipper = new Zipper(archiveTargetFile);
        
        File relative = new File(config.getPropertyFile().getCanonicalPath()).getParentFile();
        
        // archive configuration file
        try {
            addFileToZipper(zipper, config.getPropertyFile(), relative, "");
        } catch (IOException e) {
            LOGGER.logWarning("Could not archive configuration: " + e.getMessage());
        }
        
        // archive plugins
        try {
            addFileToZipper(zipper, config.getPluginsDir(), relative, "");
        } catch (IOException e) {
            LOGGER.logWarning("Could not archive plugin jars: " + e.getMessage());
        }
        
        // archive analysis output files
        try {
            if (outputFiles != null) {
                for (File outputFile : outputFiles) {
                    addFileToZipper(zipper, outputFile, relative, "/output");
                }
            }
        } catch (IOException e) {
            LOGGER.logWarning("Could not archive output: " + e.getMessage());
        }

        // archive our jar
        try {
            File kernelHavenJar = kernelHavenJarOverride;
            if (kernelHavenJar == null) {
                kernelHavenJar = new File(getClass().getProtectionDomain()
                        .getCodeSource().getLocation().getFile());
            }
            addFileToZipper(zipper, kernelHavenJar, relative, "");
        } catch (IOException e) {
            LOGGER.logError("Could not Archive KernelHaven.jar: " + e.getMessage());
        }

        // archive log file
        if (LOGGER.getLogFile() != null) {
            try {
                addFileToZipper(zipper, LOGGER.getLogFile(), relative, "/log");
            } catch (IOException e) {
                LOGGER.logWarning("Could not archive log output: " + e.getMessage());
            }
        }
        
        // archive source tree
        if (config.isArchiveSourceTree()) {
            try {
                addFileToZipper(zipper, config.getSourceTree(), relative, "");
            } catch (IOException e) {
                LOGGER.logWarning("Could not archive source tree: " + e.getMessage());
            }
        }
        
        LOGGER.logInfo("Archiving finished");
        
        return archiveTargetFile;
    }
    
    /**
     * Adds a file to the given zipper.
     * 
     * @param zipper The zipper to add to.
     * @param toAdd The file to add.
     * @param relativePath A directory to generate the relative path name inside the zip. If toAdd is inside this,
     *      then its location inside the zip is the relative path to this.
     * @param fallback The fallback location in the zip if toAdd is not inside of relativePath. The filename of toAdd
     *      will be appended to this.
     * 
     * @throws IOException If writing to the zipper fails. 
     */
    private void addFileToZipper(Zipper zipper, File toAdd, File relativePath, String fallback) throws IOException {
        String fullToAdd = toAdd.getCanonicalPath();
        String fullRelative = relativePath.getCanonicalPath() + File.separatorChar;
        
        String nameInZip = fallback + File.separatorChar + toAdd.getName();
        
        if (fullToAdd.startsWith(fullRelative)) {
            nameInZip = File.separatorChar + fullToAdd.substring(fullRelative.length());
        }
        
        zipper.copyFileToZip(toAdd, nameInZip);
    }
    
}
