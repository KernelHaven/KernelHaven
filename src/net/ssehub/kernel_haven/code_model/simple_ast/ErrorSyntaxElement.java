package net.ssehub.kernel_haven.code_model.simple_ast;

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
    
    @Override
    public int hashCode() {
        return message.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof ErrorSyntaxElement) {
            equal = this.message.equals(((ErrorSyntaxElement) obj).message);
        }
        
        return equal;
    }
    
}
