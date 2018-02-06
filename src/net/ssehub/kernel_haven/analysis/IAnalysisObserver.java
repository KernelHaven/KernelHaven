package net.ssehub.kernel_haven.analysis;

import java.util.List;

/**
 * Observer for the {@link ObservableAnalysis}, will be notified after all analysis results are available.
 * @author El-Sharkawy
 *
 */
public interface IAnalysisObserver {
    
    /**
     * Will be called after the last result was produced.
     * 
     * @param analysisResults Contains all produced results, the list will be of type of the input/ouput types of
     *     the observed analysis
     */
    public void notifyFinished(List<?> analysisResults);

    /**
     * Notifies that the analysis has come to an (unexpected) end and has produced no results.
     */
    public void notifyFinished();
}
