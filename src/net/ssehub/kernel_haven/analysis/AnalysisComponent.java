package net.ssehub.kernel_haven.analysis;

import java.io.IOException;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.Logger;
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
    
    private @NonNull BlockingQueue<O> results;
    
    private boolean logResults;
    
    private ITableWriter out;
    
    private boolean started;
    
    /**
     * Creates a new analysis component.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisComponent(@NonNull Configuration config) {
        results = new BlockingQueue<>();
        
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
     * Starts a new thread that executes this analysis component.
     * Package visibility for {@link SplitComponent}.
     */
    synchronized void start() {
        if (logResults) {
            try {
                out = PipelineAnalysis.getInstance().getResultCollection().getWriter(getResultName());
            } catch (IOException e) {
                LOGGER.logExceptionWarning("Can't create intermediate output file", e);
            }
        }
        
        Thread th = new Thread(() -> {
            LOGGER.logDebug("Analysis component " + getClass().getSimpleName() + " starting");
            
            try {
                execute();
            } finally {
                done();
            }
        }, getClass().getSimpleName());
        th.setDaemon(true); //don't cause a deadlock with accidentally created AnalysisComponents that will never finish
        th.start();
        
        started = true;
    }
    
    /**
     * Retrieves the next result that this component creates. If none is currently available, this method blocks until
     * the result is ready.
     * 
     * @return The next result. <code>null</code> if this analysis is done and does not produce any results anymore.
     */
    public final @Nullable O getNextResult() {
        synchronized (this) {
            if (!started) {
                start();
            }
        }
        return results.get();
    }
    
    /**
     * Adds a result to be retrieved by the next component.
     * 
     * @param result The result to pass to the next component. Must not be <code>null</code>.
     */
    protected final void addResult(@NonNull O result) {
        results.add(result);
        
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
        LOGGER.logDebug("Analysis component " + getClass().getSimpleName() + " done");
        
        results.end();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                LOGGER.logException("Exception while closing output file", e);
            }
        }
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
    
}
