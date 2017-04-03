package de.uni_hildesheim.sse.kernel_haven.util.logic;

/**
 * The boolean constant "false".
 * 
 * @author Adam (from KernelMiner project)
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
        return super.hashCode();
    }

}
