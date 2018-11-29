package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * A single element of an AST.
 * </p>
 * <p>
 * This does not store a list of nested elements. Sub-classes that want children should subclass
 * {@link AbstractSyntaxElementWithNesting} instead.
 * </p>
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElementNoNesting extends AbstractCodeElement<ISyntaxElement> implements ISyntaxElement {

    /**
     * Creates this {@link AbstractSyntaxElementNoNesting} with the given presence condition.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractSyntaxElementNoNesting(@NonNull Formula presenceCondition) {
        super(presenceCondition);
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
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        // TODO SE: @Adam please fix this
        throw new RuntimeException("Serialization of ast.SyntaxElement not implement yet");
    }
    
    @Override
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
    
}
