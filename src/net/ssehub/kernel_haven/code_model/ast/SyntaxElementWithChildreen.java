package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public abstract class SyntaxElementWithChildreen extends SyntaxElement {

    private List<SyntaxElement> nested;
    
    public SyntaxElementWithChildreen(@NonNull Formula presenceCondition, File sourceFile) {
        super(presenceCondition, sourceFile);
        this.nested = new LinkedList<>();
    }
    
    @Override
    public @NonNull SyntaxElement getNestedElement(int index) {
        return nested.get(index);
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
        nested.add((SyntaxElement) element);
    }

}
