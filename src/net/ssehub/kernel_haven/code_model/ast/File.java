package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class File extends AbstractSyntaxElementWithChildreen {

    public File(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "File\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFile(this);
    }

}
