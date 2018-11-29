package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A superclass for all {@link CodeElement} that have nested elements.
 * 
 * @author Adam
 *
 * @param <NestedType> The type of the nested elements.
 */
public abstract class AbstractCodeElementWithNesting<NestedType extends CodeElement<NestedType>>
        extends AbstractCodeElement<NestedType> {

    private @NonNull List<@NonNull NestedType> nested;
    
    /**
     * Creates this element with the given presence condition. Source file is unknown, line numbers are -1 and
     * condition is <code>null</code>.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractCodeElementWithNesting(@NonNull Formula presenceCondition) {
        super(presenceCondition);
        
        this.nested = new LinkedList<>();
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }

    @Override
    public @NonNull NestedType getNestedElement(int index) throws IndexOutOfBoundsException {
        return notNull(nested.get(index));
    }

    @Override
    public void addNestedElement(@NonNull NestedType element) {
        nested.add(element);
    }
    
    /**
     * Replaces the given nested element with the given new element. This method should only be called by the extractor
     * that creates the AST.
     * 
     * @param oldElement The old element to replace.
     * @param newElement The new element to replace with.
     * 
     * @throws NoSuchElementException If oldElement is not nested inside this one.
     */
    protected void replaceNestedElement(@NonNull NestedType oldElement, @NonNull NestedType newElement)
            throws NoSuchElementException {
        
        int index = nested.indexOf(oldElement);
        if (index < 0) {
            throw new NoSuchElementException();
        }
        
        nested.set(index, newElement);
    }
    
    @Override
    public Iterator<@NonNull NestedType> iterator() {
        return nested.iterator();
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + nested.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof AbstractCodeElementWithNesting && super.equals(obj)) {
            AbstractCodeElementWithNesting<?> other = (AbstractCodeElementWithNesting<?>) obj;
            equal = this.nested.equals(other.nested);
        }
        
        return equal;
    }

}
