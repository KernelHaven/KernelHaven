package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean variable.
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Variable extends Formula {
    
    private static final long serialVersionUID = -5566369071417331297L;

    private @NonNull String name;
    
    /**
     * Creates a boolean variable.
     * 
     * @param name The name of this variable.
     */
    public Variable(@NonNull String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of this variable.
     * 
     * @return The name of this variable.
     */
    public @NonNull String getName() {
        return name;
    }
    
    @Override
    public @NonNull String toString() {
        return name;
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
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

    @Override
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitVariable(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 3;
    }
    
    @Override
    public void toString(@NonNull StringBuilder result) {
        result.append(toString());
    }
}
