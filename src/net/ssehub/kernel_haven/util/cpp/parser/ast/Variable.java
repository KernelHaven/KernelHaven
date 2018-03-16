package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A variable.
 *
 * @author Adam
 */
public class Variable extends CppExpression {

    private @NonNull String name;
    
    /**
     * Creates a new variable.
     * 
     * @param name The name of the variable.
     */
    public Variable(@NonNull String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of this variable.
     * 
     * @return The name of this variable.
     */
    public @NonNull String getName() {
        return name;
    }

    @Override
    public <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitVariable(this);
    }
    
    @Override
    protected @NonNull String toString(@NonNull String indentation) {
        return indentation + "Variable " + name;
    }
    
}
