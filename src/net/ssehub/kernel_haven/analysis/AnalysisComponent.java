package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.Logger;

/**
 * A component of an analysis. Multiple of these can be combined together, to form an analysis pipeline.
 *
 * @param <O> The result type of this component.
 * 
 * @author Adam
 */
public abstract class AnalysisComponent<O> {
    
    private BlockingQueue<O> results;
    
    private boolean logResults;
    
    /**
     * Creates a new analysis component.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisComponent(Configuration config) {
        results = new BlockingQueue<>();
        
        logResults = config.getLoggingAnalyissComponents().contains(getClass().getSimpleName());
        
        Thread th = new Thread(() -> {
            try {
                execute();
            } finally {
                done();
            }
        }, "AnalysisComponent " + getClass().getSimpleName());
        th.setDaemon(true); //don't cause a deadlock with accidentally created AnalysisComponents that will never finish
        th.start();
    }
    
    /**
     * Retrieves the next result that this component creates. If none is currently available, this method blocks until
     * the result is ready.
     * 
     * @return The next result. <code>null</code> if this analysis is done and does not produce any results anymore.
     */
    public O getNextResult() {
        return results.get();
    }
    
    /**
     * Adds a result to be retrieved by the next component.
     * 
     * @param result The result to pass to the next component. Must not be <code>null</code>.
     */
    protected void addResult(O result) {
        results.add(result);
        
        if (logResults) {
            // TODO: log result to file
            Logger.get().logInfo("Analysis component " + getClass().getSimpleName()
                    + " intermediate result: " + result);
        }
    }
    
    /**
     * Signal the next component that this component is done and will not produce any more results.
     */
    private void done() {
        results.end();
    }
    
    /**
     * Executes this component.
     */
    protected abstract void execute();
    
}
