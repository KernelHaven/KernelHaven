package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.TestConfiguration;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.util.CodeExtractorException;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;

/**
 * Tests the code model provider.
 * 
 * @author Adam
 * @author Johannes
 */
public class CodeModelProviderTest {
    
    private static final File SOURCE_TREE = new File("testdata/source_tree");
    
    /**
     * Initiates the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }

    /**
     * Small class that implements {@see AbstractCodeModelExtractor} for testing
     * purposes. This waits 1/2 second after start() is called and returns four empty {@link SourceFile}s.
     */
    private static class PseudoExtractor extends AbstractCodeModelExtractor {

        private boolean throwException;
        
        private Set<File> filesToParse;
        
        /**
         * Creates a new pseudo extractor.
         * 
         * @param throwException Whether to throw an exception or return a normal result.
         */
        public PseudoExtractor(boolean throwException) {
            this.throwException = throwException;
            filesToParse = new HashSet<>();
        }
        
        @Override
        protected void init(CodeExtractorConfiguration config) throws SetUpException {
        }

        @Override
        protected SourceFile runOnFile(File target) throws ExtractorException {
            filesToParse.add(target);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (throwException) {
                throw new CodeExtractorException(target, "Test exception");
            } else {
                return new SourceFile(target);                    
            }
        }

        @Override
        protected String getName() {
            return "PseudoCodeExtractor";
        }

    }

    /**
     * Tests whether the set() and get() Methods for the result properly wait
     * for each other.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testGetSetSynchronization() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
    }

    /**
     * Tests whether exceptions are properly passed to get().
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testExceptions() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        
        assertThat(provider.getNextResult(), nullValue());
        
        CodeExtractorException exception = (CodeExtractorException) provider.getNextException();
        assertThat(exception, notNullValue());
        assertThat(exception.getCausingFile(), is(new File("test.c")));
        assertThat(provider.getNextException(), nullValue());
    }

    /**
     * Test whether timeout correctly terminates the waiting process with an
     * exception.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testShortTimeout() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.provider.timeout", "10");
        CodeModelProvider provider = new CodeModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();

        assertThat(provider.getNextResult(), nullValue());
        assertThat(provider.getNextException(), notNullValue());
    }

    /**
     * Tests whether too long timeout is not triggered.
     * 
     * @throws SetUpException
     *             unwanted
     */
    @Test
    public void testLongTimeout() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.provider.timeout", "1500");
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextException(), nullValue());
    }
    
    /**
     * Tests whether multiple generated results are read properly.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testGetMultiple() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();

        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextResult(), nullValue());
    }

    /**
     * Tests if the code model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("code.provider.cache.read", "true");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("cache_dir", new File("testdata/cmCaching/cache_valid").getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
    }
    
    /**
     * Tests if the code model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws SetUpException, IOException {
        File cacheDir = new File("testdata/cmCaching/tmp_cache");
        cacheDir.mkdir();
        
        // precondition: cache directory is empty
        assertThat(cacheDir.listFiles().length, is(0));
        
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("cache_dir", cacheDir.getAbsolutePath());
        config.setProperty("code.provider.cache.write", "true");
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
        
        // synchronize with the writing, since it runs in parallel to us...
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        
        // test if cache is now not empty
        assertThat(cacheDir.listFiles().length, is(1));
        
        // cleanup
        Util.deleteFolder(cacheDir);
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testFindFilesToParseSimple() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.extractor.files", "test.c, dir/dir2/test2.c, dir/test.c");
        //config.setProperty("code.extractor.file_regex", "");
        config.setProperty("code.extractor.threads", "1");
        
        PseudoExtractor extractor = new PseudoExtractor(false);
        CodeModelProvider provider = new CodeModelProvider();
        provider.setExtractor(extractor);
        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();

        // read until the provider is empty
        SourceFile file = provider.getNextResult();
        while (file != null) {
            file = provider.getNextResult();
        }
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("dir/dir2/test2.c"));
        expected.add(new File("dir/test.c"));
        assertThat(extractor.filesToParse, is(expected));
        
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testFindFilesToParseDirectory() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.extractor.files", "dir/");
        //config.setProperty("code.extractor.file_regex", "");
        config.setProperty("code.extractor.threads", "1");
        
        PseudoExtractor extractor = new PseudoExtractor(false);
        CodeModelProvider provider = new CodeModelProvider();
        provider.setExtractor(extractor);
        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        
        // read until the provider is empty
        SourceFile file = provider.getNextResult();
        while (file != null) {
            file = provider.getNextResult();
        }
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("dir/test.c"));
        expected.add(new File("dir/test2.c"));
        expected.add(new File("dir/dir2/test2.c"));
        assertThat(extractor.filesToParse, is(expected));
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testFindFilesToParseMixed() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        //config.setProperty("code.extractor.files", "");
        //config.setProperty("code.extractor.file_regex", "");
        config.setProperty("code.extractor.threads", "1");
        
        PseudoExtractor extractor = new PseudoExtractor(false);
        CodeModelProvider provider = new CodeModelProvider();
        provider.setExtractor(extractor);
        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        
        // read until the provider is empty
        SourceFile file = provider.getNextResult();
        while (file != null) {
            file = provider.getNextResult();
        }
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("test2.c"));
        expected.add(new File("dir/test.c"));
        expected.add(new File("dir/test2.c"));
        expected.add(new File("dir/dir2/test2.c"));
        assertThat(extractor.filesToParse, is(expected));
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testFindFilesToParseRegex() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        //config.setProperty("code.extractor.files", "");
        config.setProperty("code.extractor.file_regex", "[a-z]*\\.c");
        config.setProperty("code.extractor.threads", "1");
        
        PseudoExtractor extractor = new PseudoExtractor(false);
        CodeModelProvider provider = new CodeModelProvider();
        provider.setExtractor(extractor);
        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
        
        // read until the provider is empty
        SourceFile file = provider.getNextResult();
        while (file != null) {
            file = provider.getNextResult();
        }
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("dir/test.c"));
        assertThat(extractor.filesToParse, is(expected));
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testFindFilesToParseNonExistingFile() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.extractor.files", "non_existing.c");
        //config.setProperty("code.extractor.file_regex", "");
        config.setProperty("code.extractor.threads", "1");
        
        CodeModelProvider provider = new CodeModelProvider();
        provider.setExtractor(new PseudoExtractor(false));
        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        provider.start();
    }
   
    /**
     * Tests whether the extractor implicitly calls start().
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testStartWithoutStart() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNextResult(), notNullValue());
    }
    
}
