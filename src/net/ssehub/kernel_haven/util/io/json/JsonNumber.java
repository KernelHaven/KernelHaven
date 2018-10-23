package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A number value of JSON.
 * 
 * @author Adam
 */
public class JsonNumber extends JsonValue<Number> {

    private @NonNull Number value;
    
    /**
     * Creates a number.
     * 
     * @param value The value of this number.
     */
    public JsonNumber(@NonNull Number value) {
        this.value = value;
    }
    
    @Override
    public @NonNull Number getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        return notNull(value.toString());
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitNumber(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonNumber) {
            JsonNumber o = (JsonNumber) other;
            equal = this.value.equals(o.value);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
}
