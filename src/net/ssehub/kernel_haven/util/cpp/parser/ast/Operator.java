package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.cpp.parser.CppOperator;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * An operator.
 *
 * @author Adam
 */
public class Operator extends CppExpression {

    private CppOperator operator;
    
    private CppExpression leftSide;
    
    private CppExpression rightSide;
    
    /**
     * Creates a new operator.
     * 
     * @param operator The operator.
     */
    public Operator(CppOperator operator) {
        this.operator = operator;
    }
    
    /**
     * Returns the operator that this node represents.
     * 
     * @return The operator that this node represents.
     */
    public CppOperator getOperator() {
        return operator;
    }
    
    /**
     * Changes the opeartor.
     * 
     * @param operator The new operator.
     */
    public void setOperator(CppOperator operator) {
        this.operator = operator;
    }
    
    /**
     * Returns the left side of this operator. This is the only "side" for unary operators.
     * 
     * @return The left side of this operator.
     */
    public CppExpression getLeftSide() {
        return leftSide;
    }
    
    /**
     * Sets the left side of the operator. This is the only "side" for unary operators.
     * 
     * @param leftSide The left side of the operator.
     */
    public void setLeftSide(CppExpression leftSide) {
        this.leftSide = leftSide;
    }
    
    /**
     * Returns the right side of this operator. This is <code>null</code> for unary operators.
     * 
     * @return The right side of this operator.
     */
    public CppExpression getRightSide() {
        return rightSide;
    }

    /**
     * Sets the right side of this operator. This should be <code>null</code> for unary operators.
     * 
     * @param rightSide the right side of this operator
     */
    public void setRightSide(CppExpression rightSide) {
        this.rightSide = rightSide;
    }
    
    @Override
    public <T> T accept(ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitOperator(this);
    }

    @Override
    protected String toString(String indentation) {
        StringBuilder result = new StringBuilder(indentation).append("Operator ").append(operator.getSymbol());
        
        indentation += '\t';
        if (leftSide != null) {
            result.append('\n').append(leftSide.toString(indentation));
        }
        if (rightSide != null) {
            result.append('\n').append(rightSide.toString(indentation));
        }
        
        return result.toString();
    }
    
}
