package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a label (jump target). This is very similar to a {@link SingleStatement}, however, we want to avoid
 * that this class is counted as statement by default in later analysis.
 * @author El-Sharkawy
 *
 */
public class Label extends SyntaxElement {

    private @NonNull SyntaxElement code;
    
    public Label(@NonNull Formula presenceCondition, @NonNull SyntaxElement code) {
        super(presenceCondition);
        this.code = code;
    }
    
    public @NonNull SyntaxElement getCode() {
        return code;
    }
    
    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return "Label:\n" + code.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitLabel(this);
    }

}
