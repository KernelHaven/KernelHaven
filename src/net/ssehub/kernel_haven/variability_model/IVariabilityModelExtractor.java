package net.ssehub.kernel_haven.variability_model;

/**
 * An extractor that extracts the variability model.
 * 
 * @author Adam
 * @author Moritz
 */
public interface IVariabilityModelExtractor {

    /**
     * Starts the extractor process asynchronously.
     */
    public void start();
    
    /**
     * Tells the extractor to stop its extraction process. This is called if a timeout is exceeded
     * (when the extractor took to long). After this is called, the extractor should not call
     * setResult() or setException() anymore.
     */
    public void stop();
    
    /**
     * Sets the provider to notify about the results. Must be set before start() is called.
     * 
     * @param provider The provider to notify. Not null.
     */
    public void setProvider(VariabilityModelProvider provider);
    
}
