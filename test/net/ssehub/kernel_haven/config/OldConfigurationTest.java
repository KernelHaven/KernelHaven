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
package net.ssehub.kernel_haven.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.EmptyBuildModelExtractor;
import net.ssehub.kernel_haven.code_model.EmptyCodeModelExtractor;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.variability_model.EmptyVariabilityModelExtractor;

/**
 * Tests the Configuration class. This was the test case for the old configuration implementation. This is mainly here
 * to check for regressions. New test cases should go into {@link ConfigurationTest}.
 * 
 * @author Adam
 * @author Moritz
 */
@SuppressWarnings("null")
public class OldConfigurationTest {

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
        File somefolder = new File("testdata/configs/somefolder/");

        Configuration config = new Configuration(propfile);
        DefaultSettings.registerAllSettings(config);
        

        assertThat(config.getValue(DefaultSettings.ANALYSIS_CLASS), equalTo("some.package.ClassName"));
        assertThat(config.getValue(DefaultSettings.OUTPUT_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.PLUGINS_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.CACHE_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.SOURCE_TREE), equalTo(somefolder));
        assertThat(config.getPropertyFile(), equalTo(propfile));
        
        assertThat(Util.getExtractorResourceDir(config, getClass()),
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
        DefaultSettings.registerAllSettings(config);

        assertThat(config.getValue(DefaultSettings.ARCHIVE_DIR), is(new File(".")));
        assertThat(config.getValue(DefaultSettings.BUILD_EXTRACTOR_CLASS),
                is(EmptyBuildModelExtractor.class.getName()));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_CLASS),
                is(EmptyCodeModelExtractor.class.getName()));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_EXTRACTOR_CLASS),
                is(EmptyVariabilityModelExtractor.class.getName()));
        assertThat(config.getValue(DefaultSettings.LOG_DIR), is(new File(".")));
        assertThat(config.getValue(DefaultSettings.ARCHIVE), is(false));
        assertThat(config.getValue(DefaultSettings.LOG_CONSOLE), is(true));
        assertThat(config.getValue(DefaultSettings.LOG_LEVEL), is(Logger.Level.INFO));
        assertThat(config.getValue(DefaultSettings.LOG_FILE), is(false));

        assertThat(config.getValue(DefaultSettings.ARCH), nullValue());
        
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_TIMEOUT), is(0));
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_READ), is(false));
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_WRITE), is(false));

        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_TIMEOUT), is(0));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_READ), is(false));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_WRITE), is(false));
        
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_TIMEOUT), is(0));
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_READ), is(false));
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_WRITE), is(false));
        List<String> files = new LinkedList<>();
        files.add("");
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_FILES), is(files));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_FILE_REGEX).pattern(), equalTo(".*\\.c"));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_THREADS), is(1));
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
        DefaultSettings.registerAllSettings(config);

        assertThat(config.getValue(DefaultSettings.ARCHIVE_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.BUILD_EXTRACTOR_CLASS), equalTo("some.package.ClassName"));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_CLASS), equalTo("some.package.ClassName"));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_EXTRACTOR_CLASS), equalTo("some.package.ClassName"));
        assertThat(config.getValue(DefaultSettings.LOG_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.ARCHIVE), is(true));
        assertThat(config.getValue(DefaultSettings.LOG_CONSOLE), is(false));
        assertThat(config.getValue(DefaultSettings.LOG_LEVEL), is(Logger.Level.DEBUG));
        assertThat(config.getValue(DefaultSettings.LOG_FILE), is(true));
        
        assertThat(config.getValue(DefaultSettings.SOURCE_TREE), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.CACHE_DIR), equalTo(somefolder));
        assertThat(config.getValue(DefaultSettings.ARCH), is("x86"));
        
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_TIMEOUT), is(42));
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_READ), is(true));
        assertThat(config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_WRITE), is(true));

        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_TIMEOUT), is(42));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_READ), is(true));
        assertThat(config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_WRITE), is(true));
        
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_TIMEOUT), is(42));
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_READ), is(true));
        assertThat(config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_WRITE), is(true));
        List<String> files = new LinkedList<>();
        files.add("file1.c");
        files.add("dir/file2.c");
        files.add("dir/subdir/");
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_FILES), is(files));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_FILE_REGEX).pattern(), equalTo(".*\\.(h|c|S)"));
        assertThat(config.getValue(DefaultSettings.CODE_EXTRACTOR_THREADS), is(42));
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
        Configuration config = new Configuration(invalidLog);
        DefaultSettings.registerAllSettings(config);
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
        Configuration config = new Configuration(invalidArchive);
        DefaultSettings.registerAllSettings(config);
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
        Configuration config = new Configuration(new File("testdata/configs/invalid_number.properties"));
        DefaultSettings.registerAllSettings(config);
    }
    
    /**
     * Tests if configuration fails if an invalid regex is provided.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testInvalidRegexPropertyFile() throws SetUpException {
        Configuration config = new Configuration(new File("testdata/configs/invalid_regex.properties"));
        DefaultSettings.registerAllSettings(config);
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
        Configuration config = new Configuration(props);
        DefaultSettings.registerAllSettings(config);
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
        Configuration config = new Configuration(new File("testdata/configs/notexistingdir.properties"));
        DefaultSettings.registerAllSettings(config);
    }
    
}
