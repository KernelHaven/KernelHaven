package net.ssehub.kernel_haven.code_model.ast;

import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a label (jump target). This is very similar to a {@link SingleStatement}, however, we want to avoid
 * that this class is counted as statement by default in later analysis.
 * 
 * @author El-Sharkawy
 */
public class Label extends AbstractSyntaxElementNoNesting {

    private @NonNull ICode code;

    /**
     * Creates a label.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param code The code string that defines this label.
     */
    public Label(@NonNull Formula presenceCondition, @NonNull ICode code) {
        super(presenceCondition);
        this.code = code;
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
    protected Label(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.code = (ICode) deserializeFunction.apply(json.getObject("label"));
    }
    
    /**
     * Returns the code string that defines this label.
     * 
     * @return The code string that defines this label.
     */
    public @NonNull ICode getCode() {
        return code;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Label:\n" + code.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitLabel(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + hasher.hashCode((AbstractCodeElement<?>) code);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof Label && super.equals(other, checker);
        
        if (equal) {
            Label o = (Label) other;
            
            equal = checker.isEqual((AbstractCodeElement<?>) this.code, (AbstractCodeElement<?>) o.code);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("label", serializeFunction.apply(code));
    }

}
