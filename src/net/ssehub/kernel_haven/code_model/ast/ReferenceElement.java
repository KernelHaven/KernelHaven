/*
 * Copyright 2019 University of Hildesheim, Software Systems Engineering
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

import java.util.Map;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A special {@link ISyntaxElement} that represents a cross-link in the primary AST nesting structure. This is required,
 * as multiple occurrences of the same AST element in the primary nesting structure
 * (i.e. {@link ISyntaxElement#getNestedElement(int)}) are not allowed.
 *
 * @author Adam
 */
public class ReferenceElement extends AbstractSyntaxElementNoNesting {

    private @NonNull ISyntaxElement referenced;
    
    private @Nullable Integer referencedId;
    
    /**
     * Creates a new reference to another element.
     * 
     * @param presenceCondition The presence condition of this reference.
     * @param referenced The referenced element.
     */
    public ReferenceElement(@NonNull Formula presenceCondition, @NonNull ISyntaxElement referenced) {
        super(presenceCondition);
        this.referenced = referenced;
    }
    
    /**
     * De-serializes the given JSON to a {@link ReferenceElement}. This is the inverse operation to
     * {@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param json The JSON do de-serialize.
     * @param deserializeFunction The function to use for de-serializing secondary nested elements. Do not use this to
     *      de-serialize the {@link CodeElement}s in the primary nesting structure!
     *      (i.e. {@link #getNestedElement(int)})
     * 
     * @throws FormatException If the JSON does not have the expected format.
     */
    @SuppressWarnings("null") // referenced will be initialited in resolveIds()
    protected ReferenceElement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
                throws FormatException {
        super(json, deserializeFunction);
        
        this.referencedId = json.getInt("referenced");
    }
    
    /**
     * Changes the referenced element. This may only be called by the extractor that creates this model.
     * 
     * @param referenced The new referenced element.
     */
    public void setReferenced(@NonNull ISyntaxElement referenced) {
        this.referenced = referenced;
    }
    
    /**
     * Returns the reference element.
     * 
     * @return The referenced element.
     */
    public @NonNull ISyntaxElement getReferenced() {
        return referenced;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder("Reference\n");
        result.append(referenced.toString(indentation + '\t'));
        return notNull(result.toString());
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitReference(this);
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);

        result.putElement("referenced", serializeFunction.apply(referenced));
    }
    
    @Override
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException {
        super.resolveIds(mapping);
        
        if (this.referencedId == null) {
            throw new FormatException("Did not get de-erialization IDs");
        }

        ISyntaxElement referenced = (ISyntaxElement) mapping.get(referencedId);
        if (referenced == null) {
            throw new FormatException("Unknown ID: " + referencedId);
        }
        
        this.referenced = referenced;
    }

}
