package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A list of {@link JsonElement}s.
 * 
 * @author Adam
 */
public class JsonList extends JsonElement implements Iterable<net.ssehub.kernel_haven.util.io.json.JsonElement> {

    private @NonNull List<net.ssehub.kernel_haven.util.io.json.JsonElement> elements;
    
    /**
     * Creates an empty list.
     */
    public JsonList() {
        this.elements = new ArrayList<>();
    }
    
    /**
     * Adds an element to the end of the list.
     * 
     * @param element The element to add.
     */
    public void addElement(@NonNull JsonElement element) {
        elements.add(element);
    }
    
    /**
     * Replaces the given element in the list.
     * 
     * @param index The index to replace.
     * @param element The new element value.
     * 
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public void setElement(int index, @NonNull JsonElement element) throws IndexOutOfBoundsException {
        elements.set(index, element);
    }
    
    /**
     * Removes an element from the list. The following elements will move one to the left.
     * 
     * @param index The index to remove.
     * 
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public void removeElement(int index) throws IndexOutOfBoundsException {
        elements.remove(index);
    }
    
    /**
     * Returns an element from the list.
     * 
     * @param index The index of the element.
     * 
     * @return The element at the specified index.
     * 
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public @Nullable JsonElement getElement(int index) throws IndexOutOfBoundsException {
        return elements.get(index);
    }
    
    /**
     * Returns the number of elements in this list.
     * 
     * @return The size of this list.
     */
    public int getSize() {
        return elements.size();
    }
    
    @Override
    public @NonNull Iterator<net.ssehub.kernel_haven.util.io.json.JsonElement> iterator() {
        return notNull(elements.iterator());
    }
    
    @Override
    public @NonNull String toString() {
        StringBuilder result = new StringBuilder();
        
        if (elements.isEmpty()) {
            result.append("[]");
            
        } else {
            result.append("[ ");
            
            for (int i = 0; i < elements.size(); i++) {
                result.append(elements.get(i).toString());
                if (i != elements.size() - 1) {
                    result.append(", ");
                }
            }
            
            result.append(" ]");
        }
        
        return notNull(result.toString());
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitList(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonList) {
            JsonList o = (JsonList) other;
            equal = this.elements.equals(o.elements);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
    
}
