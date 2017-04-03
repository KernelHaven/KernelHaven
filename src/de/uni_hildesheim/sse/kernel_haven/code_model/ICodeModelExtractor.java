package de.uni_hildesheim.sse.kernel_haven.code_model;

import java.io.File;

import de.uni_hildesheim.sse.kernel_haven.util.BlockingQueue;

/**
 * An extractor that extracts the code model.
 * 
 * @author Adam
 * @author Johannes
 */
public interface ICodeModelExtractor {
    
    /**
     * Starts the extractor process asynchronously.
     * 
     * @param filesToParse A queue containing the files (relative to source code tree) that should
     *      be parsed by this extractor.
     */
    public void start(BlockingQueue<File> filesToParse);
    
    /**
     * Tells the extractor to stop its extraction process. This is called if a timeout is exceeded
     * (when the extractor took to long). After this is called, the extractor should not call
     * addResult() or setException() anymore.
     */
    public void stop();
    
    /**
     * Sets the provider to notify about the results. Must be set before start() is called.
     * 
     * @param provider The provider to notify. Not null.
     */
    public void setProvider(CodeModelProvider provider);

}
