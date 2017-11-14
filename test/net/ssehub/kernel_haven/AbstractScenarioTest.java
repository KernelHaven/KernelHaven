package net.ssehub.kernel_haven;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Logger.Level;
import net.ssehub.kernel_haven.util.Util;

/**
 * Abstract superclass for all scenario tests. This sets up the proper test environment and executes the pipeline.
 * Subclasses can override the check*() methods for assertions on the output.
 *
 * @author Adam
 */
public abstract class AbstractScenarioTest {

    private File resDir;
    
    private File cacheDir;
    
    private File outputDir;
    
    private File pluginsDir;
    
    private File logDir;
    
    private File srcTree;
    
    /**
     * We need to initialize the logger, since we use the Util class.
     */
    @BeforeClass
    public static void initLogger() {
        Logger.init();
        Logger.get().setLevel(Level.DEBUG);
    }
    
    /**
     * Creates the necessary temporary directories.
     * 
     * @throws IOException unwanted.
     */
    @Before
    public void setUp() throws IOException {
        resDir = new File("testdata/tmpRes");
        resDir.mkdir();
        
        cacheDir = new File("testdata/tmpCache");
        cacheDir.mkdir();
        
        outputDir = new File("testdata/tmpOutput");
        outputDir.mkdir();
        
        logDir = new File("testdata/tmpLog");
        logDir.mkdir();
        
        pluginsDir = new File("testdata/tmpPlugins");
        pluginsDir.mkdir();
        
        srcTree = new File("testdata/tmpSrc");
        copyFolder(getSourceTree(), srcTree);
    }
    
    /**
     * Deep-copies a folder to a destination.
     * 
     * @param src The source to copy.
     * @param dst The destination to copy to.
     * 
     * @throws IOException If copying fails.
     */
    private void copyFolder(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            dst.mkdir();
            for (File file : src.listFiles()) {
                copyFolder(file, new File(dst, file.getName()));
            }
            
        } else {
            Util.copyFile(src, dst);
        }
    }
    
    /**
     * Removes the temporary folders.
     * 
     * @throws IOException unwanted.
     */
    @After
    public void tearDown() throws IOException {
        Util.deleteFolder(resDir);
        Util.deleteFolder(cacheDir);
        Util.deleteFolder(outputDir);
        Util.deleteFolder(logDir);
        Util.deleteFolder(pluginsDir);
        Util.deleteFolder(srcTree);
    }
    
    /**
     * Main test method. Executes KernelHaven and calls the check*() methods.
     * 
     * @throws Exception unwanted.
     */
    @Test
    public void run()
        // CHECKSTYLE:OFF // we allow throws exception here
            throws Exception {
        // CHECKSTYLE:ON
        boolean success = Run.run(getPropertiesFile().getPath());
        assertThat(success, is(true));
        
        checkOutputDirResult(outputDir);
        checkCacheDirResult(cacheDir);
        checkLogDirResult(logDir);
        checkResDirResult(resDir);
    }
    
    /**
     * Defines which property file to use. This file should contain the following lines:
     * <code><pre>
     * source_tree = testdata/tmpSrc
     * resource_dir = testdata/tmpRes
     * cache_dir = testdata/tmpCache
     * output_dir = testdata/tmpOutput
     * plugins_dir = testdata/tmpPlugins
     * log.dir = testdata/tmpLog
     * </pre></code>
     * 
     * @return The property file to use.
     */
    protected abstract File getPropertiesFile();
    
    /**
     * Returns the source tree to analyze. This will be copied for a test run, to avoid modifications to the original.
     * 
     * @return The source tree to analyze.
     */
    protected abstract File getSourceTree();
    
    /**
     * Optionally does some post-checks on the resource directory.
     * 
     * @param dir The resource directory.
     * 
     * @throws Exception unwanted.
     */
    protected void checkResDirResult(File dir)
        // CHECKSTYLE:OFF // we allow throws exception here
            throws Exception {
        // CHECKSTYLE:ON
    }
    
    /**
     * Optionally does some post-checks on the output directory.
     * 
     * @param dir The output directory.
     * 
     * @throws Exception unwanted.
     */
    protected void checkOutputDirResult(File dir)
        // CHECKSTYLE:OFF // we allow throws exception here
            throws Exception {
        // CHECKSTYLE:ON
    }
    
    /**
     * Optionally does some post-checks on the cache directory.
     * 
     * @param dir The cache directory.
     * 
     * @throws Exception unwanted.
     */
    protected void checkCacheDirResult(File dir)
        // CHECKSTYLE:OFF // we allow throws exception here
            throws Exception {
        // CHECKSTYLE:ON
    }
    
    /**
     * Optionally does some post-checks on the log directory.
     * 
     * @param dir The log directory.
     * 
     * @throws Exception unwanted.
     */
    protected void checkLogDirResult(File dir)
        // CHECKSTYLE:OFF // we allow throws exception here
            throws Exception {
        // CHECKSTYLE:ON
    }
    
}
