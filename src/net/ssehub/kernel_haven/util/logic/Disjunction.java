package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean disjunction operator (OR).
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-sharkawy
 */
public final class Disjunction extends Formula {

    private static final long serialVersionUID = 8416793994383200822L;

    private @NonNull Formula left;
    
    private @NonNull Formula right;
    
    /**
    * Creates a boolean disjunction (OR).
    * 
    * @param left The left operand.
    * @param right The right operand.
    */
    public Disjunction(@NonNull Formula left, @NonNull Formula right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * Returns the formula that is nested on the left side of this disjunction.
     * 
     * @return The left operand.
     */
    public @NonNull Formula getLeft() {
        return left;
    }
    
    /**
     * Returns the formula that is nested on the right side of this disjunction.
     * 
     * @return The right operand.
     */
    public @NonNull Formula getRight() {
        return right;
    }
    
    @Override
    public @NonNull String toString() {
        String leftStr = left.toString();
        if (!(left instanceof Disjunction) && left.getPrecedence() <= this.getPrecedence()) {
            leftStr = '(' + leftStr + ')';
        }
        
        String rightStr = right.toString();
        if (!(right instanceof Disjunction) && right.getPrecedence() <= this.getPrecedence()) {
            rightStr = '(' + rightStr + ')';
        }
        
        return leftStr + " || " + rightStr;
    }
    
    @Override
    public void toString(@NonNull StringBuilder result) {
        if (!(left instanceof Disjunction) && left.getPrecedence() <= this.getPrecedence()) {
            result.append('(');
            left.toString(result);
            result.append(')');
        } else {
            left.toString(result);
        }
        
        result.append(" || ");
                
        if (!(right instanceof Disjunction) && right.getPrecedence() <= this.getPrecedence()) {
            result.append('(');
            right.toString(result);
            result.append(')');
        } else {
            right.toString(result);
        }
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Disjunction) {
            Disjunction other = (Disjunction) obj;
            return left.equals(other.getLeft()) && right.equals(other.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (left.hashCode() + right.hashCode()) * 213;
    }
    
    @Override
    public int getLiteralSize() {
        return left.getLiteralSize() + right.getLiteralSize();
    }
    
    @Override
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitDisjunction(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitDisjunction(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 1;
    }
    
}
