package net.ssehub.kernel_haven.analysis;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.ProgressLogger;
import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A component of an analysis. Multiple of these can be combined together, to form an analysis pipeline.
 *
 * @param <O> The result type of this component.
 * 
 * @author Adam
 */
public abstract class AnalysisComponent<O> {
    
    /**
     * Shorthand for the global singleton logger.
     */
    protected static final Logger LOGGER = Logger.get();
    
    /**
     * Periodically logs the size of the result queues for all {@link AnalysisComponent}s.
     * 
     * TODO: This is a temporary debug measure.
     */
    private static class ResultSizeLogger extends Thread {

        private static final boolean ENABLED = false;
        
        private static final int LOG_THRESHHOLD = 10;
        
        private static final int PERIOD = 5000;
        
        private List<AnalysisComponent<?>> components;
        
        private List<AnalysisComponent<?>> doneComponents;
        
        /**
         * Creates this logger. Private because there should be only one singleton instance.
         */
        private ResultSizeLogger() {
            components = new LinkedList<>();
            doneComponents = new LinkedList<>();
        }
        
        /**
         * Logs the size of all result queues. Periodically called by run().
         */
        private synchronized void logCurrentSizes() {
            List<String> msg = new ArrayList<>(Math.max(components.size(), doneComponents.size()) + 1);
            
            msg.add("Currently running components:");
            for (AnalysisComponent<?> component : components) {
                int size = component.results.getCurrentSize();
                if (size > LOG_THRESHHOLD) {
                    String line = "\t- " + component.getResultName() + ": " + size;
                    if (msg.get(msg.size() - 1).startsWith(line)) {
                        msg.set(msg.size() - 1, line + " ++");
                    } else {
                        msg.add(line);
                    }
                }
            }
            if (msg.size() > 1) {
                LOGGER.logInfo(msg.toArray(new String[0]));
            } else {
                LOGGER.logInfo("No running components with >10 results");
            }
            msg.clear();
            
            msg.add("Finished components:");
            for (AnalysisComponent<?> component : components) {
                int size = component.results.getCurrentSize();
                if (size > LOG_THRESHHOLD) {
                    String line = "\t- " + component.getResultName() + ": " + size;
                    if (msg.get(msg.size() - 1).startsWith(line)) {
                        msg.set(msg.size() - 1, msg.get(msg.size() - 1) + " ++");
                    } else {
                        msg.add(line);
                    }
                }
            }
            if (msg.size() > 1) {
                LOGGER.logInfo(msg.toArray(new String[0]));
            } else {
                LOGGER.logInfo("No finished components with >10 results");
            }
        }
        
        /**
         * Registers a newly create {@link AnalysisComponent}.
         * 
         * @param component The component that was newly created.
         */
        public synchronized void registerComponent(AnalysisComponent<?> component) {
            if (ENABLED) {
                components.add(component);
            }
        }
        
        /**
         * Removes an {@link AnalysisComponent} that is done.
         * 
         * @param component The component that is done.
         */
        public synchronized void removeComponent(AnalysisComponent<?> component) {
            if (ENABLED) {
                components.remove(component);
                doneComponents.add(component);
            }
        }
        
        @Override
        public synchronized void start() {
            if (ENABLED) {
                this.setDaemon(true);
                this.setName("ComponentResultSizeLogger");
                super.start();
            }
        }
        
        @Override
        public void run() {
            while (true) {
                
                logCurrentSizes();
                
                try {
                    Thread.sleep(PERIOD);
                } catch (InterruptedException e) {
                }
            }
        }
        
    }
    
    private static final ResultSizeLogger RESULZ_SIZE_LOGGER;
    
    static {
        RESULZ_SIZE_LOGGER = new ResultSizeLogger();
        RESULZ_SIZE_LOGGER.start();
    }
    
    private @NonNull BlockingQueue<O> results;
    
    private boolean logResults;
    
    private ITableWriter out;
    
    private boolean started;
    
    private long tStart;
    
    private @Nullable ProgressLogger progressLogger;
    
    /**
     * Creates a new analysis component.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisComponent(@NonNull Configuration config) {
        results = new BlockingQueue<>();
        RESULZ_SIZE_LOGGER.registerComponent(this);
        
        setLogResults(config.getValue(DefaultSettings.ANALYSIS_COMPONENTS_LOG).contains(getClass().getSimpleName()));
    }
    
    /**
     * Changes whether this component should log its results or not. Results are logged to console and to the 
     * {@link ITableCollection} provided by the {@link PipelineAnalysis#getInstance()}.
     * This method should not be called once this component has started.
     * 
     * @param logResults Whether to log results or not.
     */
    public final void setLogResults(boolean logResults) {
        this.logResults = logResults;
    }
    
    /**
     * Starts a new thread that executes this analysis component. Only the first call to this method will start this
     * component. Subsequent calls do nothing.
     */
    protected final synchronized void start() {
        if (!started) {
            if (logResults) {
                try {
                    out = PipelineAnalysis.getInstance().getResultCollection().getWriter(getResultName());
                } catch (IOException e) {
                    LOGGER.logExceptionWarning("Can't create intermediate output file", e);
                }
            }
            
            Thread th = new Thread(() -> {
                if (!isInternalHelperComponent()) {
                    LOGGER.logInfo("Analysis component " + getClass().getSimpleName() + " starting");
                    progressLogger = new ProgressLogger(notNull(getClass().getSimpleName()));
                }
                
                try {
                    execute();
                } finally {
                    done();
                }
            }, getClass().getSimpleName());
            
            //don't cause a deadlock with accidentally created AnalysisComponents that will never finish
            th.setDaemon(true);
            th.start();
            tStart = System.currentTimeMillis();
            
            started = true;
        }
    }
    
    /**
     * Retrieves the next result that this component creates. If none is currently available, this method blocks until
     * the result is ready.
     * 
     * @return The next result. <code>null</code> if this analysis is done and does not produce any results anymore.
     */
    public final @Nullable O getNextResult() {
        start(); // make sure we are started
        return results.get();
    }
    
    /**
     * Adds a result to be retrieved by the next component.
     * 
     * @param result The result to pass to the next component. Must not be <code>null</code>.
     */
    protected final void addResult(@NonNull O result) {
        results.add(result);
        
        if (progressLogger != null) {
            progressLogger.oneDone();
        }
        
        if (logResults) {
            LOGGER.logDebug("Analysis component " + getClass().getSimpleName() + " intermediate result: " + result);
            
            if (out != null) {
                try {
                    out.writeObject(result);
                } catch (IOException | IllegalArgumentException e) {
                    LOGGER.logException("Exception while writing to output file", e);
                }
            }
        }
    }
    
    /**
     * Signal the next component that this component is done and will not produce any more results.
     */
    private final void done() {
        if (!isInternalHelperComponent()) {
            long duration = System.currentTimeMillis() - tStart;
            LOGGER.logInfo("Analysis component " + getClass().getSimpleName() + " done",
                    "Execution took " + duration + "ms");
        }
        
        if (progressLogger != null) {
            progressLogger.close();
        }
        
        results.end();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                LOGGER.logException("Exception while closing output file", e);
            }
        }
        
        RESULZ_SIZE_LOGGER.removeComponent(this);
    }
    
    /**
     * Executes this component.
     */
    protected abstract void execute();
    
    /**
     * The name to display to users for the output of this component. For example, this will be used to name the
     * output tables.
     * 
     * @return The name describing the output of this component.
     */
    public abstract @NonNull String getResultName();
    
    /**
     * Whether this component is a "helper" component. Helper components will not log when they are started or finished.
     * Package visibility because only components in this package can be internal helper components.
     * 
     * @return Whether this is a helper component.
     */
    boolean isInternalHelperComponent() {
        return false;
    }
    
}
