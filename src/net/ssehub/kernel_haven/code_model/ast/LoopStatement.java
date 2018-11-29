package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a loop statement. The nested children inside this element are the loop body.
 * 
 * @author El-Sharkawy
 */
public class LoopStatement extends AbstractSyntaxElementWithNesting {

    /**
     * The type of loop.
     */
    public static enum LoopType {
        WHILE, DO_WHILE, FOR;
    }

    private @NonNull ICode condition;
    
    private @NonNull LoopType type;
    
    /**
     * Creates a {@link LoopStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param condition The condition of this loop.
     * @param type The type of loop that this is.
     */
    public LoopStatement(@NonNull Formula presenceCondition, @NonNull ICode condition,
            @NonNull LoopType type) {
        
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
    }
    
    /**
     * Returns which type of loop this is.
     * 
     * @return The {@link Type} of this loop.
     */
    public @NonNull ICode getLoopCondition() {
        return condition;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-loop\n" + condition.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitLoopStatement(this);
    }

}
