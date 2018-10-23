package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Abstract super-class for all value types of JSON.
 * 
 * @author Adam
 *
 * @param <T> The Java equivalent of the value type.
 */
public abstract class JsonValue<T> extends JsonElement {

    /**
     * Returns the value of this element.
     * 
     * @return The Java value of this element.
     */
    public abstract @NonNull T getValue();
    
}
