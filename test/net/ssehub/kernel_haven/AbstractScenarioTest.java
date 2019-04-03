/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.ssehub.kernel_haven.util.Util;

/**
 * Abstract superclass for all scenario tests. This sets up the proper test environment and executes the pipeline.
 * Subclasses can override the check*() methods for assertions on the output.
 *
 * @author Adam
 */
@SuppressWarnings("null")
public abstract class AbstractScenarioTest {

    private File resDir;
    
    private File cacheDir;
    
    private File outputDir;
    
    private File pluginsDir;
    
    private File logDir;
    
    private File srcTree;
    
    /**
     * Creates the necessary temporary directories.
     * 
     * @throws IOException unwanted.
     */
    @Before
    public void setUp() throws IOException {
        resDir = new File("testdata", getClass().getSimpleName() + ".res");
        cacheDir = new File("testdata", getClass().getSimpleName() + ".cache");
        outputDir = new File("testdata", getClass().getSimpleName() + ".output");
        logDir = new File("testdata", getClass().getSimpleName() + ".log");
        pluginsDir = new File("testdata", getClass().getSimpleName() + ".plugins");
        srcTree = new File("testdata", getClass().getSimpleName() + ".src");

        if (resDir.exists()) {
            Util.deleteFolder(resDir);
        }
        if (cacheDir.exists()) {
            Util.deleteFolder(cacheDir);
        }
        if (outputDir.exists()) {
            Util.deleteFolder(outputDir);
        }
        if (logDir.exists()) {
            Util.deleteFolder(logDir);
        }
        if (pluginsDir.exists()) {
            Util.deleteFolder(pluginsDir);
        }
        if (srcTree.exists()) {
            Util.deleteFolder(srcTree);
        }
        
        resDir.mkdir();
        cacheDir.mkdir();
        outputDir.mkdir();
        logDir.mkdir();
        pluginsDir.mkdir();
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
        Assert.assertTrue(success);
        //assertThat(success, is(true));
        
        checkOutputDirResult(outputDir);
        checkCacheDirResult(cacheDir);
        checkLogDirResult(logDir);
        checkResDirResult(resDir);
    }
    
    /**
     * Defines which property file to use. This file should contain the following lines:
     * <code><pre>
     * source_tree = testdata/CLASSNAME.src
     * resource_dir = testdata/CLASSNAME.res
     * cache_dir = testdata/CLASSNAME.cache
     * output_dir = testdata/CLASSNAME.output
     * plugins_dir = testdata//CLASSNAME.plugins
     * log.dir = testdata//CLASSNAME.log
     * </pre></code>
     * 
     * @return The property file to use. <b>Default:</b> testdata/CLASSNAME/config.properties
     */
    protected File getPropertiesFile() {
        return new File("testdata" + File.separatorChar + getClass().getSimpleName() + File.separatorChar
                + "config.properties");
    }
    
    /**
     * Returns the source tree to analyze. This will be copied for a test run, to avoid modifications to the original.
     * 
     * @return The source tree to analyze. <b>Default:</b> testdata/CLASSNAME/source_tree
     */
    protected File getSourceTree() {
        return new File("testdata" + File.separatorChar + getClass().getSimpleName() + File.separatorChar
                + "source_tree");
    }
    
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
