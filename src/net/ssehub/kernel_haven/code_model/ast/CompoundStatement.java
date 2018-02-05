package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class CompoundStatement extends SyntaxElementWithChildreen {

    public CompoundStatement(@NonNull Formula presenceCondition, @NonNull File sourceFile) {
        super(presenceCondition, sourceFile);
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return "CompoundStatement";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCompoundStatement(this);
    }

}
