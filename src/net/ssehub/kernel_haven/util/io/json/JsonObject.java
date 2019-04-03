/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A JSON object. Basically a {@link Map}&lt;{@link String}, {@link JsonElement}&gt;. Keeps insertion order when
 * iterating using {@link #iterator()}.
 * 
 * @author Adam
 */
public class JsonObject extends JsonElement implements Iterable<Map.Entry<String, JsonElement>> {

    private @NonNull Map<String, JsonElement> elements;
    
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
     * Helper method for reading a {@link JsonValue} with a specified type.
     * 
     * @param key The key of the element in this map.
     * @param type The expected type of {@link JsonValue}.
     * 
     * @param <T> The type of value to return.
     * 
     * @return The value of the {@link JsonValue} with the specified key.
     * 
     * @throws FormatException If no element with such a key exists, or has an invalid type.
     * 
     */
    @SuppressWarnings("unchecked")
    private <T> @NonNull T getValue(@NonNull String key, Class<? extends JsonValue<T>> type) throws FormatException {
        JsonElement element = getElement(key);
        if (element == null) {
            throw new FormatException("No element with key \"" + key + "\"");
        }
        
        if (!(type.isAssignableFrom(element.getClass()))) {
            throw new FormatException("Expected key \"" + key + "\" with type " + type.getSimpleName() + ", but got "
                    + element.getClass().getSimpleName());
        }
        
        return ((JsonValue<T>) element).getValue();
    }
    
    /**
     * Convenience method for reading a string value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The string value of this element.
     * 
     * @throws FormatException If no such element exists, or the element is not a string value.
     */
    public @NonNull String getString(@NonNull String key) throws FormatException {
        return getValue(key, JsonString.class);
    }
    
    /**
     * Convenience method for reading a boolean value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The boolean value of this element.
     * 
     * @throws FormatException If no such element exists, or the element is not a boolean value.
     */
    public boolean getBoolean(@NonNull String key) throws FormatException {
        return getValue(key, JsonBoolean.class);
    }
    
    /**
     * Convenience method for reading an integer value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The integer value of this element.
     * 
     * @throws FormatException If no such element exists, or the element is not an integer value.
     */
    public int getInt(@NonNull String key) throws FormatException {
        Number value = getValue(key, JsonNumber.class);
        if (!(value instanceof Integer)) {
            throw new FormatException("Expected key \"" + key + "\" with type integer, but got "
                    + value.getClass().getSimpleName());
        }
        return (int) value;
    }
    
    /**
     * Convenience method for reading a long value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The long value of this element.
     * 
     * @throws FormatException If no such element exists, or the element is not a long value.
     */
    public long getLong(@NonNull String key) throws FormatException {
        Number value = getValue(key, JsonNumber.class);
        if (!(value instanceof Integer) && !(value instanceof Long)) {
            throw new FormatException("Expected key \"" + key + "\" with type long, but got "
                    + value.getClass().getSimpleName());
        }
        return value.longValue();
    }
    
    /**
     * Convenience method for reading a double value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The double value of this element.
     * 
     * @throws FormatException If no such element exists, or the element is not a double value.
     */
    public double getDouble(@NonNull String key) throws FormatException {
        Number value = getValue(key, JsonNumber.class);
        if (!(value instanceof Double)) {
            throw new FormatException("Expected key \"" + key + "\" with type double, but got "
                    + value.getClass().getSimpleName());
        }
        return (double) value;
    }
    
    /**
     * Convenience method for reading a list value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The list stored under the given key.
     * 
     * @throws FormatException If no such element exists, or the element is not a list.
     */
    public @NonNull JsonList getList(@NonNull String key) throws FormatException {
        JsonElement element = getElement(key);
        if (element == null) {
            throw new FormatException("No element with key \"" + key + "\"");
        }
        
        if (!(element instanceof JsonList)) {
            throw new FormatException("Expected key \"" + key + "\" with type JsonList, but got "
                    + element.getClass().getSimpleName());
        }
        
        return (JsonList) element;
    }
    
    /**
     * Convenience method for reading an object value.
     * 
     * @param key The key of the element to read.
     * 
     * @return The object stored under the given key.
     * 
     * @throws FormatException If no such element exists, or the element is not an object.
     */
    public @NonNull JsonObject getObject(@NonNull String key) throws FormatException {
        JsonElement element = getElement(key);
        if (element == null) {
            throw new FormatException("No element with key \"" + key + "\"");
        }
        
        if (!(element instanceof JsonObject)) {
            throw new FormatException("Expected key \"" + key + "\" with type JsonObject, but got "
                    + element.getClass().getSimpleName());
        }
        
        return (JsonObject) element;
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
    public @NonNull Iterator<Map.Entry<String, JsonElement>> iterator() {
        return notNull(elements.entrySet().iterator());
    }
    
    @Override
    public @NonNull String toString() {
        StringBuilder result = new StringBuilder();
        
        
        if (!elements.isEmpty()) {
            result.append("{ ");
            
            for (Map.Entry<String, JsonElement> entry : this) {
                result.append('"')
                        .append(entry.getKey())
                        .append("\": ")
                        .append(notNull(entry.getValue()).toString())
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
