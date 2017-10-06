package net.ssehub.kernel_haven.variability_model;

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
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
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
     * Small class that implements {@link AbstractVariabilityModelExtractor} for
     * testing purposes. This waits 1/2 second after start() is called and
     * returns an empty {@link VariabilityModel}.
     */
    private static class PseudoExtractor extends AbstractVariabilityModelExtractor {

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
        protected void init(VariabilityExtractorConfiguration config) throws SetUpException {
        }

        @Override
        protected VariabilityModel runOnFile(File target) throws ExtractorException {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (throwException) {
                throw new ExtractorException("Test exception");
            } else {
                File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
                Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();
                set.add(new VariabilityVariable("ALPHA", "tristate", 1));
                set.add(new VariabilityVariable("BETA", "tristate", 5));
                set.add(new VariabilityVariable("GAMMA", "bool", 3));
                VariabilityModel vm = new VariabilityModel(dimacsFile, set);
                
                
                return vm;
            }
        }

        @Override
        protected String getName() {
            return "PseudoVariabilityExtractor";
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
        config.setProperty("source_tree", "source/tree");
        
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();
        assertThat(provider.getResult(), notNullValue());
        assertThat(provider.getException(), nullValue());
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
        config.setProperty("source_tree", "source/tree");
        
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();

        assertThat(provider.getResult(), nullValue());
        assertThat(provider.getException(), notNullValue());
    }

    /**
     * Test whether timeout correctly terminates the waiting process with an
     * exception.
     * 
     * @throws SetUpException
     *             unwanted
     */
    @Test
    public void testShortTimeout() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("variability.provider.timeout", "10");
        VariabilityModelProvider provider = new VariabilityModelProvider();

        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();


        assertThat(provider.getResult(), nullValue());
        assertThat(provider.getException(), notNullValue());
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
        config.setProperty("variability.provider.timeout", "1500");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();
        
        
        assertThat(provider.getResult(), notNullValue());
        assertThat(provider.getException(), nullValue());
    }
    
    /**
     * Tests if the variability model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCacheRead() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("cache_dir", new File("testdata/vmCaching/cache_valid").getAbsolutePath());
        config.setProperty("variability.provider.cache.read", "true");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        // if the provider were to start, then we get an exception and fail;
        // we expect the cache to work so the provider would never start
        PseudoExtractor extractor = new PseudoExtractor(true);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();
        assertThat(provider.getResult(), notNullValue());
        assertThat(provider.getException(), nullValue());
    }
    
    /**
     * Tests if the variability model is read from the cache.
     * 
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testCacheWrite() throws SetUpException, IOException {
        File cacheDir = new File("testdata/vmCaching/tmp_cache");
        cacheDir.mkdir();
        
        // precondition: cache directory is empty
        assertThat(cacheDir.listFiles().length, is(0));
        
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        config.setProperty("cache_dir", cacheDir.getAbsolutePath());
        config.setProperty("variability.provider.cache.write", "true");
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        provider.start();
        assertThat(provider.getResult(), notNullValue());
        assertThat(provider.getException(), nullValue());
        
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
    
    /**
     * Tests whether the extractor implicitly calls start().
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testStartWithoutStart() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("source_tree", "source/tree");
        
        VariabilityModelProvider provider = new VariabilityModelProvider();
        PseudoExtractor extractor = new PseudoExtractor(false);
        provider.setExtractor(extractor);

        provider.setConfig(new TestConfiguration(config).getVariabilityConfiguration());
        assertThat(provider.getResult(), notNullValue());
        assertThat(provider.getException(), nullValue());
    }
    
}
