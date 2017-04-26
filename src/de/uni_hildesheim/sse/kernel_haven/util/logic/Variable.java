package de.uni_hildesheim.sse.kernel_haven.util.logic;

/**
 * A boolean variable.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Variable extends Formula {
    
    private static final long serialVersionUID = -5566369071417331297L;

    private String name;
    
    private boolean value;
    
    /**
     * Creates a boolean variable.
     * 
     * @param name The name of this variable.
     */
    public Variable(String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of this variable.
     * 
     * @return The name of this variable.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Changes the value of this variable. Used for the evaluate method.
     * 
     * @param value The value of this variable. Will be returned by {@link #evaluate()}.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean evaluate() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            Variable other = (Variable) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
