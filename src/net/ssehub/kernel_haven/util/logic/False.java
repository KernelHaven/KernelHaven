package net.ssehub.kernel_haven.util.logic;

/**
 * The boolean constant "false".
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class False extends Formula {

    private static final long serialVersionUID = 6422261057525028423L;

    @Override
    public boolean evaluate() {
        return false;
    }

    @Override
    public String toString() {
        return "0";
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof False;
    }
    
    @Override
    public int hashCode() {
        return 2343242;
    }

    @Override
    public int getLiteralSize() {
        return 0;
    }
    
    @Override
    public void accept(IFormulaVisitor visitor) {
        visitor.visitFalse(this);
    }
}
