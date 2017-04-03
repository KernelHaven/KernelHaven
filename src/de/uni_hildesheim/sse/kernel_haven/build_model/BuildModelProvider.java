package de.uni_hildesheim.sse.kernel_haven.build_model;

import java.io.IOException;

import de.uni_hildesheim.sse.kernel_haven.SetUpException;
import de.uni_hildesheim.sse.kernel_haven.config.BuildExtractorConfiguration;
import de.uni_hildesheim.sse.kernel_haven.util.ExtractorException;
import de.uni_hildesheim.sse.kernel_haven.util.FormatException;
import de.uni_hildesheim.sse.kernel_haven.util.Logger;

/**
 * Provider for the Build model. This gives the analysis an interface to start
 * and query the results of the {@link IBuildModelExtractor}s. It also manages
 * proper synchronization between the analysis thread and the extractor thread.
 * 
 * @author Johannes
 * @author Marvin
 * @author Malek
 * @author Adam
 */
public class BuildModelProvider {

    private static final Logger LOGGER = Logger.get();

    /**
     * The BuildModel extractor.
     */
    private IBuildModelExtractor extractor;
    
    private IBuildExtractorFactory factory;

    private BuildExtractorConfiguration config;

    /**
     * The result. This is <code>null</code> until the extractor has provided
     * it. If this changes, then resultNotifier is notified.
     */
    private BuildModel result;

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
     * The cache to write and read into. Not <code>null</code> if caching is enabled (readCache || writeCache).
     */
    private BuildModelCache cache;
    
    /**
     * Creates a new Build model provider.
     */
    public BuildModelProvider() {
        resultNotifier = new Object();
    }

    /**
     * Tells this provider which extractor factory to use. This has to be called before
     * start() or get() is used.
     * 
     * @param factory
     *            The factory to create extractors for the build model.
     *            Can be null if we should only read from the cache.
     */
    public void setFactory(IBuildExtractorFactory factory) {
        this.factory = factory;
    }

    /**
     * Starts the extractor.
     */
    private void startExtractor() {
        LOGGER.logInfo("Starting build extractor...");
        
        if (extractor == null) {
            setException(new ExtractorException("Build extractor required but not specified."));
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
    public void start(BuildExtractorConfiguration config) {
        LOGGER.logInfo("Build extraction started");
        
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
            cache = new BuildModelCache(config.getCacheDir());
        }
        
        if (config.isCacheRead()) {
            LOGGER.logDebug("Trying to read build model cache");
            
            new Thread() {
                
                @Override
                public void run() {
                    Thread.currentThread().setName("BmCache");
                    
                    try {
                        BuildModel bm = cache.read();
                        
                        if (bm == null) {
                            // if the cache is not yet created, then start the extractor anyway
                            startExtractor();
                        } else {
                            LOGGER.logInfo("Read build model from cache");
                            setResult(bm);
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
     * Returns the build model. If the result is not ready yet, then this method
     * waits until the extractor is finished.
     * 
     * @return The {@see BuildModel}.
     * 
     * @throws ExtractorException
     *             If the extraction process failed or the timeout has been
     *             reached.
     */ 
    public BuildModel getResult() throws ExtractorException {
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
     *            The {@see BuildModel}. Not null.
     */
    public void setResult(BuildModel result) {
        synchronized (resultNotifier) {
            this.result = result;
            resultNotifier.notifyAll();
        }
        
        if (config.isCacheWrite()) {
            try {
                LOGGER.logInfo("Writing build model cache...");
                cache.write(result);
            } catch (IOException e) {
                LOGGER.logException("Exception while writing cache", e);
            }
        }
    }

}
