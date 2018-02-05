package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class SingleStatement extends SyntaxElement {

    private @NonNull SyntaxElement code;
    
    public SingleStatement(@NonNull Formula presenceCondition, @NonNull File sourceFile, @NonNull SyntaxElement code) {
        super(presenceCondition, sourceFile);
        this.code = code;
    }
    
    public @NonNull SyntaxElement getCode() {
        return code;
    }
    
    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return "Statement:\n" + code.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSingleStatement(this);
    }

}
