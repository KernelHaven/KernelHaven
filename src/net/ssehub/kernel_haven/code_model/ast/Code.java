package net.ssehub.kernel_haven.code_model.ast;

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
 * Represents unparsed string of code inside the AST. See class comment of {@link ISyntaxElement}.
 * 
 * @author Adam
 */
public class Code extends AbstractSyntaxElementNoNesting implements ICode {

    private @NonNull String text;
    
    /**
     * Creates this {@link Code}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param text The unparsed code string.
     */
    public Code(@NonNull Formula presenceCondition, @NonNull String text) {
        super(presenceCondition);
        this.text = text;
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
    protected Code(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.text = json.getString("code");
    }
    
    /**
     * Returns the unparsed code string.
     * 
     * @return The unparsed code string.
     */
    public @NonNull String getText() {
        return text;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return text + "\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCode(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + text.hashCode();
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof Code && super.equals(other, checker);
        
        if (equal) {
            Code o = (Code) other;
            
            equal = this.text.equals(o.text);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("code", new JsonString(text));
    }
    
}
