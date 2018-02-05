package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class SwitchStatement extends SyntaxElementWithChildreen {

    private SyntaxElement header;
    
    public SwitchStatement(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement header) {
        super(presenceCondition, sourceFile);
        this.header = header;
    }
    
    public SyntaxElement getHeader() {
        return header;
    }

    @Override
    protected String elementToString() {
        return "Switch\n" + (header == null ? "\t\t\t\tnull" : header.toString("\t\t\t\t")); // TODO
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

}
