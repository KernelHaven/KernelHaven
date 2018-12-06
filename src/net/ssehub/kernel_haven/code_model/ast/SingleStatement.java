package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.beans.Statement;
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
 * A single statement.
 *
 * @author Adam
 */
public class SingleStatement extends AbstractSyntaxElementNoNesting {
    
    /**
     * The type of statement.
     */
    public static enum Type {
        
        /**
         * Any kind of instruction (e.g. assignment or return)
         */
        INSTRUCTION,
        
        /**
         * A declaration of some kind (e.g. declaration of a struct). Basically anything that declares a variable.
         *  If a function is declared, then {@link #FUNCTION_DECLARATION} should be used instead.
         */
        DECLARATION,
        
        /**
         * A declaration of a function. This is used instead of {@link #DECLARATION} when a function is declared.
         */
        FUNCTION_DECLARATION,
        
        /**
         * A call to a preprocessor macro.
         */
        PREPROCESSOR_MACRO,
        
        /**
         * Unspecified or anything not covered by the other types.
         */
        OTHER,
        
    }

    private @NonNull ICode code;
    
    private @NonNull Type type;
    
    /**
     * Creates a {@link Statement}.
     * 
     * @param presenceCondition The presence condition.
     * @param code The code string that this statement represents.
     * @param type The type of this statement. Use {@link Type#OTHER} if unsure.
     */
    public SingleStatement(@NonNull Formula presenceCondition, @NonNull ICode code, @NonNull Type type) {
        super(presenceCondition);
        this.code = code;
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
    protected SingleStatement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.type = Type.valueOf(json.getString("statementType"));
        this.code = (ICode) deserializeFunction.apply(json.getObject("statement"));
    }
    
    /**
     * Returns the code string that this statement represents.
     * 
     * @return The code string that this statement represents.
     */
    public @NonNull ICode getCode() {
        return code;
    }
    
    /**
     * Returns the type of statement that this single statement represents.
     * 
     * @return The type of statement that this is.
     */
    public @NonNull Type getType() {
        return type;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type + "-Statement:\n" + code.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSingleStatement(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + type.hashCode() + hasher.hashCode((AbstractCodeElement<?>) code);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof SingleStatement && super.equals(other, checker);
        
        if (equal) {
            SingleStatement o = (SingleStatement) other;
            
            equal = this.type == o.type && checker.isEqual(
                    (AbstractCodeElement<?>) this.code, (AbstractCodeElement<?>) o.code);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("statementType", new JsonString(notNull(type.name())));
        result.putElement("statement", serializeFunction.apply(code));
    }

}
