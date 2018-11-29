package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.AbstractCodeElementWithNesting;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link AbstractSyntaxElement} that has children.
 * 
 * @author Adam
 */
abstract class AbstractSyntaxElementWithChildreen extends AbstractCodeElementWithNesting<ISyntaxElement>
        implements ISyntaxElement {

    /**
     * Creates this {@link AbstractSyntaxElement} with the given presence
     * condition.
     * 
     * @param presenceCondition
     *            The presence condition of this element.
     */
    public AbstractSyntaxElementWithChildreen(@NonNull Formula presenceCondition) {
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
    public @NonNull String toString() {
        return toString("");
    }

    @Override
    public abstract @NonNull String elementToString(@NonNull String indentation);

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

        for (ISyntaxElement child : this) {
            result.append(child.toString(indentation));
        }

        return notNull(result.toString());
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        // TODO SE: @Adam please fix this
        throw new RuntimeException("Serialization of ast.SyntaxElement not implement yet");
    }

    @Override
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);

}
