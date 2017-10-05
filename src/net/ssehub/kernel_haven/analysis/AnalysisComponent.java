package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Timestamp;

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
    
    private BlockingQueue<O> results;
    
    private boolean logResults;
    
    private PrintStream out;
    
    /**
     * Creates a new analysis component.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisComponent(Configuration config) {
        results = new BlockingQueue<>();
        
        logResults = config.getLoggingAnalyissComponents().contains(getClass().getSimpleName());
        if (logResults) {
            // TODO properly create this
            try {
                out = new PrintStream(new File(config.getOutputDir(),
                        Timestamp.INSTANCE.getFilename(getClass().getSimpleName() + "_intermediate_result", "txt")));
            } catch (FileNotFoundException e) {
                LOGGER.logExceptionWarning("Can't create intermediate output file", e);
            }
        }
        
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
            LOGGER.logInfo("Analysis component " + getClass().getSimpleName()
                    + " intermediate result: " + result);
            // TODO: log result to file
            if (out != null) {
                out.println(result.toString());
                out.flush();
            }
        }
    }
    
    /**
     * Signal the next component that this component is done and will not produce any more results.
     */
    private void done() {
        results.end();
        if (out != null) {
            out.close();
        }
    }
    
    /**
     * Executes this component.
     */
    protected abstract void execute();
    
}
