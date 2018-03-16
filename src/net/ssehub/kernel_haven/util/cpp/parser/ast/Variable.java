package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * A variable.
 *
 * @author Adam
 */
public class Variable extends CppExpression {

    private String name;
    
    /**
     * Creates a new variable.
     * 
     * @param name The name of the variable.
     */
    public Variable(String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of this variable.
     * 
     * @return The name of this variable.
     */
    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitVariable(this);
    }
    
    @Override
    protected String toString(String indentation) {
        return indentation + "Identifier " + name;
    }
    
}
