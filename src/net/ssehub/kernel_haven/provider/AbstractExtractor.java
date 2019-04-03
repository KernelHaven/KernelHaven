/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import net.ssehub.kernel_haven.util.ProgressLogger;
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

    protected static final Logger LOGGER = Logger.get();
    
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
        
        private @NonNull ProgressLogger progress;
        
        /**
         * Creates a new worker thread.
         * 
         * @param name The name of the extractor.
         * @param number The number of this thread.
         * @param targets The queue to get targets from.
         * @param progress A {@link ProgressLogger} to notfiy about finished items.
         */
        public WorkerThread(@NonNull String name, int number, @NonNull BlockingQueue<File> targets,
                @NonNull ProgressLogger progress) {
            super(name + "-" + number);
            this.targets = targets;
            this.progress = progress;
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
                        LOGGER.logDebug("Starting extractor for " + target.getPath());
                        result = runOnFile(target);
                        
                    } else {
                        readFromCache = true;
                        LOGGER.logDebug("Read " + target.getPath() + " from cache");
                    }
                    
                    if (result == null) {
                        throw new ExtractorException("Extractor returned null");
                    }
                    
                    provider.addResult(result);
                    
                    if (provider.writeCache() && !readFromCache) {
                        try {
                            provider.getCache().write(result);
                            LOGGER.logDebug("Cache successfully written");
                            
                        } catch (IOException e) {
                            LOGGER.logException("Error writing cache for file " + target.getPath(), e);
                        }
                    }
                    
                } catch (ExtractorException e) {
                    provider.addException(e);
                }
                
                progress.processedOne();
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
            
            LOGGER.logStatus("Starting on ", targets.size(), " targets in ", provider.getNumberOfThreads(), " threads");
            ProgressLogger progress = new ProgressLogger(getName(), targets.size());
           
            BlockingQueue<File> targetQueue = new BlockingQueue<>();
            for (File target : targets) {
                targetQueue.add(target);
            }
            targetQueue.end();
            
            List<WorkerThread> threads = new ArrayList<>(provider.getNumberOfThreads());
            
            for (int i = 1; i <= provider.getNumberOfThreads(); i++) {
                WorkerThread th = new WorkerThread(getName(), i, targetQueue, progress);
                th.start();
                threads.add(th);
            }
            
            for (WorkerThread th : threads) {
                try {
                    th.join();
                } catch (InterruptedException e) {
                }
            }
            
            progress.close();
            
            synchronized (isRunningMutex) {
                isRunning = false;
                provider.addResult(null);
            }
            
        }, getName()).start();
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
