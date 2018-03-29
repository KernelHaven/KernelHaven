package net.ssehub.kernel_haven.code_model.ast;

import java.beans.Statement;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A single statement.
 *
 * @author Adam
 */
public class SingleStatement extends AbstractSyntaxElement {
    
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

}
