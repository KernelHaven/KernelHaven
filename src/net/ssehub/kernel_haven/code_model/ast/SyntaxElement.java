package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.List;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

public abstract class SyntaxElement implements CodeElement {

    private @NonNull Formula presenceCondition;
    private File sourceFile;
    
    public SyntaxElement(@NonNull Formula presenceCondition, File sourceFile) {
        this.presenceCondition = presenceCondition;
        this.sourceFile = sourceFile;
    }
    
    public Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    public @NonNull SyntaxElement getNestedElement(int index) {
        throw new IndexOutOfBoundsException();
    }
    
    public int getNestedElementCount() {
        return 0;
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    protected abstract String elementToString();
    
    protected String toString(String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = this.presenceCondition;
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append("[").append(conditionStr).append("] ");
        
        result.append(elementToString()).append('\n');
        
        indentation += '\t';
        
        for (int i = 0; i < getNestedElementCount(); i++) {
            SyntaxElement child = getNestedElement(i);
            result.append(child != null ? child.toString(indentation) : indentation + "null\n");
        }
        
        return result.toString();
    }
    
    /**
     * Line start is not supported by <a href="http://www.srcml.org">srcML</a>.
     * @return -1
     */
    @Override
    public int getLineStart() {
        return -1;
    }
    
    /**
     * Line end is not supported by <a href="http://www.srcml.org">srcML</a>.
     * @return -1
     */
    @Override
    public int getLineEnd() {
        return -1;
    }

    @Override
    public @NonNull File getSourceFile() {
        return sourceFile;
    }

    @Override
    public @Nullable Formula getCondition() {
        // TODO SE: @Adam please fix this
        return null;
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        // TODO SE: @Adam please fix this
        return null;
    }
    
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
}
