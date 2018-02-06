package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * A single element of an abstract syntax tree (AST). Other {@link SyntaxElement}s can be nested inside of this element.
 * </p>
 * <p>
 * The ASTs created by this class are not fully parsed. Some leaf-nodes are instances of {@link Code}, which contain
 * unparsed code strings. If unparsed code elements contain variability (e.g. ifdef), then {@link CodeList} is used,
 * which contains {@link Code} and {@link CppBlock} children (the {@link CppBlock}s contain {@link Code} or more
 * {@link CppBlock}s).
 * </p>
 * <p>
 * By default, this does not store a list of nested elements. Sub-classes that want children should subclass
 * {@link SyntaxElementWithChildreen} instead.
 * </p>
 * 
 * @author Adam
 */
public abstract class SyntaxElement implements CodeElement {

    private @NonNull Formula presenceCondition;
    
    private @Nullable Formula condition;
    
    private @NonNull File sourceFile;
    
    private int lineStart;
    
    private int lineEnd;
    
    /**
     * Creates this {@link SyntaxElement} with the given presence condition.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public SyntaxElement(@NonNull Formula presenceCondition) {
        this.presenceCondition = presenceCondition;
        this.sourceFile = new File("<unknown>");
        this.condition = null;
        lineStart = -1;
        lineEnd = -1;
    }
    
    /**
     * Sets the source file that this element comes from.
     * 
     * @param sourceFile The source element that this element comes from.
     * 
     * @see #getSourceFile()
     */
    public void setSourceFile(@NonNull File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    /**
     * Sets the immediate condition of this element.
     * 
     * @param condition The immediate condition of this element.
     * 
     * @see #getCondition()
     */
    public void setCondition(@Nullable Formula condition) {
        this.condition = condition;
    }
    
    /**
     * Sets the starting line of this element.
     * 
     * @param lineStart The starting line.
     * 
     * @see #getLineStart()
     */
    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }
    
    /**
     * Sets the end line of this element.
     * 
     * @param lineEnd The end line.
     * 
     * @see #getLineEnd()
     */
    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
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
    public void addNestedElement(@NonNull CodeElement element) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    /**
     * Converts this single element into a string. This should not consider nested elements. However, other attributes
     * may be added in additional lines with the given indentation + "\t".
     * 
     * @param indentation The initial indentation to use for multiple lines.
     * 
     * @return A string representation of this single element.
     */
    protected abstract @NonNull String elementToString(@NonNull String indentation);
    
    /**
     * Converts this element and all its nested elements into a string.
     * 
     * @param indentation The initial indentation to use.
     * 
     * @return A string representation of the full hierarchy starting from this element.
     */
    protected @NonNull String toString(@NonNull String indentation) {
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
            SyntaxElement child = getNestedElement(i);
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
    
    /**
     * Accept this visitor.
     * 
     * @param visitor The visitor to accept.
     */
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
    
    
    /**
     * Iterates over the {@link SyntaxElement}s nested inside this element. Not recursively.
     * 
     * @return An iterable over the nested elements.
     */
    public Iterable<@NonNull SyntaxElement> iterateNestedSyntaxElements() {
        return new Iterable<@NonNull SyntaxElement>() {
            
            @Override
            public @NonNull Iterator<@NonNull SyntaxElement> iterator() {
                return new Iterator<@NonNull SyntaxElement>() {

                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < getNestedElementCount();
                    }

                    @Override
                    public SyntaxElement next() {
                        return getNestedElement(index++);
                    }
                };
            }
        };
    }
    
}
