package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class File extends SyntaxElementWithChildreen {

    public File(@NonNull Formula presenceCondition, java.io.File sourceFile) {
        super(presenceCondition, sourceFile);
    }

    @Override
    protected String elementToString() {
        return "File";
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitFile(this);
    }

}
