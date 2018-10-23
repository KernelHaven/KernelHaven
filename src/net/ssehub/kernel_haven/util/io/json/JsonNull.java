package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * The <code>null</code> value of JSON.
 * 
 * @author Adam
 */
public class JsonNull extends JsonValue<JsonNull> {

    public static final @NonNull JsonNull INSTANCE = new JsonNull();
    
    /**
     * Singleton constructor.
     */
    private JsonNull() {
    }
    
    @Override
    public @NonNull JsonNull getValue() {
        return this;
    }
    
    @Override
    public @NonNull String toString() {
        return "null";
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitNull(this);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return 13;
    }

}
