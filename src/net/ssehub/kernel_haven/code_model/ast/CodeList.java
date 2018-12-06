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
