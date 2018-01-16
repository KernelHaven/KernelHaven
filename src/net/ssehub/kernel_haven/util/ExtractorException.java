package net.ssehub.kernel_haven.util;

import net.ssehub.kernel_haven.util.null_checks.Nullable;

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
    public ExtractorException(@Nullable String message) {
        super(message);
    }
    
    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(@Nullable Throwable cause) {
        super(cause);
    }
    

    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param message The message to show.
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
