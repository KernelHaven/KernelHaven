package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Abstract superclass for all {@link CodeElement}s. This implementation of the {@link CodeElement} interface has
 * no support for nested elements. Use {@link AbstractCodeElementWithNesting} for that.
 * 
 * @param <NestedType> The type of the nested elements.
 * 
 * @author Adam
 */
public abstract class AbstractCodeElement<NestedType extends CodeElement<NestedType>>
        implements CodeElement<NestedType> {

    private static final @NonNull File UNKNOWN = new File("<unknown>");
    
    private @NonNull File sourceFile;
    
    private int lineStart;
    
    private int lineEnd;
    
    private @Nullable Formula condition;
    
    private @NonNull Formula presenceCondition;
    
    /**
     * Creates this element with the given presence condition. Source file is unknown, line numbers are -1 and
     * condition is <code>null</code>.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractCodeElement(@NonNull Formula presenceCondition) {
        this.sourceFile = UNKNOWN;
        this.lineStart = -1;
        this.lineEnd = -1;
        this.condition = null;
        this.presenceCondition = presenceCondition;
    }
    

    @Override
    public @NonNull File getSourceFile() {
        return sourceFile;
    }

    /**
     * Changes the source file.
     * 
     * @param sourceFile The new source file.
     * 
     * @see #getSourceFile()
     */
    public void setSourceFile(@NonNull File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    @Override
    public int getLineStart() {
        return lineStart;
    }

    @Override
    public void setLineStart(int start) {
        this.lineStart = start;
    }

    @Override
    public int getLineEnd() {
        return lineEnd;
    }

    @Override
    public void setLineEnd(int end) {
        this.lineEnd = end;
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    /**
     * Changes the immediate condition of this element.
     * 
     * @param condition The new condition.
     * 
     * @see #getCondition()
     */
    protected void setCondition(Formula condition) {
        this.condition = condition;
    }

    @Override
    public @NonNull Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    /**
     * Changes the presence condition of this element.
     * 
     * @param presenceCondition The new presence condition.
     * 
     * @see #getPresenceCondition()
     */
    protected void setPresenceCondition(@NonNull Formula presenceCondition) {
        this.presenceCondition = presenceCondition;
    }

    @Override
    public int getNestedElementCount() {
        return 0;
    }

    @Override
    public @NonNull NestedType getNestedElement(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void addNestedElement(@NonNull NestedType element) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public Iterator<@NonNull NestedType> iterator() {
        return new Iterator<@NonNull NestedType>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public @NonNull NestedType next() {
                throw new NoSuchElementException();
            }
            
        };
    }
    
    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    @Override
    public @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = getCondition();
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append("[").append(conditionStr).append("] ");
        
        result.append(elementToString(indentation));
        
        indentation += '\t';
        
        for (NestedType child : this) {
            result.append(child.toString(indentation));
        }
        
        return notNull(result.toString());
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(lineStart) + Integer.hashCode(lineEnd) + sourceFile.hashCode()
            + (condition != null ? condition.hashCode() : 54234) + presenceCondition.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof AbstractCodeElement<?>) {
            AbstractCodeElement<?> other = (AbstractCodeElement<?>) obj;
            
            equal = this.lineStart == other.lineStart && this.lineEnd == other.lineEnd;
            if (equal) {
                equal &= this.sourceFile.equals(other.sourceFile);
            }
            
            if (equal) {
                Formula condition = this.condition;
                if (condition == null) {
                    equal &= other.condition == null;
                } else {
                    equal &= condition.equals(other.condition);
                }
            }
            
            if (equal) {
                equal &= this.presenceCondition.equals(other.presenceCondition);
            }
        }
        return equal;
    }
    
}
