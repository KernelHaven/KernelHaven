package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class Code extends SyntaxElement {

    private @NonNull String text;
    
    public Code(@NonNull Formula presenceCondition, @NonNull File sourceFile, @NonNull String text) {
        super(presenceCondition, sourceFile);
        this.text = text;
    }
    
    public @NonNull String getText() {
        return text;
    }
    
    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return text;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCode(this);
    }
    
}
