package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.maybeNull;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.List;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

public abstract class SyntaxElement implements CodeElement {

    private @NonNull Formula presenceCondition;
    
    private @NonNull File sourceFile;
    
    public SyntaxElement(@NonNull Formula presenceCondition, @NonNull File sourceFile) {
        this.presenceCondition = presenceCondition;
        this.sourceFile = sourceFile;
    }
    
    @Override
    public @NonNull Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    @Override
    public @NonNull SyntaxElement getNestedElement(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
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
    
    protected abstract @NonNull String elementToString(@NonNull String indentation);
    
    protected @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = maybeNull(getPresenceCondition()); // TODO
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append("[").append(conditionStr).append("] ");
        
        result.append(elementToString(indentation)).append('\n');
        
        indentation += '\t';
        
        for (int i = 0; i < getNestedElementCount(); i++) {
            SyntaxElement child = getNestedElement(i);
            result.append(child.toString(indentation));
        }
        
        return notNull(result.toString());
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
        throw new RuntimeException("Serialization of ast.SyntaxElement not implement yet");
    }
    
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
}
