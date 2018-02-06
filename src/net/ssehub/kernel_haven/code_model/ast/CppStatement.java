package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

public class CppStatement extends SyntaxElement {

    public static enum Type {
        DEFINE, UNDEF, INCLUDE, PRAGMA, ERROR, WARNING, LINE, EMPTY
    }
    
    private @NonNull Type type;
    
    private @Nullable SyntaxElement expression;
    
    public CppStatement(@NonNull Formula presenceCondition, @NonNull Type type, @Nullable SyntaxElement expression) {
        super(presenceCondition);
        
        this.type = type;
        this.expression = expression;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type + "\n";
        
        SyntaxElement expression = this.expression;
        if (expression != null) {
            result += expression.toString(indentation + "\t");
        }
        
        return result;
    }
    
    public @NonNull Type getType() {
        return type;
    }
    
    public @Nullable SyntaxElement getExpression() {
        return expression;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppStatement(this);
    }

}
