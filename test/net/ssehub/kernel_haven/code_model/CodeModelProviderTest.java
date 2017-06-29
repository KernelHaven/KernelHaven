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
import net.ssehub.kernel_haven.util.BlockingQueue;
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
     * Small class that implements {@see ICodeModelExtractor} for testing
     * purposes. This waits 1/2 second after start() is called and returns four empty {@link SourceFile}s.
     */
    private static class PseudoExtractor implements ICodeModelExtractor, Runnable, ICodeExtractorFactory {

        /** The provider. */
        private CodeModelProvider provider;

        /** The throw exception. */
        private boolean throwException;
        
        private boolean stopCalled;
        
        private PseudoExtractor extractor;
        
        private BlockingQueue<File> filesToParse;
        
        /**
         * Creates a new PseudoExtractor.
         * 
         * @param provider
         *            The provider to notify about the result.
         * @param throwException
         *            Whether an exception should be returned instead of a
         *            normal result.
         */
        public PseudoExtractor(CodeModelProvider provider, boolean throwException) {
            this.provider = provider;
            this.throwException = throwException;
        }

        @Override
        public void start(BlockingQueue<File> filesToParse) {
            this.filesToParse = filesToParse;
            new Thread(this).start();
        }
        
        @Override
        public void stop() {
            stopCalled = true;
        }

        @Override
        public void run() {
            
            File file;
            while ((file = filesToParse.get()) != null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                if (throwException) {
                    provider.addException(new CodeExtractorException(file, "Test exception"));
                } else {
                    provider.addResult(new SourceFile(file));                    
                }
            }
            
            provider.addResult(null);
        }

        @Override
        public void setProvider(CodeModelProvider provider) {
            this.provider = provider;
        }

        @Override
        public ICodeModelExtractor create(CodeExtractorConfiguration config) {
            PseudoExtractor extractor = new PseudoExtractor(provider, throwException);
            this.extractor = extractor;
            return extractor;
        }

    }

    /**
     * Tests whether the set() and get() Methods for the result properly wait
     * for each other.
     * 
     * @throws ExtractorException
     *             unwanted.
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testGetSetSynchronization() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNext(), notNullValue());
    }

    /**
     * Tests whether exceptions are properly passed to get().
     * 
     * @throws ExtractorException
     *             unwanted.
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testExceptions() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        
        assertThat(provider.getNext(), nullValue());
        
        CodeExtractorException exception = provider.getNextException();
        assertThat(exception, notNullValue());
        assertThat(exception.getCausingFile(), is(new File("test.c")));
        assertThat(provider.getNextException(), nullValue());
    }

    /**
     * Test whether timeout correctly terminates the waiting process with an
     * exception.
     * 
     * @throws SetUpException
     *             unwanted
     * @throws ExtractorException
     *             wanted
     */
    @Test(expected = ExtractorException.class)
    public void testShortTimeout() throws SetUpException, ExtractorException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.provider.timeout", "10");
        CodeModelProvider provider = new CodeModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());

        try {
            provider.getNext();
        } finally {
            assertThat(extractor.extractor.stopCalled, is(true));
        }
    }

    /**
     * Tests whether too long timeout is not triggered.
     * 
     * @throws ExtractorException
     *             unwanted
     * @throws SetUpException
     *             unwanted
     */
    @Test
    public void testLongTimeout() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.provider.timeout", "1500");
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNext(), notNullValue());
        assertThat(extractor.extractor.stopCalled, is(false));
    }
    
    /**
     * Tests whether multiple generated results are read properly.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     */
    public void testGetMultiple() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNext(), notNullValue());
        assertThat(provider.getNext(), notNullValue());

        // sleep a bit, to make sure that multiple results are in the queue
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        
        assertThat(provider.getNext(), notNullValue());
        assertThat(provider.getNext(), notNullValue());
        assertThat(provider.getNext(), nullValue());
    }

    /**
     * Tests if the code model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("code.extractor.files", "test.c");
        config.setProperty("code.extractor.file_pattern", ".*");
        config.setProperty("code.provider.cache.read", "true");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("cache_dir", new File("testdata/cmCaching/cache_valid").getAbsolutePath());
        CodeModelProvider provider = new CodeModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNext(), notNullValue());
    }
    
    /**
     * Tests if the code model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws ExtractorException, SetUpException, IOException {
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
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getCodeConfiguration());
        assertThat(provider.getNext(), notNullValue());
        
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
        
        CodeModelProvider extractor = new CodeModelProvider();
        extractor.start(new TestConfiguration(config).getCodeConfiguration());
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("dir/dir2/test2.c"));
        expected.add(new File("dir/test.c"));
        assertThat(extractor.getFilesToParse(), is(expected));
        
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
        
        CodeModelProvider extractor = new CodeModelProvider();
        extractor.start(new TestConfiguration(config).getCodeConfiguration());
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("dir/test.c"));
        expected.add(new File("dir/test2.c"));
        expected.add(new File("dir/dir2/test2.c"));
        assertThat(extractor.getFilesToParse(), is(expected));
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
        
        CodeModelProvider extractor = new CodeModelProvider();
        extractor.start(new TestConfiguration(config).getCodeConfiguration());
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("test2.c"));
        expected.add(new File("dir/test.c"));
        expected.add(new File("dir/test2.c"));
        expected.add(new File("dir/dir2/test2.c"));
        assertThat(extractor.getFilesToParse(), is(expected));
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
        
        CodeModelProvider extractor = new CodeModelProvider();
        extractor.start(new TestConfiguration(config).getCodeConfiguration());
        
        Set<File> expected = new HashSet<>();
        expected.add(new File("test.c"));
        expected.add(new File("dir/test.c"));
        assertThat(extractor.getFilesToParse(), is(expected));
    }
    
    /**
     * Tests the method that finds the files to parse from a properties file.
     * 
     * @throws ExtractorException wanted. 
     * @throws SetUpException unwanted.
     */
    @Test(expected = ExtractorException.class)
    public void testFindFilesToParseNonExistingFile() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("resource_dir", "testdata");
        config.setProperty("source_tree", SOURCE_TREE.getAbsolutePath());
        config.setProperty("code.extractor.files", "non_existing.c");
        //config.setProperty("code.extractor.file_regex", "");
        config.setProperty("code.extractor.threads", "1");
        
        CodeModelProvider provider = new CodeModelProvider();
        provider.start(new TestConfiguration(config).getCodeConfiguration());
        provider.getNext();
        
    }
   
}
