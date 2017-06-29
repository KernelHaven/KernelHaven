package net.ssehub.kernel_haven.util;

/**
 * Exception thrown if an extractor fails.
 * 
 * @author Adam
 * @author Moritz
 */
public class ExtractorException extends Exception {

    private static final long serialVersionUID = 8036527012134674472L;
    
    /**
     * Creates a new {@link ExtractorException}.
     */
    public ExtractorException() {
    }
    
    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param message The message to show.
     */
    public ExtractorException(String message) {
        super(message);
    }
    
    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(Throwable cause) {
        super(cause);
    }
    

    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param message The message to show.
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

}
