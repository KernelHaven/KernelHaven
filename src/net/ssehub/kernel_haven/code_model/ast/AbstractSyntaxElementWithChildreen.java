package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link AbstractSyntaxElement} that has children.
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElementWithChildreen extends AbstractSyntaxElement {

    private @NonNull List<@NonNull AbstractSyntaxElement> nested;
    
    /**
     * Creates this {@link AbstractSyntaxElement} with the given presence condition.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractSyntaxElementWithChildreen(@NonNull Formula presenceCondition) {
        super(presenceCondition);
        this.nested = new LinkedList<>();
    }
    
    @Override
    public @NonNull AbstractSyntaxElement getNestedElement(int index) {
        return notNull(nested.get(index));
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
        if (!(element instanceof AbstractSyntaxElement)) {
            throw new IllegalArgumentException("Can only add SyntaxElements as child of SyntaxElement");
        }
        
        nested.add((AbstractSyntaxElement) element);
    }

    @Override
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException {
        
        if (!(newElement instanceof AbstractSyntaxElement)) {
            throw new IllegalArgumentException("Can only add SyntaxElements as child of SyntaxElement");
        }
        
        int index = nested.indexOf(oldElement);
        if (index < 0) {
            throw new NoSuchElementException();
        }
        
        nested.set(index, (AbstractSyntaxElement) newElement);
    }
    
}
