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
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A single statement for the C preprocessor (CPP). This represents everything except #if etc.
 *
 * @author Adam
 */
public class CppStatement extends AbstractSyntaxElementNoNesting {

    /**
     * The type of preprocessor directive.
     */
    public static enum Type {
        DEFINE, UNDEF, INCLUDE, PRAGMA, ERROR, WARNING, LINE, EMPTY
    }
    
    private @NonNull Type type;
    
    private @Nullable ICode expression;

    /**
     * Creates a {@link CppStatement}.
     * 
     * @param presenceCondition The presence condition.
     * @param type The type of preprocessor directive used.
     * @param expression The text expression following the directive. <code>null</code> if there is none.
     */
    public CppStatement(@NonNull Formula presenceCondition, @NonNull Type type, @Nullable ICode expression) {
        super(presenceCondition);
        
        this.type = type;
        this.expression = expression;
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
    protected CppStatement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        String typeString = json.getString("cppType");
        this.type = Type.valueOf(typeString);
        
        if (json.getElement("cppExpression") != null) {
            this.expression = (ICode) deserializeFunction.apply(json.getObject("cppExpression"));
        }
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type + "\n";
        
        ICode expression = this.expression;
        if (expression != null) {
            result += expression.toString(indentation + "\t");
        }
        
        return result;
    }
    
    /**
     * Returns the type of preprocessor directive.
     * 
     * @return The type of directive that this statement represents.
     */
    public @NonNull Type getType() {
        return type;
    }
    
    /**
     * Returns the text expression following the directive.
     * 
     * @return The text expression following the directive. <code>null</code> if there is none.
     */
    public @Nullable ICode getExpression() {
        return expression;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppStatement(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + type.hashCode()
                + (expression != null ? hasher.hashCode((AbstractCodeElement<?>) expression) : 123);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof CppStatement && super.equals(other, checker);
        
        if (equal) {
            CppStatement o = (CppStatement) other;
            
            if (this.expression != null && o.expression != null) {
                equal = this.type == o.type && checker.isEqual(
                        (AbstractCodeElement<?>) this.expression, (AbstractCodeElement<?>) o.expression);
            } else {
                equal = this.type == o.type && this.expression == o.expression;
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
        if (expression != null) {
            result.putElement("cppExpression", serializeFunction.apply(expression));
        }
    }
    
}
