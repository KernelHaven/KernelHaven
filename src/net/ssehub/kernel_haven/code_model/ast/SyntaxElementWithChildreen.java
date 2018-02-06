package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link SyntaxElement} that has children.
 * 
 * @author Adam
 */
public abstract class SyntaxElementWithChildreen extends SyntaxElement {

    private @NonNull List<@NonNull SyntaxElement> nested;
    
    /**
     * Creates this {@link SyntaxElement} with the given presence condition.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public SyntaxElementWithChildreen(@NonNull Formula presenceCondition) {
        super(presenceCondition);
        this.nested = new LinkedList<>();
    }
    
    @Override
    public @NonNull SyntaxElement getNestedElement(int index) {
        return notNull(nested.get(index));
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
        if (!(element instanceof SyntaxElement)) {
            throw new IllegalArgumentException("Can only add SyntaxElements as child of SyntaxElement");
        }
        
        nested.add((SyntaxElement) element);
    }

}
