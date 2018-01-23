package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;

/**
 * Tests the {@link PipelineArchiver} class.
 *
 * @author Adam
 */
@SuppressWarnings("null")
public class PipelineArchiverTest {
    
    private static final File BASE_TESTDATA = new File("testdata/archiver");
    
    private File result;
    
    /**
     * Sets up the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }
    
    /**
     * Removes the result after a test, if present.
     */
    @After
    public void removeResult() {
        if (result != null) {
            result.delete();
        }
    }
    
    /**
     * Constructs the directory name for the given testname.
     * 
     * @param testname The testname.
     * 
     * @return The directory containing the test files.
     */
    private File getTestDirectory(String testname) {
        return new File(BASE_TESTDATA, testname);
    }
    
    /**
     * Creates the configuration for the given test in testdata/archiver.
     * 
     * @param testDirectory The directory containting the test file.
     * @param propertyName The name of the property file.
     * @return The configuration for the given test.
     * 
     * @throws IOException If creating the configuration fails.
     * @throws SetUpException If creating the configuration fails.
     */
    private Configuration prepareConfig(File testDirectory, String propertyName) throws IOException, SetUpException {
        File propertyFile = new File(testDirectory, propertyName);
        
        Properties props = new Properties();
        props.load(new FileReader(propertyFile));
        
        TestConfiguration config = new TestConfiguration(props);
        config.setPropertyFile(propertyFile);
        
        return config;
    }
    
    /**
     * A basic test for the archiver.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testBasic() throws SetUpException, IOException {
        File testDir = getTestDirectory("basic");
        Configuration config = prepareConfig(testDir, "basic.properties");
        
        PipelineArchiver archiver = new PipelineArchiver(config);
        archiver.setKernelHavenJarOverride(new File(testDir, "kernel_haven.jar"));
        
        result = archiver.archive();
        assertThat(result.getAbsolutePath(), startsWith(new File("testdata/archiver").getAbsolutePath()));
        
        assertThat(result.isFile(), is(true));
        
        ZipArchive archive = new ZipArchive(result);
        
        Set<File> files = archive.listFiles();
        assertThat(files.size(), is(3));
        assertThat(files, CoreMatchers.hasItems(
                new File("basic.properties"),
                new File("plugins/test.jar"),
                new File("kernel_haven.jar")));
        
        archive.close();
        
        result.delete();
    }
    
    /**
     * Tests whether fallback names for resources in parent folders of the configuration.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testFallback() throws SetUpException, IOException {
        File testDir = getTestDirectory("fallback");
        Configuration config = prepareConfig(testDir, "fallback.properties");
        
        PipelineArchiver archiver = new PipelineArchiver(config);
        archiver.setKernelHavenJarOverride(new File(testDir, "../fallback_kernel_haven.jar"));
        
        result = archiver.archive();
        assertThat(result.getAbsolutePath(), startsWith(new File("testdata/archiver").getAbsolutePath()));
        
        assertThat(result.isFile(), is(true));
        
        ZipArchive archive = new ZipArchive(result);

        Set<File> files = archive.listFiles();
        assertThat(files.size(), is(3));
        assertThat(files, CoreMatchers.hasItems(
                new File("fallback.properties"),
                new File("plugins/test.jar"),
                new File("fallback_kernel_haven.jar")));
        
        archive.close();
        
        result.delete();
    }
    
    /**
     * Same as basic, but with different names to make sure that the archiver does not use fixed names.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testDifferentNames() throws SetUpException, IOException {
        File testDir = getTestDirectory("different_names");
        Configuration config = prepareConfig(testDir, "different_names.properties");
        
        PipelineArchiver archiver = new PipelineArchiver(config);
        archiver.setKernelHavenJarOverride(new File(testDir, "main.jar"));
        
        result = archiver.archive();
        assertThat(result.getAbsolutePath(), startsWith(new File("testdata/archiver").getAbsolutePath()));
        
        assertThat(result.isFile(), is(true));
        
        ZipArchive archive = new ZipArchive(result);

        Set<File> files = archive.listFiles();
        assertThat(files.size(), is(3));
        assertThat(files, CoreMatchers.hasItems(
                new File("different_names.properties"),
                new File("addons/a_plugin.jar"),
                new File("main.jar")));
        
        archive.close();
        
        result.delete();
    }
    
    /**
     * Tests whether the additional dirs (like cache and res) are added correctly.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testAdditonalDirs() throws SetUpException, IOException {
        File testDir = getTestDirectory("additonal_dirs");
        Configuration config = prepareConfig(testDir, "config.properties");
        
        PipelineArchiver archiver = new PipelineArchiver(config);
        archiver.setKernelHavenJarOverride(new File(testDir, "kernel_haven.jar"));
        
        result = archiver.archive();
        assertThat(result.getAbsolutePath(), startsWith(new File("testdata/archiver").getAbsolutePath()));
        
        assertThat(result.isFile(), is(true));
        
        try (ZipArchive archive = new ZipArchive(result)) {
            Set<File> files = archive.listFiles();
            
            assertThat(files.size(), is(6));
            assertThat(files, CoreMatchers.hasItems(
                    new File("config.properties"),
                    new File("plugins/test.jar"),
                    new File("cache/some_cache.txt"),
                    new File("my_resource_dir/some_res.txt"),
                    new File("source_tree/test.source_file"),
                    new File("kernel_haven.jar")));
        
        }
        result.delete();
    }

}
