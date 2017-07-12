package net.ssehub.kernel_haven.util.logic;

/**
 * The boolean negation operator (NOT).
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Negation extends Formula {
    
    private static final long serialVersionUID = -7539218655156469390L;
    
    private Formula formula;
    
    /**
     * Creates a boolean negation (NOT).
     * 
     * @param formula The operand of this negation.
     */
    public Negation(Formula formula) {
        this.formula = formula;
    }
    
    /**
     * Returns the formula that is nested inside this negation.
     * 
     * @return The operand of this negation.
     */
    public Formula getFormula() {
        return formula;
    }

    @Override
    public boolean evaluate() {
        return !formula.evaluate();
    }

    @Override
    public String toString() {
        String innerStr = formula.toString();
        if (!(formula instanceof Negation) && formula.getPrecedence() <= this.getPrecedence()) {
            innerStr = '(' + innerStr + ')';
        }
        
        return "!" + innerStr;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Negation) {
            Negation other = (Negation) obj;
            return formula.equals(other.formula);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return formula.hashCode() * 123;
    }

    @Override
    public int getLiteralSize() {
        return formula.getLiteralSize();
    }
    
    @Override
    public void accept(IFormulaVisitor visitor) {
        visitor.visitNegation(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 2;
    }
    
}
