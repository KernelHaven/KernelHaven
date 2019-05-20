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
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A block of one or multiple lines of code. For example, this could be used to represent an #ifdef hierarchy.
 * 
 * @author Adam
 */
public class CodeBlock extends AbstractCodeElementWithNesting<CodeBlock> {

    /**
     * Creates a new code block.
     * 
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }
    
    /**
     * Creates a new code block.
     * 
     * @param lineStart The line where this block starts.
     * @param lineEnd The line where this block ends.
     * @param sourceFile The source file in the source tree where this block originates from.
     * @param condition The immediate condition of this block. May be <code>null</code>.
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(int lineStart, int lineEnd, @NonNull File sourceFile, @Nullable Formula condition,
            @NonNull Formula presenceCondition) {
        
        super(presenceCondition);
        
        setSourceFile(sourceFile);
        setLineStart(lineStart);
        setLineEnd(lineEnd);
        setCondition(condition);
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
    protected CodeBlock(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "CodeBlock[start=" + getLineStart() + "; end=" + getLineEnd() + "; file=" + getSourceFile()
                + "; condition=" + getCondition() + "; pc=" + getPresenceCondition() + "]\n";
    }
    
}
