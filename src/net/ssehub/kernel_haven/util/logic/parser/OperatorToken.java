package net.ssehub.kernel_haven.util.logic.parser;

/**
 * A token containing an {@link Operator}. Basically just an {@link Operator} with a position.
 * 
 * @author Adam
 */
public final class OperatorToken extends Token {

    private Operator operator;
    
    /**
     * Creates an operator token.
     * 
     * @param pos The position in the expression where this token starts.
     * @param operator The operator that this token represents.
     */
    public OperatorToken(int pos, Operator operator) {
        super(pos);
        this.operator = operator;
    }

    /**
     * Returns the operator of this token.
     * @return The operator of this token.
     */
    public Operator getOperator() {
        return operator;
    }
    
    @Override
    public int getLength() {
        return operator.getSymbol().length();
    }
    
    @Override
    public String toString() {
        return "[Operator: " + operator.getSymbol() + "]";
    }
    
}
