package net.ssehub.kernel_haven.code_model;

/**
 * An element in the syntax tree that indicates an error.
 * 
 * @author Adam
 */
public class ErrorSyntaxElement implements ISyntaxElementType {

    private String message;
    
    /**
     * Creates a new error syntax element.
     * 
     * @param message The message to be displayed.
     */
    public ErrorSyntaxElement(String message) {
        this.message = message;
    }
    
    /**
     * Retrieves the message for this error.
     * 
     * @return This error message.
     */
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return "Error: " + message;
    }
    
}
