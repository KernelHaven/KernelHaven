package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElementWithNesting;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link AbstractSyntaxElementNoNesting} that has children.
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElementWithNesting extends AbstractCodeElementWithNesting<ISyntaxElement>
        implements ISyntaxElement {

    /**
     * Creates this {@link AbstractSyntaxElementNoNesting} with the given presence
     * condition.
     * 
     * @param presenceCondition
     *            The presence condition of this element.
     */
    public AbstractSyntaxElementWithNesting(@NonNull Formula presenceCondition) {
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
    protected AbstractSyntaxElementWithNesting(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
    }
    
    @Override
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException {
        
        super.replaceNestedElement(oldElement, newElement);
    }
    
    @Override
    public void setSourceFile(@NonNull File sourceFile) {
        super.setSourceFile(sourceFile);
    }

    @Override
    public void setCondition(@Nullable Formula condition) {
        super.setCondition(condition);
    }

    @Override
    public void setPresenceCondition(@NonNull Formula presenceCondition) {
        super.setPresenceCondition(presenceCondition);
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        throw new RuntimeException("CSV serialization of ast.SyntaxElement not implement yet");
    }

    @Override
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);

}
