package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A visitor for {@link JsonElement}s.
 * 
 * @author Adam
 *
 * @param <T> The return type for the visitor. Use {@link Void} if none is needed.
 */
public interface JsonVisitor<T> {

    /**
     * Visits a {@link JsonObject}.
     * 
     * @param object The object to visit.
     * 
     * @return Some value.
     */
    public T visitObject(@NonNull JsonObject object);
    
    /**
     * Visits a {@link JsonList}.
     * 
     * @param list The list to visit.
     * 
     * @return Some value.
     */
    public T visitList(@NonNull JsonList list);
    
    /**
     * Visits a {@link JsonBoolean}.
     * 
     * @param bool The boolean to visit.
     * 
     * @return Some value.
     */
    public T visitBoolean(@NonNull JsonBoolean bool);
    
    /**
     * Visits a {@link JsonNumber}.
     * 
     * @param number The number to visit.
     * 
     * @return Some value.
     */
    public T visitNumber(@NonNull JsonNumber number);
    
    /**
     * Visits a {@link JsonString}.
     * 
     * @param string The string to visit.
     * 
     * @return Some value.
     */
    public T visitString(@NonNull JsonString string);
    
    /**
     * Visits. a {@link JsonNull}.
     * 
     * @param nall The null value to visit.
     * 
     * @return Some value.
     */
    public T visitNull(@NonNull JsonNull nall);
    
}
