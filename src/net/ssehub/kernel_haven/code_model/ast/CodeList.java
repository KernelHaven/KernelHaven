package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class CodeList extends SyntaxElementWithChildreen {

    public CodeList(@NonNull Formula presenceCondition, File sourceFile) {
        super(presenceCondition, sourceFile);
    }

    @Override
    protected String elementToString() {
        return "CodeList";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCodeList(this);
    }
    
}
