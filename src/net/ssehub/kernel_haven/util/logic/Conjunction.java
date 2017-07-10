package net.ssehub.kernel_haven.util.logic;


/**
 * A boolean conjunction operator (AND).
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Conjunction extends Formula {

    private static final long serialVersionUID = 8595985320940207982L;

    private Formula left;
    
    private Formula right;
    
    /**
     * Creates a boolean conjunction (AND).
     * 
     * @param left The left operand.
     * @param right The right operand.
     */
    public Conjunction(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * Returns the formula that is nested on the left side of this conjunction.
     * 
     * @return The left operand.
     */
    public Formula getLeft() {
        return left;
    }
    
    /**
     * Returns the formula that is nested on the right side of this conjunction.
     * 
     * @return The right operand.
     */
    public Formula getRight() {
        return right;
    }
    
    @Override
    public boolean evaluate() {
        return left.evaluate() && right.evaluate();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " && " + right.toString() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
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
    public void accept(IFormulaVisitor visitor) {
        visitor.visitConjunction(this);
    }
}
