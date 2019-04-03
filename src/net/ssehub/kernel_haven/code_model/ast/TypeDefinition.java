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
 * Represents a declaration (and initialization) of a new data structure, outside of a function. More precisely one of:
 * <ul>
 *     <li><a href="https://www.srcml.org/doc/c_srcML.html#struct-definition">Struct definition/declaration</a></li>
 *     <li><a href="https://www.srcml.org/doc/c_srcML.html#enum-definition">Enum definition/declaration</a></li>
 * </ul>
 * 
 * TODO SE: is this list complete? should we have a reference to srcML here?
 * 
 * @author El-Sharkawy
 */
public class TypeDefinition extends AbstractSyntaxElementWithNesting {

    /**
     * The type of typedef.
     */
    public static enum TypeDefType {
        STRUCT, ENUM, TYPEDEF, UNION;
    }
    
    private @NonNull ICode declaration;
    
    private @NonNull TypeDefType type;
    
    /**
     * Creates a {@link TypeDefinition}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param declaration The declaration of this element.
     * @param type The type of typedef that this is.
     */
    public TypeDefinition(@NonNull Formula presenceCondition, @NonNull ICode declaration,
            @NonNull TypeDefType type) {
        
        super(presenceCondition);
        this.declaration = declaration;
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
    protected TypeDefinition(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.type = TypeDefType.valueOf(json.getString("typedefType"));
        this.declaration = (ICode) deserializeFunction.apply(json.getObject("typedefDeclaration"));
    }
    
    /**
     * Returns the declaration of this type.
     * 
     * @return The declaration of this type.
     */
    public @NonNull ICode getDeclaration() {
        return declaration;
    }
    
    /**
     * Returns the type of this typedef.
     * 
     * @return The type of this typedef.
     */
    public TypeDefType getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-Definition\n" + declaration.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitTypeDefinition(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + hasher.hashCode((AbstractCodeElement<?>) declaration) + type.hashCode();
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof TypeDefinition && super.equals(other, checker);
        
        if (equal) {
            TypeDefinition o = (TypeDefinition) other;
            
            equal = this.type == o.type && checker.isEqual(
                    (AbstractCodeElement<?>) this.declaration, (AbstractCodeElement<?>) o.declaration);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("typedefType", new JsonString(notNull(type.name())));
        result.putElement("typedefDeclaration", serializeFunction.apply(declaration));
    }

}
