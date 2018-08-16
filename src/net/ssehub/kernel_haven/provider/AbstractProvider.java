package net.ssehub.kernel_haven.provider;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Abstract parent class for all providers. This class handles basic functionality of the providers such as
 * synchronization and communication with the extractors.
 * 
 * @param <ResultType> The type of result that the extractor produces.
 *
 * @author Adam
 */
public abstract class AbstractProvider<ResultType> {

    /**
     * The configuration for the provider and extractor.
     */
    protected Configuration config;
    
    private AbstractExtractor<ResultType> extractor;
    
    private @NonNull BlockingQueue<ResultType> resultQueue;
    
    private @NonNull BlockingQueue<ExtractorException> exceptionQueue;
    
    private AbstractCache<ResultType> cache;

    /**
     * Creates a new provider.
     */
    public AbstractProvider() {
        resultQueue = new BlockingQueue<>();
        exceptionQueue = new BlockingQueue<>();
    }
    
    /**
     * Creates a list of targets that the extractor should run on. On single-result providers (such as variability
     * or build model), this may only contain one item and will probably be ignored by the extractors. For the code
     * model, however, this will probably list every single source file that needs to be parsed.
     * 
     * @return The list of targets to run the extractor on.
     * 
     * @throws SetUpException If generating the list of targets fails (e.g. due to configuration problems).
     */
    protected abstract @NonNull List<@NonNull File> getTargets() throws SetUpException;
    
    /**
     * Specifies the timeout in milliseconds until waiting for the result of the extractor is aborted and an exception
     * is generated instead.
     * 
     * @return The timeout in milliseconds. 0 means no timeout is used.
     */
    protected abstract long getTimeout();
    
    /**
     * Creates a cache to use for the extractor.
     * 
     * @return The cache to use to store and read results from / to.
     */
    protected abstract @NonNull AbstractCache<ResultType> createCache();
    
    /**
     * Whether to try and read from the cache before running the extractor.
     * 
     * @return Whether to try and read from the cache before running the extractor.
     */
    public abstract boolean readCache();
    
    /**
     * Whether to write results to the cache or not.
     * 
     * @return Whether to write results to the cache or not.
     */
    public abstract boolean writeCache();
    
    /**
     * Specifies the number of threads that should execute the extractor in parallel.
     * 
     * @return The number of threads to use for the extractor.
     */
    public abstract int getNumberOfThreads();
    
    /**
     * Tells this provider which extractor to use.
     * 
     * @param extractor The extractor to use.
     */
    public void setExtractor(@NonNull AbstractExtractor<ResultType> extractor) {
        this.extractor = extractor;
        this.extractor.setProvider(this);
    }

    /**
     * Sets or changes the configuration for the extractor. This may be called multiple times with different
     * configuration, but never while the extractor is running.
     * 
     * @param config The configuration for the extractor.
     * 
     * @throws SetUpException If the extractor is currently running or initializing the extractor with this
     *      configuration failed.
     */
    public void setConfig(@NonNull Configuration config) throws SetUpException {
        if (extractor.isRunning()) {
            throw new SetUpException("Can't change config while extractor is running");
        }
        
        this.config = config;
        extractor.init(config);
        
        this.cache = createCache();
    }
    
    /**
     * Retrieves the cache to use.
     * 
     * @return The cache to use.
     */
    public @NonNull AbstractCache<ResultType> getCache() {
        AbstractCache<ResultType> cache = this.cache;
        if (cache == null) {
            throw new RuntimeException("setConfig() not called before getCache()");
        }
        return cache;
    }
    
    /**
     * Starts the extraction process. Calling this method clears the internal result and exception queues.
     * 
     * @throws SetUpException If the extractor is already running, the configuration has not been set yet, or creation
     *      of the target list fails.
     */
    public void start() throws SetUpException {
        if (extractor.isRunning()) {
            throw new SetUpException("Extractor already running");
        }
        
        if (config == null) {
            throw new SetUpException("Extractor not yet initialized");
        }
        
        resultQueue = new BlockingQueue<>();
        exceptionQueue = new BlockingQueue<>();

        try {
            extractor.run(getTargets());
        } catch (SetUpException e) {
            addResult(null); // signal that no more results are going to be sent (since starting the extractor failed)
            throw e;
        }
    }
    
    /**
     * Adds a new result to this provider. Pass <code>null</code> to signal that the extraction process is finished.
     * 
     * @param result The result of the extractor to add.
     */
    public void addResult(@Nullable ResultType result) {
        if (result == null) {
            resultQueue.end();
            exceptionQueue.end();
        } else {
            resultQueue.add(result);
        }
    }
    
    /**
     * Adds a new exception to this provider.
     * 
     * @param exception The exception to add.
     */
    public void addException(@NonNull ExtractorException exception) {
        exceptionQueue.add(exception);
    }
    
    /**
     * Starts the extractor if its not already running and there is no result. This should be called in each get()
     * method to make sure that we never endlessly wait for a not-started extractor.
     */
    private void startExtractorIfNotRunning() {
        // check if the extractor is currently not running and whether it didn't run in the past
        // when an extractor has finished in the past, then the result queue is set to end()
        if (!extractor.isRunning() && !resultQueue.isEnd()) {
            try {
                start();
            } catch (SetUpException e) {
                // we can't properly handle the SetUpException here, since this extractor is started "implicitly" after
                // the set up phase
                Logger.get().logException("Can't start extractor on demand", e);
            }
        }
    }
    
    /**
     * Returns the current result. This does not advance the internal result queue. If there is no result yet, then this
     * method waits until there is one.
     * 
     * @return The result that the extractor created. <code>null</code> if there is no result left in the queue or
     *      the timeout for waiting has been reached.
     */
    public @Nullable ResultType getResult() {
        startExtractorIfNotRunning();
        
        ResultType result = null;
        
        try {
            result = resultQueue.peek(getTimeout());
        } catch (TimeoutException e) {
            addException(new ExtractorException("Timeout reached: Waited longer than " + getTimeout()
                    + " ms on extractor result"));
        }
        
        return result;
    }
    
    /**
     * Returns the current result. This advances the internal result queue (i.e. removes the result from it). If there
     * is no result yet, then this method waits until there is one.
     * 
     * @return The result that the extractor created. <code>null</code> if there is no result left in the queue or
     *      the timeout for waiting has been reached.
     */
    public @Nullable ResultType getNextResult() {
        startExtractorIfNotRunning();
        
        ResultType result = null;
        
        try {
            result = resultQueue.get(getTimeout());
        } catch (TimeoutException e) {
            addException(new ExtractorException("Timeout reached: Waited longer than " + getTimeout()
                    + " ms on extractor result"));
        }
        
        return result;
    }
    
    /**
     * Returns the queue that contains all results created by the extractor.
     * 
     * @return The result queue.
     */
    public @NonNull BlockingQueue<ResultType> getResultQueue() {
        startExtractorIfNotRunning();
        
        return resultQueue;
    }
    
    /**
     * Returns the current exception. This does not advance the internal exception queue. If there is no exception yet,
     * then this method waits until there is one.
     * 
     * @return The exception that the extractor created. <code>null</code> if there is no exception left in the queue or
     *      the timeout for waiting has been reached.
     */
    public @Nullable ExtractorException getException() {
        return exceptionQueue.peek();
    }
    
    /**
     * Returns the current exception. This advances the internal exception queue (i.e. removes the exception from it).
     * If there is no exception yet, then this method waits until there is one.
     * 
     * @return The exception that the extractor created. <code>null</code> if there is no exception left in the queue or
     *      the timeout for waiting has been reached.
     */
    public @Nullable ExtractorException getNextException() {
        return exceptionQueue.get();
    }
    
}
