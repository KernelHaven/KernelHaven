package net.ssehub.kernel_haven.build_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.TestConfiguration;
import net.ssehub.kernel_haven.config.BuildExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;

/**
 * Tests the build model provider.
 * 
 * @author Marvin
 * @author Johannes
 * @author Malek
 * @author Adam
 * @author Kevin
 */
public class BuildModelProviderTest {

    /**
     * Initiates the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }

    /**
     * Small class that implements {@see AbstractBuildModelExtractor} for testing
     * purposes. This waits 1/2 second after start() is called and returns an
     * empty {@link BuildModel}.
     */
    
    private static class PseudoExtractor extends AbstractBuildModelExtractor {

        /** The throw exception. */
        private boolean throwException;
        
        /**
         * Creates a new pseudo extractor.
         * 
         * @param throwException Whether to throw an exception or return a normal result.
         */
        public PseudoExtractor(boolean throwException) {
            this.throwException = throwException;
        }
        
        @Override
        protected void init(BuildExtractorConfiguration config) throws SetUpException {
            
        }

        @Override
        protected BuildModel runOnFile(File target) throws ExtractorException {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            
            if (throwException) {
                throw new ExtractorException("Test exception");
            } else {
                return new BuildModel();
            }

        }

        @Override
        protected String getName() {
            return "PseudoBuildExtractor";
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
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        Properties props = new Properties();
        props.setProperty("source_tree", "source/tree");
        
        provider.setConfig(new TestConfiguration(props).getBuildConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextException(), nullValue());
    }

    /**
     * Tests whether exceptions are properly passed to get().
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test()
    public void testExceptions() throws SetUpException {
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        Properties props = new Properties();
        props.setProperty("source_tree", "source/tree");

        provider.setConfig(new TestConfiguration(props).getBuildConfiguration());
        provider.start();

        assertThat(provider.getNextResult(), nullValue());
        assertThat(provider.getNextException(), notNullValue());
    }

    /**
     * Test whether timeout correctly terminates the waiting process with an
     * exception.
     * 
     * @throws SetUpException
     *             unwanted
     */
    @Test()
    public void testShortTimeout() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("build.provider.timeout", "10");
        BuildModelProvider provider = new BuildModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getBuildConfiguration());
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
        config.setProperty("source_tree", "source/tree");
        config.setProperty("build.provider.timeout", "1500");
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getBuildConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextException(), nullValue());
    }
    
    /**
     * Tests if the build model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("cache_dir", new File("testdata/bmCaching/cache_valid").getAbsolutePath());
        config.setProperty("build.provider.cache.read", "true");
        BuildModelProvider provider = new BuildModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getBuildConfiguration());
        provider.start();
        assertThat(provider.getNextResult(), notNullValue());
    }
    
    /**
     * Tests if the build model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws SetUpException, IOException {
        File cacheDir = new File("testdata/bmCaching/tmp_cache");
        cacheDir.mkdir();
        
        // precondition: cache directory is empty
        assertThat(cacheDir.listFiles().length, is(0));
        
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("cache_dir", cacheDir.getAbsolutePath());
        config.setProperty("build.provider.cache.write", "true");
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getBuildConfiguration());
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
     * Tests whether the extractor implicitly calls start().
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testStartWithoutStart() throws SetUpException {
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        Properties props = new Properties();
        props.setProperty("source_tree", "source/tree");
        
        provider.setConfig(new TestConfiguration(props).getBuildConfiguration());
        assertThat(provider.getNextResult(), notNullValue());
        assertThat(provider.getNextException(), nullValue());
    }
    
}
