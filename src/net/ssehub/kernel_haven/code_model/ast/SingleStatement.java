package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class SingleStatement extends AbstractSyntaxElement {

    private @NonNull ICode code;
    
    public SingleStatement(@NonNull Formula presenceCondition, @NonNull ICode code) {
        super(presenceCondition);
        this.code = code;
    }
    
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
