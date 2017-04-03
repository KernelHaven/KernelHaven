package de.uni_hildesheim.sse.kernel_haven.util;

/**
 * Exception for unexpected structure of file-content.
 * 
 * @author Adam
 * @author Moritz
 *
 */
public class FormatException extends Exception {

    
    private static final long serialVersionUID = 2081277470741239201L;

    /**
     * Creates a new FormatException.
     */
    public FormatException() {

    }
    
    /**
     * Creates a new FormatException.
     * 
     * @param message The message to display.
     */
    public FormatException(String message) {
        super(message);
    }
    
    /**
     * Creates a new FormatException.
     * 
     * @param cause The exception that caused this exception.
     */
    public FormatException(Throwable cause) {
        super(cause);
    }
}
