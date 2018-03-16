package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * A function call.
 *
 * @author Adam
 */
public class FunctionCall extends CppExpression {

    private String functionName;
    
    private CppExpression argument;
    
    /**
     * Creates a new {@link FunctionCall}.
     * 
     * @param functionName The name of the called function.
     * @param argument The expression inside the brackets of the call.
     */
    public FunctionCall(String functionName, CppExpression argument) {
        this.functionName = functionName;
        this.argument = argument;
    }
    
    /**
     * Returns the name of the called function.
     * 
     * @return The name of the called function.
     */
    public String getFunctionName() {
        return functionName;
    }
    
    /**
     * Returns the expression inside the brackets of the function call.
     * 
     * @return The argument of the function. May be <code>null</code>.
     */
    public CppExpression getArgument() {
        return argument;
    }
    
    /**
     * Overrides the argument (parameter) for the function.
     * 
     * @param argument The new argument.
     */
    public void setArgument(CppExpression argument) {
        this.argument = argument;
    }
    
    @Override
    public <T> T accept(ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitFunctionCall(this);
    }

    @Override
    protected String toString(String indentation) {
        StringBuilder result = new StringBuilder(indentation).append("Function ").append(functionName);
        
        indentation += '\t';
        result.append('\n').append(argument.toString(indentation));
    
        return result.toString();
    }
    
}
