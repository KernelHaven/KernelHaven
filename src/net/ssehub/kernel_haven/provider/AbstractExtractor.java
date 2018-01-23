package net.ssehub.kernel_haven.provider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Abstract parent class for all extractors. This class speaks to the provider and handles stuff like multi-threading
 * and caching. Extractors only need to worry about implementing the abstract methods.
 *
 * @param <ResultType> The type of the result the extractor produces.
 *
 * @author Adam
 */
public abstract class AbstractExtractor<ResultType> {

    private static final Logger LOGGER = Logger.get();
    
    private boolean isRunning;
    
    private @NonNull Object isRunningMutex;
    
    private AbstractProvider<ResultType> provider;
    
    /**
     * Creates a new extractor.
     */
    public AbstractExtractor() {
        isRunningMutex = new Object();
    }
    
    /**
     * Initializes the extractor with the given configuration. This may be called multiple times with different
     * configurations, but never while the extractor is running.
     * 
     * @param config The configuration for the extractor.
     * 
     * @throws SetUpException If configuring the extractor fails.
     */
    protected abstract void init(@NonNull Configuration config) throws SetUpException;
    
    /**
     * Runs the extractor on a single target. This may potentially be called in parallel from multiple threads; thus,
     * it should be thread-safe.
     * 
     * @param target The target to run on.
     * @return The result of the extraction process. Should not be <code>null</code> (otherwise and exception is passed
     *      to the analysis instead).
     *      
     * @throws ExtractorException If the extraction process failed.
     */
    protected abstract @Nullable ResultType runOnFile(@NonNull File target) throws ExtractorException;
    
    /**
     * The name of the extractor. Used for naming threads.
     * 
     * @return The name of the extractor.
     */
    protected abstract @NonNull String getName();
    
    /**
     * Checks if the extractor is currently running.
     * 
     * @return Whether the extractor is running or not.
     */
    public final boolean isRunning() {
        synchronized (isRunningMutex) {
            return isRunning;
        }
    }
    
    /**
     * A worker thread that executes runOnFile() on the targets it gets from a queue.
     */
    private final class WorkerThread extends Thread {
        
        private @NonNull BlockingQueue<File> targets;
        
        /**
         * Creates a new worker thread.
         * 
         * @param name The name of the extractor.
         * @param number The number of this thread.
         * @param targets The queue to get targets from.
         */
        public WorkerThread(@NonNull String name, int number, @NonNull BlockingQueue<File> targets) {
            super(name + "Thread-" + number);
            this.targets = targets;
        }
        
        @Override
        public void run() {
            File target;
            
            while ((target = targets.get()) != null) {
                try {
                    ResultType result = null;
                    boolean readFromCache = false;
                    
                    
                    if (provider.readCache()) {
                        try {
                            result = provider.getCache().read(target);
                        } catch (FormatException | IOException e) {
                            LOGGER.logException("Invalid cache for file " + target.getPath(), e);
                        }
                    }
                    
                    if (result == null) {
                        LOGGER.logInfo("Starting extractor for " + target.getPath());
                        result = runOnFile(target);
                        
                    } else {
                        readFromCache = true;
                        LOGGER.logInfo("Read " + target.getPath() + " from cache");
                    }
                    
                    if (result == null) {
                        throw new ExtractorException("Extractor returned null");
                    }
                    
                    provider.addResult(result);
                    
                    if (provider.writeCache() && !readFromCache) {
                        try {
                            provider.getCache().write(result);
                            LOGGER.logInfo("Cache successfully written");
                            
                        } catch (IOException e) {
                            LOGGER.logException("Error writing cache for file " + target.getPath(), e);
                        }
                    }
                    
                } catch (ExtractorException e) {
                    provider.addException(e);
                }
            }
        }
        
    }
    
    /**
     * Runs the extractor asynchronously on the given list of targets. This potentially (depending on configuration)
     * spawns multiple threads that chew through the list of targets. For each result, setResult() or setException()
     * of the provider is called.
     * 
     * @param targets The targets to run on.
     */
    public final void run(@NonNull List<@NonNull File> targets) {
        synchronized (isRunningMutex) {
            this.isRunning = true;
        }
        
        new Thread(() -> {
            
            LOGGER.logInfo("Starting on " + targets.size() + " targets in "
                    + provider.getNumberOfThreads() + " threads");
           
            BlockingQueue<File> targetQueue = new BlockingQueue<>();
            for (File target : targets) {
                targetQueue.add(target);
            }
            targetQueue.end();
            
            List<WorkerThread> threads = new ArrayList<>(provider.getNumberOfThreads());
            
            for (int i = 1; i <= provider.getNumberOfThreads(); i++) {
                WorkerThread th = new WorkerThread(getName(), i, targetQueue);
                th.start();
                threads.add(th);
            }
            
            for (WorkerThread th : threads) {
                try {
                    th.join();
                } catch (InterruptedException e) {
                }
            }
            
            LOGGER.logInfo("All threads done");
            
            synchronized (isRunningMutex) {
                isRunning = false;
                provider.addResult(null);
            }
            
        }, getName() + "Thread").start();
    }

    /**
     * Sets the provider to pass the results to.
     * 
     * @param provider The provider.
     */
    public final void setProvider(@NonNull AbstractProvider<ResultType> provider) {
        this.provider = provider;
    }
    
}
