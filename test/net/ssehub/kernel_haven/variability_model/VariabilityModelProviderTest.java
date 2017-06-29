package net.ssehub.kernel_haven.variability_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;

/**
 * Tests the variability model provider.
 * 
 * @author Adam
 * @author Moritz
 * @author Manu
 * @author Kevin
 */
public class VariabilityModelProviderTest {
    
    /**
     * Inits the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }

    /**
     * Small class that implements {@link IVariabilityModelExtractor} for
     * testing purposes. This waits 1/2 second after start() is called and
     * returns an empty {@link VariabilityModel}.
     */
    private static class PseudoExtractor implements IVariabilityModelExtractor, Runnable, IVariabilityExtractorFactory {

        /** The provider. */
        private VariabilityModelProvider provider;

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
        public PseudoExtractor(VariabilityModelProvider provider, boolean throwException) {
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
                File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
                Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();
                set.add(new VariabilityVariable("ALPHA", "tristate", 1));
                set.add(new VariabilityVariable("BETA", "tristate", 5));
                set.add(new VariabilityVariable("GAMMA", "bool", 3));
                VariabilityModel vm = new VariabilityModel(dimacsFile, set);
                
                provider.setResult(vm);
            }
        }

        @Override
        public void setProvider(VariabilityModelProvider provider) {
            this.provider = provider;
        }

        @Override
        public IVariabilityModelExtractor create(VariabilityExtractorConfiguration config) {
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
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(new Properties()).getVariabilityConfiguration());
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
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(new Properties()).getVariabilityConfiguration());
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
        config.setProperty("variability.provider.timeout", "10");
        VariabilityModelProvider provider = new VariabilityModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getVariabilityConfiguration());

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
        config.setProperty("variability.provider.timeout", "1500");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getVariabilityConfiguration());
        assertThat(provider.getResult(), notNullValue());
        assertThat(extractor.extractor.stopCalled, is(false));
    }
    
    /**
     * Tests if the variability model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws ExtractorException, SetUpException {
        Properties config = new Properties();
        config.setProperty("cache_dir", new File("testdata/vmCaching/cache_valid").getAbsolutePath());
        config.setProperty("variability.provider.cache.read", "true");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(provider, true);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getVariabilityConfiguration());
        assertThat(provider.getResult(), notNullValue());
    }
    
    /**
     * Tests if the variability model is read from the cache.
     * 
     * @throws ExtractorException unwanted.
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws ExtractorException, SetUpException, IOException {
        File cacheDir = new File("testdata/vmCaching/tmp_cache");
        cacheDir.mkdir();
        
        // precondition: cache directory is empty
        assertThat(cacheDir.listFiles().length, is(0));
        
        Properties config = new Properties();
        config.setProperty("cache_dir", cacheDir.getAbsolutePath());
        config.setProperty("variability.provider.cache.write", "true");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(provider, false);
        provider.setFactory(extractor);

        provider.start(new TestConfiguration(config).getVariabilityConfiguration());
        assertThat(provider.getResult(), notNullValue());
        
        // synchronize with the writing, since it runs in parallel to us...
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        
        // test if cache is now not empty
        assertThat(cacheDir.listFiles().length, is(2));
        
        // cleanup
        Util.deleteFolder(cacheDir);
    }
    
}
