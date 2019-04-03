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

import java.util.function.Function;

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
 * Represents a loop statement. The nested children inside this element are the loop body.
 * 
 * @author El-Sharkawy
 */
public class LoopStatement extends AbstractSyntaxElementWithNesting {

    /**
     * The type of loop.
     */
    public static enum LoopType {
        WHILE, DO_WHILE, FOR;
    }

    private @NonNull ICode condition;
    
    private @NonNull LoopType type;
    
    /**
     * Creates a {@link LoopStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param condition The condition of this loop.
     * @param type The type of loop that this is.
     */
    public LoopStatement(@NonNull Formula presenceCondition, @NonNull ICode condition,
            @NonNull LoopType type) {
        
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
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
    protected LoopStatement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.type = LoopType.valueOf(json.getString("loopType"));
        this.condition = (ICode) deserializeFunction.apply(json.getObject("loopCondition"));
    }
    
    /**
     * Returns which type of loop this is.
     * 
     * @return The {@link Type} of this loop.
     */
    public @NonNull ICode getLoopCondition() {
        return condition;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-loop\n" + condition.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitLoopStatement(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + type.hashCode() + hasher.hashCode((AbstractCodeElement<?>) condition);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof LoopStatement && super.equals(other, checker);
        
        if (equal) {
            LoopStatement o = (LoopStatement) other;
            
            equal = this.type == o.type && checker.isEqual(
                    (AbstractCodeElement<?>) this.condition, (AbstractCodeElement<?>) o.condition);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("loopType", new JsonString(notNull(type.name())));
        result.putElement("loopCondition", serializeFunction.apply(condition));
    }

}
