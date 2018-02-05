package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class SingleStatement extends SyntaxElement {

    private SyntaxElement code;
    
    public SingleStatement(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement code) {
        super(presenceCondition, sourceFile);
        this.code = code;
    }
    
    public SyntaxElement getCode() {
        return code;
    }
    
    @Override
    protected String elementToString() {
        return "Statement:\n" + code.toString("\t\t\t\t");
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitSingleStatement(this);
    }

}
