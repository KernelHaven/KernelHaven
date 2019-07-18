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

import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A special {@link ISyntaxElement} that signals that some kind of error happened during parsing. The unparseable
 * element is represented by this instance. Nested elements may be properly parsed again.
 *
 * @author Adam
 */
public class ErrorElement extends AbstractSyntaxElementWithNesting implements ICode {

    private @NonNull String errorText;
    
    /**
     * Creates an {@link ErrorElement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param errorText A text describing the error that caused normal parsing to fail.
     */
    public ErrorElement(@NonNull Formula presenceCondition, @NonNull String errorText) {
        super(presenceCondition);
        this.errorText = errorText;
    }
    
    /**
     * De-serializes the given JSON to a {@link ErrorElement}. This is the inverse operation to
     * {@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param json The JSON do de-serialize.
     * @param deserializeFunction The function to use for de-serializing secondary nested elements. Do not use this to
     *      de-serialize the {@link CodeElement}s in the primary nesting structure!
     *      (i.e. {@link #getNestedElement(int)})
     * 
     * @throws FormatException If the JSON does not have the expected format.
     */
    protected ErrorElement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
                throws FormatException {
        super(json, deserializeFunction);
        
        this.errorText = json.getString("errorText");
    }
    
    /**
     * Returns the text describing the error that caused this element.
     * 
     * @return The text describing the error that caused normal parsing to fail.
     */
    public String getErrorText() {
        return errorText;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "ErrorElement: " + errorText + "\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitErrorElement(this);
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("errorText", new JsonString(errorText));
    }
    
}
