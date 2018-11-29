package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A single statement for the C preprocessor (CPP). This represents everything except #if etc.
 *
 * @author Adam
 */
public class CppStatement extends AbstractSyntaxElementNoNesting {

    /**
     * The type of preprocessor directive.
     */
    public static enum Type {
        DEFINE, UNDEF, INCLUDE, PRAGMA, ERROR, WARNING, LINE, EMPTY
    }
    
    private @NonNull Type type;
    
    private @Nullable ICode expression;

    /**
     * Creates a {@link CppStatement}.
     * 
     * @param presenceCondition The presence condition.
     * @param type The type of preprocessor directive used.
     * @param expression The text expression following the directive. <code>null</code> if there is none.
     */
    public CppStatement(@NonNull Formula presenceCondition, @NonNull Type type, @Nullable ICode expression) {
        super(presenceCondition);
        
        this.type = type;
        this.expression = expression;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type + "\n";
        
        ICode expression = this.expression;
        if (expression != null) {
            result += expression.toString(indentation + "\t");
        }
        
        return result;
    }
    
    /**
     * Returns the type of preprocessor directive.
     * 
     * @return The type of directive that this statement represents.
     */
    public @NonNull Type getType() {
        return type;
    }
    
    /**
     * Returns the text expression following the directive.
     * 
     * @return The text expression following the directive. <code>null</code> if there is none.
     */
    public @Nullable ICode getExpression() {
        return expression;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppStatement(this);
    }

}
