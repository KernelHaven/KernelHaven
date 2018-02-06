package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a label (jump target). This is very similar to a {@link SingleStatement}, however, we want to avoid
 * that this class is counted as statement by default in later analysis.
 * 
 * @author El-Sharkawy
 */
public class Label extends AbstractSyntaxElement {

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

}
