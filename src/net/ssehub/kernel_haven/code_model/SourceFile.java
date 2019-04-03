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
package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a single file from the source tree.
 * 
 * @param <ElementType> The type of nested elements in this file.
 * 
 * @author Johannes
 * @author Adam
 */
public class SourceFile<ElementType extends CodeElement<?>> implements Iterable<@NonNull ElementType> {

    /**
     * This path is relative to the source tree.
     */
    private @NonNull File path;

    /**
     * This are the toplevel elements which are not nested in other elements.
     */
    private @NonNull List<@NonNull ElementType> elements;

    /**
     * Constructs a Sourcefile.
     * 
     * @param path
     *            The relative path to the source file in the source tree. Must
     *            not be <code>null</code>.
     */
    public SourceFile(@NonNull File path) {
        this.path = path;
        elements = new LinkedList<>();
    }

    /**
     * Retrieves the path of this file which is relative to the source tree.
     * 
     * @return The path.
     */
    public @NonNull File getPath() {
        return path;
    }

    /**
     * Adds a element to the end of the list.
     * 
     * @param element The element to add. Must not be <code>null</code>.
     */
    public void addElement(@NonNull ElementType element) {
        this.elements.add(element);
    }

    /**
     * Iterates over the top elements not nested in other elements.
     * @return an iterator over top elements.
     */
    @Override
    public Iterator<@NonNull ElementType> iterator() {
        return elements.iterator();
    }
    
    /**
     * Returns the top element at the given index.
     * 
     * @param index The index to get the top element for.
     * @return The top element at the given index.
     * 
     * @throws IndexOutOfBoundsException If index is out of range.
     */
    public @NonNull ElementType getElement(int index) throws IndexOutOfBoundsException {
        return notNull(elements.get(index));
    }
    
    /**
     * Returns the number of top elements (not nested in other elements).
     *  
     * @return the number of elements.
     */
    public int getTopElementCount() {
        return elements.size();
    }
    
    /**
     * Casts this {@link SourceFile} to a source file with the given nested type.
     * 
     * @param type The new type to cast to.
     * 
     * @param <T> The new generic type.
     * 
     * @return This, with a different generic.
     * 
     * @throws ClassCastException If the nested elements do not match the given type.
     */
    @SuppressWarnings("unchecked")
    public <T extends CodeElement<?>> @NonNull SourceFile<T> castTo(Class<T> type) throws ClassCastException {
        for (ElementType element : this) {
            if (!type.isAssignableFrom(element.getClass())) {
                throw new ClassCastException("Nested element with type " + element.getClass().getName()
                        + " can't be cast to " + type.getName());
            }
        }
        
        return (SourceFile<T>) this;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof SourceFile<?>) {
            SourceFile<?> other = (SourceFile<?>) obj;
            result = this.path.equals(other.path) && this.elements.equals(other.elements);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return path.hashCode() * 31 + elements.hashCode();
    }

}
