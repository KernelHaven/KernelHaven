package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.BuildExtractorConfiguration;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;

/**
 * Tests the Configuration class.
 * 
 * @author Adam
 * @author Moritz
 */
public class ConfigurationTest {

    /**
     * Tests if the configuration initializes correctly with a minimal configuration file and provides
     * the correct property-values defined in that file.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testMinimalConfiguration() throws SetUpException {

        File propfile = new File("testdata/configs/minimal.properties");

        Configuration config = new Configuration(propfile);
        File somefolder = new File("testdata/configs/somefolder/");

        assertThat(config.getAnalysisClassName(), equalTo("some.package.ClassName"));
        assertThat(config.getOutputDir(), equalTo(somefolder));
        assertThat(config.getPluginsDir(), equalTo(somefolder));
        assertThat(config.getPropertyFile(), equalTo(propfile));
        
        assertThat(config.getVariabilityConfiguration().getExtractorResourceDir(getClass()),
                equalTo(new File(somefolder, getClass().getName())));
    }

    /**
     * Tests if the configuration provides correct default values.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testDefaultValues() throws SetUpException {

        Configuration config = new Configuration(new File("testdata/configs/minimal.properties"));

        assertThat(config.getArchiveDir(), nullValue());
        assertThat(config.getBuildExtractorClassName(), nullValue());
        assertThat(config.getCodeExtractorClassName(), nullValue());
        assertThat(config.getLogDir(), nullValue());
        assertThat(config.getVariabilityExtractorClassName(), nullValue());
        assertThat(config.isArchive(), is(false));
        assertThat(config.isLogConsole(), is(true));
        assertThat(config.isLogDebug(), is(false));
        assertThat(config.isLogError(), is(true));
        assertThat(config.isLogFile(), is(false));
        assertThat(config.isLogInfo(), is(true));
        assertThat(config.isLogWarning(), is(true));

        BuildExtractorConfiguration bConfig = config.getBuildConfiguration();
        assertThat(bConfig.getProviderTimeout(), is(0L));
        assertThat(bConfig.isCacheRead(), is(false));
        assertThat(bConfig.isCacheWrite(), is(false));
        assertThat(bConfig.getArch(), nullValue());
        assertThat(bConfig.getSourceTree(), nullValue());
        assertThat(bConfig.getCacheDir(), nullValue());

        VariabilityExtractorConfiguration vConfig = config.getVariabilityConfiguration();
        assertThat(vConfig.getProviderTimeout(), is(0L));
        assertThat(vConfig.isCacheWrite(), is(false));
        assertThat(vConfig.isCacheRead(), is(false));
        assertThat(vConfig.getArch(), nullValue());
        assertThat(vConfig.getSourceTree(), nullValue());
        assertThat(vConfig.getCacheDir(), nullValue());
        
        CodeExtractorConfiguration cConfig = config.getCodeConfiguration();
        assertThat(cConfig.getProviderTimeout(), is(0L));
        assertThat(cConfig.isCacheWrite(), is(false));
        assertThat(cConfig.isCacheRead(), is(false));
        assertThat(cConfig.getFiles(), equalTo(new File[] {new File("") }));
        assertThat(cConfig.getFilenamePattern().pattern(), equalTo(".*\\.c"));
        assertThat(cConfig.getThreads(), is(1));
        assertThat(cConfig.getArch(), nullValue());
        assertThat(cConfig.getSourceTree(), nullValue());
        assertThat(cConfig.getCacheDir(), nullValue());
    }

    /**
     * Tests if the configuration succeeds and is initialized with correct values when all
     * possible property-entries for the kernelhaven infrastructure are set through a properties file.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testMaxConfig() throws SetUpException {
        File somefolder = new File("testdata/configs/somefolder/");
        Configuration config = new Configuration(new File("testdata/configs/gigantic.properties"));

        assertThat(config.getArchiveDir(), equalTo(somefolder));
        assertThat(config.getBuildExtractorClassName(), equalTo("some.package.ClassName"));
        assertThat(config.getCodeExtractorClassName(), equalTo("some.package.ClassName"));
        assertThat(config.getLogDir(), equalTo(somefolder));
        assertThat(config.getVariabilityExtractorClassName(), equalTo("some.package.ClassName"));
        assertThat(config.isArchive(), is(true));
        assertThat(config.isLogConsole(), is(false));
        assertThat(config.isLogDebug(), is(true));
        assertThat(config.isLogError(), is(false));
        assertThat(config.isLogFile(), is(true));
        assertThat(config.isLogInfo(), is(false));
        assertThat(config.isLogWarning(), is(false));
        
        BuildExtractorConfiguration bConfig = config.getBuildConfiguration();
        assertThat(bConfig.getProviderTimeout(), is(42L));
        assertThat(bConfig.isCacheRead(), is(true));
        assertThat(bConfig.isCacheWrite(), is(true));
        assertThat(bConfig.getArch(), equalTo("x86"));
        assertThat(bConfig.getSourceTree(), equalTo(somefolder));
        assertThat(bConfig.getCacheDir(), equalTo(somefolder));

        VariabilityExtractorConfiguration vConfig = config.getVariabilityConfiguration();
        assertThat(vConfig.getProviderTimeout(), is(42L));
        assertThat(vConfig.isCacheWrite(), is(true));
        assertThat(vConfig.isCacheRead(), is(true));
        assertThat(vConfig.getArch(), equalTo("x86"));
        assertThat(vConfig.getSourceTree(), equalTo(somefolder));
        assertThat(vConfig.getCacheDir(), equalTo(somefolder));
        
        CodeExtractorConfiguration cConfig = config.getCodeConfiguration();
        assertThat(cConfig.getProviderTimeout(), is(42L));
        assertThat(cConfig.isCacheWrite(), is(true));
        assertThat(cConfig.isCacheRead(), is(true));
        assertThat(cConfig.getFiles(), equalTo(new File[] {
            new File("file1.c"),
            new File("dir/file2.c"),
            new File("dir/subdir/")
        }));
        assertThat(cConfig.getFilenamePattern().pattern(), equalTo(".*\\.(h|c|S)"));
        assertThat(cConfig.getThreads(), is(42));
        assertThat(cConfig.getArch(), equalTo("x86"));
        assertThat(cConfig.getSourceTree(), equalTo(somefolder));
        assertThat(cConfig.getCacheDir(), equalTo(somefolder));
    }

    /**
     * Checks if configuration fails when logging to file is activated but no target directory is defined.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidLogSettings() throws SetUpException {

        File invalidLog = new File("testdata/configs/invalid_log.properties");
        assertThat(invalidLog.exists(), is(true));
        new Configuration(invalidLog);

    }

    /**
     * Checks if configuration fails when archiving is activated but no target directory is defined.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidArchiveSettings() throws SetUpException {
        File invalidArchive = new File("testdata/configs/invalid_archive.properties");
        assertThat(invalidArchive.exists(), is(true));
        new Configuration(invalidArchive);

    }

    /**
     * Tests if configuration fails if no valid properties file is passed to constructor.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testNotExistingPropertyFile() throws SetUpException {
        new Configuration(new File("testdata/configs/not_existing.properties"));

    }
    

    /**
     * Tests if configuration fails when a non-numeric value is used for a property that expects
     * a number.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidNumberPropertyFile() throws SetUpException {
        new Configuration(new File("testdata/configs/invalid_number.properties")).getBuildConfiguration();

    }
    
    /**
     * Tests if configuration fails if an invalid regex is provided.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidRegexPropertyFile() throws SetUpException {
        new Configuration(new File("testdata/configs/invalid_regex.properties")).getCodeConfiguration();
    }
    
    /**
     * Tests if configuration fails if a file is used instead of a folder for a property 
     * expecting a folder.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidDirPropertyFile() throws SetUpException {
        File props = new File("testdata/configs/invaliddir.properties");
        assertThat(props.isFile(), is(true));
        new Configuration(props);

    }
    
    /**
     * Tests if configuration fails if a non-existant directory is used for a property expecting
     * an existing folder.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testNotExistingDirPropertyFile() throws SetUpException {
        new Configuration(new File("testdata/configs/notexistingdir.properties"));

    }
    
    
    
    

}
