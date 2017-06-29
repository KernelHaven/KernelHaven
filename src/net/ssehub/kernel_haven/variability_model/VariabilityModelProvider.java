package net.ssehub.kernel_haven.variability_model;

import java.io.IOException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Logger;

/**
 * Provider for the variability model. This gives the analysis an interface to
 * start and query the results of the {@link IVariabilityModelExtractor}s. It
 * also manages proper synchronization between the analysis thread and the
 * extractor thread. Also Provides caching functionality.
 * 
 * @author Adam
 * @author Moritz
 * @author Manu
 * @author Johannes
 */
public class VariabilityModelProvider {
    
    private static final Logger LOGGER = Logger.get();

    /**
     * The extractor.
     */
    private IVariabilityModelExtractor extractor;
    
    private IVariabilityExtractorFactory factory;
    
    private VariabilityExtractorConfiguration config;

    /**
     * The result. This is <code>null</code> until the extractor has provided
     * it. If this changes, then resultNotifier is notified.
     */
    private VariabilityModel result;

    /**
     * If this is non-<code>null</code>, then the extractor has provided an
     * exception instead of an actual result. If this changes, then
     * resultNotifier is notified.
     */
    private ExtractorException exception;

    /**
     * Used for notifying the get() method that a new result or exception has
     * been set.
     */
    private Object resultNotifier;
    
    /**
     * The cache to write and read to. Not <code>null</code> if caching is enabled (readCache || writeCache).
     */
    private VariabilityModelCache cache;
    
//    /**
//     * If this flag is set to true, the cache will be read via the read() method.
//     */
//    private boolean readCache = false;
//    
//    /**
//     * If this flag is set to true, the cache will be written via the write() method.
//     */
//    private boolean writeCache = false;

    /**
     * Creates a new variability model provider.
     */
    public VariabilityModelProvider() {
        resultNotifier = new Object();
    }

    /**
     * Tells this provider which extractor factory to use. This has to be called before
     * start() or get() is used.
     * 
     * @param factory
     *            The factory to create extractors for the variability model.
     *            Can be null if we should only read from the cache.
     */
    public void setFactory(IVariabilityExtractorFactory factory) {
        this.factory = factory;
    }

    /**
     * Starts the extractor.
     */
    private void startExtractor() {
        LOGGER.logInfo("Starting variability extractor...");
        
        if (extractor == null) {
            setException(new ExtractorException("VM extractor required but not specified."));
        } else {
            extractor.start();
        }
    }
    
    /**
     * Starts the extraction process.
     * 
     * @param config The configuration for this execution. Do not change this after calling this method.
     *      Must not be <code>null</code>.
     */
    public void start(VariabilityExtractorConfiguration config) {
        LOGGER.logInfo("Variability model extraction started");
        
        this.config = config;
        
        if (factory != null) {
            try {
                this.extractor = factory.create(config);
            } catch (SetUpException e) {
                this.exception = new ExtractorException(e);
                return;
            }
            this.extractor.setProvider(this);
        }
        
        if (config.isCacheRead() || config.isCacheWrite()) {
            cache = new VariabilityModelCache(config.getCacheDir());
        }
        
        if (config.isCacheRead()) {
            LOGGER.logDebug("Trying to read variability model cache");
            
            new Thread() {
                
                @Override
                public void run() {
                    Thread.currentThread().setName("VmCache");
                    
                    try {
                        VariabilityModel vm = cache.read();
                        
                        if (vm == null) {
                            // if the cache is not yet created, then start the extractor anyway
                            startExtractor();
                        } else {
                            LOGGER.logInfo("Read variability model from cache");
                            setResult(vm);
                        }
                        
                    } catch (FormatException | IOException e) {
                        setException(new ExtractorException(e));
                    }
                }
                
            }.start();
            
        } else {
            startExtractor();
        }
    }

    /**
     * Returns the variability model. If the result is not ready yet, then this
     * method waits until the extractor is finished.
     * 
     * @return The {@link VariabilityModel}.
     * 
     * @throws ExtractorException
     *             If the extraction process failed or the timeout has been
     *             reached.
     */
    public VariabilityModel getResult() throws ExtractorException {
        synchronized (resultNotifier) {
            if (result == null && exception == null) {
                boolean success = false;
                while (!success) {
                    try {
                        resultNotifier.wait(config.getProviderTimeout());
                        success = true;
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (exception != null) {
                throw exception;
            }

            if (result == null) {
                if (extractor != null) {
                    extractor.stop();
                }
                throw new ExtractorException("Did not get a result in time (timeout = "
                        + config.getProviderTimeout() + ")");
            }

            return result;
        }
    }

    /**
     * Tells the provider that the extraction process could not finish. This
     * method should be called by the extractor if the extraction process
     * failed.
     * 
     * @param exception
     *            The exception to throw to anyone that wants the result. Not
     *            null.
     */
    public void setException(ExtractorException exception) {
        synchronized (resultNotifier) {
            this.exception = exception;
            resultNotifier.notifyAll();
        }
    }

    /**
     * Sets the result to be returned on later calls of getResult(). This method
     * should be called by the extractor once it is finished.
     * 
     * @param result
     *            The {@link VariabilityModel}. Not null.
     */
    public void setResult(VariabilityModel result) {
        synchronized (resultNotifier) {
            this.result = result;
            resultNotifier.notifyAll();
        }
        
        if (config.isCacheWrite()) {
            try {
                LOGGER.logInfo("Writing variability model cache...");
                cache.write(result);
            } catch (IOException e) {
                LOGGER.logException("Exception while writing cache", e);
            }
        }
    }

}
