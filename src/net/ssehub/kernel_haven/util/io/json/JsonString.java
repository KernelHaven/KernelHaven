package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A JSON string.
 * 
 * @author Adam
 */
public class JsonString extends JsonValue<String> {

    private @NonNull String value;

    /**
     * Creates a JSON string with the given content.
     * 
     * @param value The string content.
     */
    public JsonString(@NonNull String value) {
        this.value = value;
    }

    @Override
    public @NonNull String getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        // TODO: escape
        return '"' + value + '"';
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitString(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonString) {
            JsonString o = (JsonString) other;
            equal = this.value.equals(o.value);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
        
}
