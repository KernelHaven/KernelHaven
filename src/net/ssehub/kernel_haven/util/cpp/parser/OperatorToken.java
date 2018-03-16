package net.ssehub.kernel_haven.util.cpp.parser;

/**
 * An operator token.
 *
 * @author Adam
 */
class OperatorToken extends CppToken {

    private CppOperator operator;
    
    /**
     * Creates a new operator token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param operator The operator that this token represents.
     */
    public OperatorToken(int pos, CppOperator operator) {
        super(pos);
        this.operator = operator;
    }
    
    /**
     * Returns the operator that this token represents.
     * 
     * @return The operator of this token.
     */
    public CppOperator getOperator() {
        return operator;
    }
    
    @Override
    public int getLength() {
        return operator.getSymbol().length();
    }
    
    @Override
    public String toString() {
        return "Operator('" + operator.getSymbol() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = super.equals(obj);
        if (equal && obj instanceof OperatorToken) {
            equal = ((OperatorToken) obj).operator == this.operator;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + operator.hashCode();
    }
    
}
