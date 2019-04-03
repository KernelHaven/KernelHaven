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

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a function definition. The nested children inside this element is the function body (usually a single
 * {@link CompoundStatement}).
 *
 * @author Adam
 */
public class Function extends AbstractSyntaxElementWithNesting {

    private @NonNull String name;
    
    private @NonNull ICode header;
    
    /**
     * Creates a {@link Function}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param name The name of this function.
     * @param header The header of this function as a code string.
     */
    public Function(@NonNull Formula presenceCondition, @NonNull String name, @NonNull ICode header) {
        super(presenceCondition);
        this.header = header;
        this.name = name;
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
    protected Function(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.name = json.getString("functionName");
        this.header = (ICode) deserializeFunction.apply(json.getObject("functionHeader"));
    }
    
    /**
     * Returns the header of this function as a code string.
     * 
     * @return The header of this function as a code string.
     */
    public @NonNull ICode getHeader() {
        return header;
    }
    
    /**
     * Returns the name of this function.
     * 
     * @return The name of this function.
     */
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Function " + name + "\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFunction(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + name.hashCode() + hasher.hashCode((AbstractCodeElement<?>) header);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof Function && super.equals(other, checker);
        
        if (equal) {
            Function o = (Function) other;
            
            equal = this.name.equals(o.name) && checker.isEqual(
                    (AbstractCodeElement<?>) this.header, (AbstractCodeElement<?>) o.header);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            java.util.function.@NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            java.util.function.@NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("functionName", new JsonString(name));
        result.putElement("functionHeader", serializeFunction.apply(header));
    }

}
