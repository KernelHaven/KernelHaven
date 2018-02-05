package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;

public class Code extends SyntaxElement {

    private String text;
    
    public Code(Formula presenceCondition, File sourceFile, String text) {
        super(presenceCondition, sourceFile);
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    @Override
    protected String elementToString() {
        return text;
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitCode(this);
    }
    
}
