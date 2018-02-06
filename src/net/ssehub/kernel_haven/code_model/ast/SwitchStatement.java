package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class SwitchStatement extends AbstractSyntaxElementWithChildreen {

    private @NonNull ICode header;
    
    public SwitchStatement(@NonNull Formula presenceCondition, @NonNull ICode header) {
        
        super(presenceCondition);
        this.header = header;
    }
    
    public @NonNull ICode getHeader() {
        return header;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Switch\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

}
