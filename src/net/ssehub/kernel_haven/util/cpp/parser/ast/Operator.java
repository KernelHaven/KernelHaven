package net.ssehub.kernel_haven.util.cpp.parser.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.maybeNull;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.util.cpp.parser.CppOperator;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An operator.
 *
 * @author Adam
 */
public class Operator extends CppExpression {

    private @NonNull CppOperator operator;
    
    private @NonNull CppExpression leftSide;
    
    private @Nullable CppExpression rightSide;
    
    /**
     * Creates a new operator. Make sure to set a valid non-null leftSide; befor that, this operator is not complete.
     * 
     * @param operator The operator.
     */
    @SuppressWarnings("null") // leftSide may be null during AST construction
    public Operator(@NonNull CppOperator operator) {
        this.operator = operator;
    }
    
    /**
     * Returns the operator that this node represents.
     * 
     * @return The operator that this node represents.
     */
    public @NonNull CppOperator getOperator() {
        return operator;
    }
    
    /**
     * Changes the opeartor.
     * 
     * @param operator The new operator.
     */
    public void setOperator(@NonNull CppOperator operator) {
        this.operator = operator;
    }
    
    /**
     * Returns the left side of this operator. This is the only "side" for unary operators.
     * 
     * @return The left side of this operator.
     */
    public @NonNull CppExpression getLeftSide() {
        return leftSide;
    }
    
    /**
     * Sets the left side of the operator. This is the only "side" for unary operators.
     * 
     * @param leftSide The left side of the operator.
     */
    public void setLeftSide(@NonNull CppExpression leftSide) {
        this.leftSide = leftSide;
    }
    
    /**
     * Returns the right side of this operator. This is <code>null</code> for unary operators.
     * 
     * @return The right side of this operator.
     */
    public @Nullable CppExpression getRightSide() {
        return rightSide;
    }

    /**
     * Sets the right side of this operator. This should be <code>null</code> for unary operators.
     * 
     * @param rightSide the right side of this operator
     */
    public void setRightSide(@Nullable CppExpression rightSide) {
        this.rightSide = rightSide;
    }
    
    @Override
    public <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitOperator(this);
    }

    @Override
    protected @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder(indentation).append("Operator ").append(operator.getSymbol());
        
        indentation += '\t';
        CppExpression leftSide = maybeNull(this.leftSide); // may be null during ASt construction
        if (leftSide != null) {
            result.append('\n').append(leftSide.toString(indentation));
        }
        CppExpression rightSide = this.rightSide;
        if (rightSide != null) {
            result.append('\n').append(rightSide.toString(indentation));
        }
        
        return notNull(result.toString());
    }
    
}
