package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

public class CppBlock extends SyntaxElementWithChildreen {
    
    /**
     * Denotes the exact kind of preprocessor element.
     * @author El-Sharkawy
     *
     */
    public static enum Type {
        IF, IFDEF, IFNDEF, ELSEIF, ELSE;
    }

    private @Nullable Formula condition;
    
    private @NonNull Type type;
    
    public CppBlock(@NonNull Formula presenceCondition, @Nullable Formula condition,
            @NonNull Type type) {
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    public @NonNull Type getType() {
        return type;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type.name();
        
        Formula condition = this.condition;
        if (condition != null) {
            result += " " + condition.toString();
        }
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppBlock(this);
    }

}
