package net.ssehub.kernel_haven.code_model;

import java.util.HashMap;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A wrapper around objects so that they can be used as keys in a {@link HashMap} based on object identity.
 * 
 * @param <T> The type that is held by this wrapper.
 */
final class IdentityWrapper<T> {


    private @NonNull T data;
    
    /**
     * Creates a wrapper for the given object.
     * 
     * @param data The object.
     */
    public IdentityWrapper(@NonNull T data) {
        this.data = data;
    }
    
    /**
     * Returns the object held by this wrapper.
     * 
     * @return The held object.
     */
    public @NonNull T getData() {
        return data;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IdentityWrapper && ((IdentityWrapper<?>) obj).data == this.data;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(data);
    }
    
}
