package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A JSON object. Basically a {@link Map}&lt;{@link String}, {@link JsonElement}&gt;.
 * 
 * @author Adam
 */
public class JsonObject extends JsonElement implements Iterable<Map.Entry<String, JsonElement>> {

    private @NonNull Map<String, net.ssehub.kernel_haven.util.io.json.JsonElement> elements;
    
    /**
     * Creates an empty {@link JsonObject}.
     */
    public JsonObject() {
        this.elements = new LinkedHashMap<>();
    }
    
    /**
     * Puts an element into the map.
     * 
     * @param key The key of the element.
     * @param element The element to add.
     */
    public void putElement(@NonNull String key, @NonNull JsonElement element) {
        elements.put(key, element);
    }
    
    /**
     * Returns an element from this map.
     * 
     * @param key The key of the element.
     * 
     * @return The element, or <code>null</code> if no element with the given key is present.
     */
    public @Nullable JsonElement getElement(@NonNull String key) {
        return elements.get(key);
    }
    
    /**
     * Removes an element from this map.
     * 
     * @param key The key of the element.
     */
    public void removeElement(@NonNull String key) {
        elements.remove(key);
    }
    
    /**
     * Returns the number of elements in this object.
     * 
     * @return The size of this map.
     */
    public int getSize() {
        return elements.size();
    }
    
    @Override
    public @NonNull Iterator<Map.Entry<String, net.ssehub.kernel_haven.util.io.json.JsonElement>> iterator() {
        return notNull(elements.entrySet().iterator());
    }
    
    @Override
    public @NonNull String toString() {
        StringBuilder result = new StringBuilder();
        
        
        if (!elements.isEmpty()) {
            result.append("{ ");
            
            for (Map.Entry<String, net.ssehub.kernel_haven.util.io.json.JsonElement> entry : this) {
                result.append('"')
                        .append(entry.getKey())
                        .append("\": ")
                        .append(entry.getValue().toString())
                        .append(", ");
            }
            result.delete(result.length() - 2, result.length()); // remvoe trailing ", "
            
            result.append(" }");
            
        } else {
            result.append("{}");
        }
        
        return notNull(result.toString());
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitObject(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonObject) {
            JsonObject o = (JsonObject) other;
            equal = this.elements.equals(o.elements);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
    
}
