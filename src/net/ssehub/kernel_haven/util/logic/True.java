package net.ssehub.kernel_haven.util.logic;

/**
 * The boolean constant "true".
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class True extends Formula {

    /**
     * Shared instance for this class.
     * Currently not a pure singleton to avoid refactoring of complete architecture.
     */
    public static final True INSTANCE = new True();

    private static final long serialVersionUID = 2252789480365343658L;
    
    /**
     * Should not longer used outside of this class, use {@link #INSTANCE} instead.
     */
    @Deprecated
    public True() {}

    @Override
    public boolean evaluate() {
        return true;
    }

    @Override
    public String toString() {
        return "1";
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof True;
    }
    
    @Override
    public int hashCode() {
        return 123213;
    }

    @Override
    public int getLiteralSize() {
        return 0;
    }
    
    @Override
    public void accept(IFormulaVisitor visitor) {
        visitor.visitTrue(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 3;
    }
    
}
