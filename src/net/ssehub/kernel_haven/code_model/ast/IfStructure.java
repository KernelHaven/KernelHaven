package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class IfStructure extends SyntaxElementWithChildreen {
    
    private @NonNull SyntaxElement condition;
    
    public IfStructure(@NonNull Formula presenceCondition, @NonNull File sourceFile, @NonNull SyntaxElement condition) {
        super(presenceCondition, sourceFile);
        this.condition = condition;
    }
    
    public @NonNull SyntaxElement getIfCondition() {
        return condition;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return "if\n" + condition.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitIfStructure(this);
    }

}
