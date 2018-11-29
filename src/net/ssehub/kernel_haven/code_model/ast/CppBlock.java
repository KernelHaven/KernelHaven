package net.ssehub.kernel_haven.code_model.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A preprocessor block with a variability condition (e.g. an #ifdef block). The nested children in this element
 * are the elements inside the preprocessor block.
 *
 * @author El-Sharkawy
 */
public class CppBlock extends AbstractSyntaxElementWithNesting implements ICode {
    
    /**
     * The kind of preprocessor block.
     */
    public static enum Type {
        IF, IFDEF, IFNDEF, ELSEIF, ELSE;
    }

    private @Nullable Formula condition;
    
    private @NonNull Type type;
    
    private List<CppBlock> siblings;
    
    /**
     * Creates a {@link CppBlock}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param condition The variability condition of this block.
     * @param type The {@link Type} of preprocessor block that this is.
     */
    public CppBlock(@NonNull Formula presenceCondition, @Nullable Formula condition, @NonNull Type type) {
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
        siblings = new ArrayList<>();
    }
    
    /**
     * Adds another if, elif, else block, which belongs to the same block.
     * @param sibling Another if, elif, else block, which belongs to the this block structure.
     */
    public void addSibling(CppBlock sibling) {
        siblings.add(sibling);
    }
    
    /**
     * Returns an unmodifiable iterator for iterating through all the siblings starting at the opening <tt>if</tt>.
     * @return An unmodifiable iterator for iterating through all the siblings.
     */
    public Iterator<CppBlock> getSiblingsIterator() {
        // Copied from: java.util.Collections.UnmodifiableCollection<E>
        return new Iterator<CppBlock>() {
            private final Iterator<CppBlock> itr = siblings.iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }
            
            @Override
            public CppBlock next() {
                return itr.next();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void forEachRemaining(Consumer<? super CppBlock> action) {
                // Use backing collection version
                itr.forEachRemaining(action);
            }
        };
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    /**
     * Returns the {@link Type} of preprocessor block that this is.
     * 
     * @return The {@link Type} of preprocessor block that this is.
     */
    public @NonNull Type getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type.name();
        
        Formula condition = this.condition;
        if (condition != null) {
            result += " " + condition.toString();
        }
        return result + "\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppBlock(this);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + type.hashCode() + siblings.hashCode()
                + (condition != null ? condition.hashCode() : 123);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CppBlock && super.equals(obj)) {
            CppBlock other = (CppBlock) obj;
            equal = this.type.equals(other.type) && this.siblings.equals(other.siblings);
            
            if (equal && this.condition != null) {
                equal &= this.condition.equals(other.condition);
            } else {
                equal &= other.condition == null;
            }
        }
        
        return equal;
    }

}
