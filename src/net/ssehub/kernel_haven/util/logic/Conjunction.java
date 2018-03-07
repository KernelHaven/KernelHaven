package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean conjunction operator (AND).
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Conjunction extends Formula {

    private static final long serialVersionUID = 8595985320940207982L;

    private @NonNull Formula left;
    
    private @NonNull Formula right;
    
    /**
     * Creates a boolean conjunction (AND).
     * 
     * @param left The left operand.
     * @param right The right operand.
     */
    public Conjunction(@NonNull Formula left, @NonNull Formula right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * Returns the formula that is nested on the left side of this conjunction.
     * 
     * @return The left operand.
     */
    public @NonNull Formula getLeft() {
        return left;
    }
    
    /**
     * Returns the formula that is nested on the right side of this conjunction.
     * 
     * @return The right operand.
     */
    public @NonNull Formula getRight() {
        return right;
    }
    
    @Override
    public @NonNull String toString() {
        String leftStr = left.toString();
        if (!(left instanceof Conjunction) && left.getPrecedence() <= this.getPrecedence()) {
            leftStr = '(' + leftStr + ')';
        }
        
        String rightStr = right.toString();
        if (!(right instanceof Conjunction) && right.getPrecedence() <= this.getPrecedence()) {
            rightStr = '(' + rightStr + ')';
        }
        
        return leftStr + " && " + rightStr;
    }
    
    @Override
    public void toString(@NonNull StringBuffer result) {
        if (!(left instanceof Conjunction) && left.getPrecedence() <= this.getPrecedence()) {
            result.append('(');
            left.toString(result);
            result.append(')');
        } else {
            left.toString(result);
        }
        
        result.append(" && ");
                
        if (!(right instanceof Conjunction) && right.getPrecedence() <= this.getPrecedence()) {
            result.append('(');
            right.toString(result);
            result.append(')');
        } else {
            right.toString(result);
        }
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Conjunction) {
            Conjunction other = (Conjunction) obj;
            return left.equals(other.getLeft()) && right.equals(other.getRight());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (left.hashCode() + right.hashCode()) * 4564;
    }

    @Override
    public int getLiteralSize() {
        return left.getLiteralSize() + right.getLiteralSize();
    }
    
    @Override
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitConjunction(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitConjunction(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 1;
    }
    
}
