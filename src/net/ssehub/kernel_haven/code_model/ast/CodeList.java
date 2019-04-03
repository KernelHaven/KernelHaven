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

import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A list containing {@link ICode} elements. For example, this class is used to represent that some parts of an
 * unparsed code string have no immediate variability conditions, while other parts are wrapped inside a
 * {@link CppBlock}. The nested children are only {@link ICode} elements.
 *
 * @author Adam
 */
public class CodeList extends AbstractSyntaxElementWithNesting implements ICode {

    /**
     * Creates a {@link CodeList}.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public CodeList(@NonNull Formula presenceCondition) {
        super(presenceCondition);
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
    protected CodeList(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "CodeList\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCodeList(this);
    }
    
}
