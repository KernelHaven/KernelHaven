package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * A single element of an AST.
 * </p>
 * <p>
 * This does not store a list of nested elements. Sub-classes that want children should subclass
 * {@link AbstractSyntaxElementWithChildreen} instead.
 * </p>
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElement implements ISyntaxElement {

    private @NonNull Formula presenceCondition;
    
    private @Nullable Formula condition;
    
    private @NonNull File sourceFile;
    
    private int lineStart;
    
    private int lineEnd;
    
    /**
     * Creates this {@link AbstractSyntaxElement} with the given presence condition.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractSyntaxElement(@NonNull Formula presenceCondition) {
        this.presenceCondition = presenceCondition;
        this.sourceFile = new File("<unknown>");
        this.condition = null;
        lineStart = -1;
        lineEnd = -1;
    }
    
    @Override
    public void setSourceFile(@NonNull File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    @Override
    public void setCondition(@Nullable Formula condition) {
        this.condition = condition;
    }
    
    @Override
    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }
    
    @Override
    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }
    
    @Override
    public @NonNull Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    @Override
    public @NonNull AbstractSyntaxElement getNestedElement(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public int getNestedElementCount() {
        return 0;
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    @Override
    public abstract @NonNull String elementToString(@NonNull String indentation);
    
    @Override
    public @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = this.condition;
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append("[").append(conditionStr).append("] ");
        
        result.append(elementToString(indentation));
        
        indentation += '\t';
        
        for (int i = 0; i < getNestedElementCount(); i++) {
            AbstractSyntaxElement child = getNestedElement(i);
            result.append(child.toString(indentation));
        }
        
        return notNull(result.toString());
    }
    
    @Override
    public int getLineStart() {
        return lineStart;
    }
    
    @Override
    public int getLineEnd() {
        return lineEnd;
    }

    @Override
    public @NonNull File getSourceFile() {
        return sourceFile;
    }

    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        // TODO SE: @Adam please fix this
        throw new RuntimeException("Serialization of ast.SyntaxElement not implement yet");
    }
    
    @Override
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
    
    @Override
    public Iterable<@NonNull ISyntaxElement> iterateNestedSyntaxElements() {
        return new Iterable<@NonNull ISyntaxElement>() {
            
            @Override
            public @NonNull Iterator<@NonNull ISyntaxElement> iterator() {
                return new Iterator<@NonNull ISyntaxElement>() {

                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < getNestedElementCount();
                    }

                    @Override
                    public ISyntaxElement next() {
                        return getNestedElement(index++);
                    }
                };
            }
        };
    }
    
}
