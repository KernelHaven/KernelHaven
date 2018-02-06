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

    private @NonNull ICode code;
    
    /**
     * Creates a {@link Statement}.
     * 
     * @param presenceCondition The presence condition.
     * @param code The code string that this statement represents.
     */
    public SingleStatement(@NonNull Formula presenceCondition, @NonNull ICode code) {
        super(presenceCondition);
        this.code = code;
    }
    
    /**
     * Returns the code string that this statement represents.
     * 
     * @return The code string that this statement represents.
     */
    public @NonNull ICode getCode() {
        return code;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Statement:\n" + code.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSingleStatement(this);
    }

}
