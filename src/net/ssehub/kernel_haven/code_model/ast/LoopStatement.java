package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a loop.
 * @author El-Sharkawy
 *
 */
public class LoopStatement extends SyntaxElementWithChildreen {
    
    public static enum LoopType {
        WHILE, DO_WHILE, FOR;
    }

    private @NonNull SyntaxElement condition;
    
    private @NonNull LoopType type;
    
    public LoopStatement(@NonNull Formula presenceCondition, @NonNull SyntaxElement condition,
            @NonNull LoopType type) {
        
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
    }
    
    public @NonNull SyntaxElement getLoopCondition() {
        return condition;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-loop\n" + condition.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitLoopStatement(this);
    }

}
