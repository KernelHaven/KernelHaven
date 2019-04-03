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
package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A preprocessor block with a variability condition (e.g. an #ifdef block). The nested children in this element
 * are the elements inside the preprocessor block.
 *
 * @author El-Sharkawy
 */
public class CppBlock extends AbstractSyntaxElementWithNesting implements ICode {
    
    /**
     * The kind of preprocessor block.
     */
    public static enum Type {
        IF, IFDEF, IFNDEF, ELSEIF, ELSE;
    }

    private @Nullable Formula condition;
    
    private @NonNull Type type;
    
    private List<@NonNull CppBlock> siblings;

    private @Nullable List<@NonNull Integer> serializationSiblingIds;
    
    /**
     * Creates a {@link CppBlock}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param condition The variability condition of this block.
     * @param type The {@link Type} of preprocessor block that this is.
     */
    public CppBlock(@NonNull Formula presenceCondition, @Nullable Formula condition, @NonNull Type type) {
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
        siblings = new ArrayList<>();
    }
    
    /**
     * De-serializes the given JSON to a {@link CodeElement}. This is the inverse operation to
     * {@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param json The JSON do de-serialize.
     * @param deserializeFunction The function to use for de-serializing secondary nested elements. Do not use this to
     *      de-serialize the {@link CodeElement}s in the primary nesting structure!
     *      (i.e. {@link #getNestedElement(int)})
     * 
     * @throws FormatException If the JSON does not have the expected format.
     */
    protected CppBlock(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.type = Type.valueOf(json.getString("cppType"));
        
        if (json.getElement("cppCondition") != null) {
            this.condition = parseJsonFormula(json.getString("cppCondition"));
        }
        
        JsonList siblingIds = json.getList("cppSiblings");
        List<@NonNull Integer> serializationSiblingIds = new ArrayList<>(siblingIds.getSize());
        for (JsonElement siblingId : siblingIds) {
            serializationSiblingIds.add((Integer) ((JsonNumber) siblingId).getValue());
        }
        
        this.serializationSiblingIds = serializationSiblingIds;
        this.siblings = new LinkedList<>(); // will be filled in resolveIds()
    }
    
    /**
     * Adds another sibling to this {@link CppBlock}. This should only be
     * called by the extractors that creates the AST. It should be ensured that
     * all siblings have a complete list of all siblings in a given
     * #if-#elif-#else construct (including themselves).
     * 
     * @param sibling
     *            The sibling to add.
     */
    public void addSibling(@NonNull CppBlock sibling) {
        siblings.add(sibling);
    }

    /**
     * Returns the number of siblings this element has. This is at least one
     * (this object itself).
     * 
     * @return The number of siblings.
     */
    public int getSiblingCount() {
        return siblings.size();
    }

    /**
     * Returns the sibling at the given index.
     * 
     * @param index
     *            The index to get the sibling for.
     * @return The sibling at the given index.
     * 
     * @throws IndexOutOfBoundsException
     *             If index is out of bounds.
     */
    public @NonNull CppBlock getSibling(int index) throws IndexOutOfBoundsException {
        return notNull(siblings.get(index));
    }
    
    /**
     * Returns an unmodifiable iterator for iterating through all the siblings starting at the opening <tt>if</tt>.
     * This also contains this element itself.
     * 
     * @return An unmodifiable iterator for iterating through all the siblings.
     */
    public Iterator<@NonNull CppBlock> getSiblingsIterator() {
        // Copied from: java.util.Collections.UnmodifiableCollection<E>
        return new Iterator<@NonNull CppBlock>() {
            private final Iterator<@NonNull CppBlock> itr = siblings.iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }
            
            @Override
            public @NonNull CppBlock next() {
                return notNull(itr.next());
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void forEachRemaining(Consumer<? super CppBlock> action) {
                // Use backing collection version
                itr.forEachRemaining(action);
            }
        };
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    /**
     * Returns the {@link Type} of preprocessor block that this is.
     * 
     * @return The {@link Type} of preprocessor block that this is.
     */
    public @NonNull Type getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type.name();
        
        Formula condition = this.condition;
        if (condition != null) {
            result += " " + condition.toString();
        }
        return result + "\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppBlock(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        int result = 1;
        
        for (CppBlock sibling : siblings) {
            result = 31 * result + hasher.hashCode(sibling);
        }
        
        return result + super.hashCode(hasher) + type.hashCode()
                + (condition != null ? condition.hashCode() : 123);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof CppBlock && super.equals(other, checker);
        
        if (equal) {
            CppBlock o = (CppBlock) other;
            
            if (this.condition != null && o.condition != null) {
                equal = this.type == o.type && this.condition.equals(o.condition)
                        && this.siblings.size() == o.siblings.size();
            } else {
                equal = this.type == o.type && this.condition == o.condition
                        && this.siblings.size() == o.siblings.size();
            }
            
            for (int i = 0; equal && i < this.siblings.size(); i++) {
                equal &= checker.isEqual(this.siblings.get(i), o.siblings.get(i));
            }
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("cppType", new JsonString(notNull(type.name())));
        if (condition != null) {
            result.putElement("cppCondition", new JsonString(condition.toString()));
        }
        
        JsonList siblingIds = new JsonList();
        for (CppBlock sibling : siblings) {
            siblingIds.addElement(new JsonNumber(idFunction.apply(sibling)));
        }

        result.putElement("cppSiblings", siblingIds);
    }
    
    @Override
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException {
        super.resolveIds(mapping);
        
        List<@NonNull Integer> serializationSiblingIds = this.serializationSiblingIds;
        this.serializationSiblingIds = null;
        if (serializationSiblingIds == null) {
            throw new FormatException("Did not get de-erialization IDs");
        }
        
        for (Integer id : serializationSiblingIds) {
            CppBlock sibling = (CppBlock) mapping.get(id);
            if (sibling == null) {
                throw new FormatException("Unknown ID: " + id);
            }
            this.siblings.add(sibling);
        }
    }

}
