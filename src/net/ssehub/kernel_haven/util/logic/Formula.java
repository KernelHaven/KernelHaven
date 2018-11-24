package net.ssehub.kernel_haven.util.logic;

import java.io.Serializable;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean formula.
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public abstract class Formula implements Serializable {
    
    private static final long serialVersionUID = -2811872324947850301L;

    /**
     * Returns the precedence of this boolean operation. Higher means that this operation is evaluated
     * before operations with lower precedence. This has no semantic meaning for execution, though, since we
     * are organized in a tree structure. This is only used for proper parenthesis placement in toString().
     * 
     * @return The precedence of this operation.
     */
    protected abstract int getPrecedence();
    
    /**
     * Converts the formula into a string representation.
     * 
     * @return A string representation of this formula, in a C-style like format.
     */
    @Override
    public abstract @NonNull String toString();
    
    /**
     * Converts the formula into a string representation.
     * 
     * @param result The result object to which the result in a C-style like format shall be appended to
     *     (must not be <tt>null</tt>) .
     */
    public abstract void toString(@NonNull StringBuilder result);
    
    /**
     * Checks whether two {@link Formula}s are equal. {@link Formula}s are equal,
     * if they contain the same operators in the same hierarchy with the same
     * variable names.
     */
    @Override
    public abstract boolean equals(@Nullable Object obj);
    
    
    @Override
    public abstract int hashCode();
    
    /**
     * Returns the number of literals (the number of involved variables).
     * If a variable is multiple times involved, it will also counted multiple times.
     * 
     * @return The number of used variables (&ge; 0).
     */
    public abstract int getLiteralSize(); // TODO implement this as a visitor instead.
    
    /**
     * Visiting method for visitors.
     * 
     * @param visitor A visitor, which shall visit <tt>this</tt> formula.
     * 
     * @param <T> The return type of the visitor
     * 
     * @return The return value for the visitor.
     */
    public abstract <T> T accept(@NonNull IFormulaVisitor<T> visitor);
    
    /**
     * Visiting method for void visitors.
     * 
     * @param visitor A visitor, which shall visit <tt>this</tt> formula.
     */
    public abstract void accept(@NonNull IVoidFormulaVisitor visitor);
    
}
