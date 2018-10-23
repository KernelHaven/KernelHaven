package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Abstract super-class of all JSON elements.
 * 
 * @author Adam
 */
public abstract class JsonElement {

    /**
     * Creates a valid JSON string of this element.
     */
    @Override
    public abstract @NonNull String toString();
    
    /**
     * Accepts the given visitor.
     * 
     * @param visitor The visitor to accept.
     * 
     * @return The return value of the visitor.
     * 
     * @param <T> The return type of the visitor.
     */
    public abstract <T> T accept(@NonNull JsonVisitor<T> visitor);
    
    @Override
    public abstract boolean equals(Object other);
    
    @Override
    public abstract int hashCode();
    
}
