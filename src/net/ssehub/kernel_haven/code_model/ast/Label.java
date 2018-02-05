package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a label (jump target). This is very similar to a {@link SingleStatement}, however, we want to avoid
 * that this class is counted as statement by default in later analysis.
 * @author El-Sharkawy
 *
 */
public class Label extends SyntaxElement {

    private SyntaxElement code;
    
    public Label(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement code) {
        super(presenceCondition, sourceFile);
        this.code = code;
    }
    
    public SyntaxElement getCode() {
        return code;
    }
    
    @Override
    protected String elementToString() {
        return "Label:\n" + code.toString("\t\t\t\t");
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitLabel(this);
    }

}
