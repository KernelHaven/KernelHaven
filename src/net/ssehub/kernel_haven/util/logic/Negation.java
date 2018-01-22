package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * The boolean negation operator (NOT).
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Negation extends Formula {
    
    private static final long serialVersionUID = -7539218655156469390L;
    
    private @NonNull Formula formula;
    
    /**
     * Creates a boolean negation (NOT).
     * 
     * @param formula The operand of this negation.
     */
    public Negation(@NonNull Formula formula) {
        this.formula = formula;
    }
    
    /**
     * Returns the formula that is nested inside this negation.
     * 
     * @return The operand of this negation.
     */
    public @NonNull Formula getFormula() {
        return formula;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean evaluate() {
        return !formula.evaluate();
    }

    @Override
    public @NonNull String toString() {
        String innerStr = formula.toString();
        if (!(formula instanceof Negation) && formula.getPrecedence() <= this.getPrecedence()) {
            innerStr = '(' + innerStr + ')';
        }
        
        return "!" + innerStr;
    }
    
    @Override
    public void toString(@NonNull StringBuffer result) {
        result.append('!');
        if (!(formula instanceof Negation) && formula.getPrecedence() <= this.getPrecedence()) {
            result.append('(');
            formula.toString(result);
            result.append(')');
        } else {
            formula.toString(result);
        }
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
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
    protected <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitNegation(this);
    }
    
    @Override
    protected void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitNegation(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 2;
    }
    
}
