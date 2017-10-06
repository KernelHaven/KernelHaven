package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.BlockingQueue;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Timestamp;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;

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
    
    private ITableWriter out;
    
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
                PrintStream out = new PrintStream(new File(config.getOutputDir(),
                        Timestamp.INSTANCE.getFilename(getClass().getSimpleName() + "_intermediate_result", "csv")));
                this.out = new CsvWriter(out);
                
            } catch (IOException e) {
                LOGGER.logExceptionWarning("Can't create intermediate output file", e);
            }
        }
        
        Thread th = new Thread(() -> {
            try {
                execute();
            } finally {
                done();
            }
        }, getClass().getSimpleName());
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
            if (out != null) {
                try {
                    out.writeRow(result);
                } catch (IOException | IllegalArgumentException e) {
                    LOGGER.logException("Exception while writing to output file", e);
                }
            }
        }
    }
    
    /**
     * Signal the next component that this component is done and will not produce any more results.
     */
    private void done() {
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
    
}
