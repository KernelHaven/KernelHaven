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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a code element inside a {@link SourceFile}.
 * 
 * @param <NestedType> The type of elements that are nested inside this element.
 * 
 * @author Johannes
 * @author Adam
 */
public interface CodeElement<NestedType extends CodeElement<NestedType>> extends Iterable<@NonNull NestedType> {

    /**
     * Returns the number of nested elements (not recursively).
     * 
     * @return the number of elements.
     */
    public abstract int getNestedElementCount();
    
    /**
     * Returns a single nested element inside this element.
     * 
     * @param index The index of the element to return.
     * 
     * @return The element at the position index.
     * 
     * @throws IndexOutOfBoundsException If index &ge; getNestedElementCount().
     */
    public abstract @NonNull NestedType getNestedElement(int index) throws IndexOutOfBoundsException;

    /**
     * Adds a nested element to the end of the list.
     * 
     * @param element The element to add.
     * 
     * @throws IndexOutOfBoundsException If the concrete class conceptually does not allow any more children.
     */
    public abstract void addNestedElement(@NonNull NestedType element) throws IndexOutOfBoundsException;
    
    /**
     * Returns the line where this element starts in the source file.
     * 
     * @return The start line number. -1 if not available.
     */
    public abstract int getLineStart();

    /**
     * Set the line where this element starts in the source file. This should only be called by the extractor
     * that creates the code model.
     * 
     * @param start The start line number. -1 if not available.
     */
    public abstract void setLineStart(int start);
    
    /**
     * Returns the line where this element ends in the source file.
     * 
     * @return The end line number. -1 if not available.
     */
    public abstract int getLineEnd();
    
    /**
     * Sets the line where this element ends in the source file. This should only be called by the extractor
     * that creates the code model.
     * 
     * @param end The end line number. -1 if not available.
     */
    public abstract void setLineEnd(int end);
    
    /**
     * Returns the source file that this element originates from.
     * 
     * @return The source file location relative to the source tree. <code>new File("&lt;unknown&gt;")</code> if not
     *      available.
     */
    public abstract @NonNull File getSourceFile();

    /**
     * Returns the immediate condition of this element. This condition is not
     * considering the parent of this element, etc.
     * 
     * @return the condition. May be <code>null</code> if this concept dosen't
     *         apply for the concrete subclass.
     */
    public abstract @Nullable Formula getCondition();

    /**
     * Returns the presence condition of this element.
     * 
     * @return the presence condition. Must not be <code>null</code>.
     */
    public abstract @NonNull Formula getPresenceCondition();
    
    /**
     * Converts this single element into a string. This should not consider nested elements. However, other attributes
     * may be added in additional lines with the given indentation + "\t".
     * 
     * @param indentation The initial indentation to use for multiple lines.
     * 
     * @return A string representation of this single element.
     */
    public @NonNull String elementToString(@NonNull String indentation);
    
    /**
     * Converts this element and all its nested elements into a string.
     * 
     * @param indentation The initial indentation to use.
     * 
     * @return A string representation of the full hierarchy starting from this element.
     */
    public @NonNull String toString(@NonNull String indentation);
    
    /**
     * Serializes this element as a CSV line. This does not consider nested elements.
     * Extending classes also need a createFromCsv(String[], Parser<Formula>) method that deserializes
     * this output.
     * 
     * @return The CSV parts representing this element.
     */
    public @NonNull List<@NonNull String> serializeCsv();

    /**
     * Serializes this element as a JSON. Does not recurse into the primary nesting structure, but may recurse
     * into secondary nested elements. Overriding methods should always call
     * super.{@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param result The resulting JSON object to serialize into.
     * @param serializeFunction The function to use for serializing secondary nested elements. Do not use this to
     *      serialize the {@link CodeElement} in the primary nesting structure! (i.e. {@link #getNestedElement(int)})
     * @param idFunction The function to convert {@link CodeElement}s to IDs. Use this for references to other
     *      {@link CodeElement} that are somewhere else in the primary nesting structure.
     */
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction);
    
    /**
     * Helper method for de-serialization. This method resolves the temporary integer IDs back to {@link CodeElement}
     * references. Overriding methods should always call super.{@link #resolveIds(Map)}.
     * 
     * @param mapping The mapping of integer IDs to {@link CodeElement}s.
     * 
     * @throws FormatException If matching code elements could not be found.
     */
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException;
    
}
