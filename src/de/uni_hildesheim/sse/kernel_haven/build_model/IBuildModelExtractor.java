package de.uni_hildesheim.sse.kernel_haven.build_model;

/**
 * An extractor that extracts the build model.
 * 
 * @author Malek
 * @author Johannes
 * @author Marvin
 */
public interface IBuildModelExtractor {

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
    public void setProvider(BuildModelProvider provider);
    
}
