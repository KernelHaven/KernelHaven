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
    
    private boolean value;
    
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
    
    /**
     * Changes the value of this variable. Used for the evaluate method.
     * 
     * @param value The value of this variable. Will be returned by {@link #evaluate()}.
     * 
     * @deprecated Use {@link FormulaEvaluator} instead.
     */
    @Deprecated
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean evaluate() {
        return value;
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
    public int getLiteralSize() {
        return 1;
    }
    
    @Override
    protected <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }
    
    @Override
    protected void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitVariable(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 3;
    }
    
    @Override
    public void toString(@NonNull StringBuffer result) {
        result.append(toString());
    }
}
