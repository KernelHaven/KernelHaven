package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A boolean value.
 * 
 * @author Adam
 */
public class JsonBoolean extends JsonValue<Boolean> {

    public static final @NonNull JsonBoolean TRUE = new JsonBoolean(true);
    
    public static final @NonNull JsonBoolean FALSE = new JsonBoolean(false);
    
    private boolean value;

    /**
     * Singleton constructor.
     * 
     * @param value The boolean value.
     */
    private JsonBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public @NonNull Boolean getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        return value ? "true" : "false";
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

}
