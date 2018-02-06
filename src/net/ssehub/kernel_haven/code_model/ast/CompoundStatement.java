package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A block of statements. the individual statements are the nested children inside this element.
 *
 * @author Adam
 */
public class CompoundStatement extends AbstractSyntaxElementWithChildreen {

    /**
     * Creates a {@link CompoundStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public CompoundStatement(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "CompoundStatement\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCompoundStatement(this);
    }

}
