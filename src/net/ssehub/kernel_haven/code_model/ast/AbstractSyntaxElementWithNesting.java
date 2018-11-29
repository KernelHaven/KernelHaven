package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.AbstractCodeElementWithNesting;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link AbstractSyntaxElementNoNesting} that has children.
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElementWithNesting extends AbstractCodeElementWithNesting<ISyntaxElement>
        implements ISyntaxElement {

    /**
     * Creates this {@link AbstractSyntaxElementNoNesting} with the given presence
     * condition.
     * 
     * @param presenceCondition
     *            The presence condition of this element.
     */
    public AbstractSyntaxElementWithNesting(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }
    
    @Override
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException {
        
        super.replaceNestedElement(oldElement, newElement);
    }
    
    @Override
    public void setSourceFile(@NonNull File sourceFile) {
        super.setSourceFile(sourceFile);
    }

    @Override
    public void setCondition(@Nullable Formula condition) {
        super.setCondition(condition);
    }

    @Override
    public void setPresenceCondition(@NonNull Formula presenceCondition) {
        super.setPresenceCondition(presenceCondition);
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        // TODO SE: @Adam please fix this
        throw new RuntimeException("Serialization of ast.SyntaxElement not implement yet");
    }

    @Override
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);

}
