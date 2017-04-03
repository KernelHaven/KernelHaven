package de.uni_hildesheim.sse.kernel_haven.build_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_hildesheim.sse.kernel_haven.SetUpException;
import de.uni_hildesheim.sse.kernel_haven.TestConfiguration;
import de.uni_hildesheim.sse.kernel_haven.config.BuildExtractorConfiguration;
import de.uni_hildesheim.sse.kernel_haven.util.ExtractorException;
import de.uni_hildesheim.sse.kernel_haven.util.Logger;
import de.uni_hildesheim.sse.kernel_haven.util.Util;

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
     * Small class that implements {@see IBuildyModelExtractor} for testing
     * purposes. This waits 1/2 second after start() is called and returns an
     * empty {@link BuildModel}.
     */
    
    private static class PseudoExtractor implements IBuildModelExtractor, Runnable, IBuildExtractorFactory {

        /** The provider. */
        private BuildModelProvider provider;

        /** The throw exception. */
        private boolean throwException;
        
        private boolean stopCalled;
        
        private PseudoExtractor extractor;

        /**
         * Creates a new PseudoExtractor.
         * 
         * @param provider
         *            The provider to notify about the result.
         * @param throwException
         *            Whether an exception should be returned instead of a
         *            normal result.
         */
        public PseudoExtractor(BuildModelProvider provider, boolean throwException) {
            this.provider = provider;
            this.throwException = throwException;
        }

        @Override
        public void start() {
            new Thread(this).start();
        }
        
        @Override
        public void stop() {
            stopCalled = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (throwException) {
                provider.setException(new ExtractorException("Test exception"));
            } else {

                provider.setResult(new BuildModel());
            }
        }

        @Override
        public void setProvider(BuildModelProvider provider) {
            this.provider = provider;
        }

        @Override
        public IBuildModelExtractor create(BuildExtractorConfiguration config) {
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
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(new Properties()).getBuildConfiguration());
        assertThat(provider.getResult(), notNullValue());
    }

    /**
     * Tests whether exceptions are properly passed to get().
     * 
     * @throws ExtractorException
     *             wanted.
     * @throws SetUpException
     *             unwanted.
     */
    @Test(expected = ExtractorException.class)
    public void testExceptions() throws ExtractorException, SetUpException {
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(new Properties()).getBuildConfiguration());
        provider.getResult();
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
        config.setProperty("build.provider.timeout", "10");
        BuildModelProvider provider = new BuildModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getBuildConfiguration());

        try {
            provider.getResult();
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
        config.setProperty("build.provider.timeout", "1500");
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getBuildConfiguration());
        assertThat(provider.getResult(), notNullValue());
        assertThat(extractor.extractor.stopCalled, is(false));
    }

    /**
     * Tests if the build model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("cache_dir", new File("testdata/bmCaching/cache_valid").getAbsolutePath());
        config.setProperty("build.provider.cache.read", "true");
        BuildModelProvider provider = new BuildModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getBuildConfiguration());
        assertThat(provider.getResult(), notNullValue());
    }
    
    /**
     * Tests if the build model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws ExtractorException, SetUpException, IOException {
        File cacheDir = new File("testdata/bmCaching/tmp_cache");
        cacheDir.mkdir();
        
        // precondition: cache directory is empty
        assertThat(cacheDir.listFiles().length, is(0));
        
        Properties config = new Properties();
        config.setProperty("cache_dir", cacheDir.getAbsolutePath());
        config.setProperty("build.provider.cache.write", "true");
        BuildModelProvider provider = new BuildModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getBuildConfiguration());
        assertThat(provider.getResult(), notNullValue());
        
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
    
}
