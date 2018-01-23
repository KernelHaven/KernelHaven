package net.ssehub.kernel_haven.code_model;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * An element in the syntax tree that indicates an error.
 * 
 * @author Adam
 */
public class ErrorSyntaxElement implements ISyntaxElementType {

    private @NonNull String message;
    
    /**
     * Creates a new error syntax element.
     * 
     * @param message The message to be displayed.
     */
    public ErrorSyntaxElement(@NonNull String message) {
        this.message = message;
    }
    
    /**
     * Retrieves the message for this error.
     * 
     * @return This error message.
     */
    public @NonNull String getMessage() {
        return message;
    }
    
    @Override
    public @NonNull String toString() {
        return "Error: " + message;
    }
    
}
