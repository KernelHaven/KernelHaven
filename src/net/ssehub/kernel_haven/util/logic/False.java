package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * The boolean constant "false".
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class False extends Formula {

    /**
     * Shared instance for this class.
     * Currently not a pure singleton to avoid refactoring of complete architecture.
     */
    public static final @NonNull False INSTANCE = new False();
    
    private static final long serialVersionUID = 6422261057525028423L;

    /**
     * Should not longer used outside of this class, use {@link #INSTANCE} instead.
     */
    @Deprecated
    public False() {}
    
    @Override
    public boolean evaluate() {
        return false;
    }

    @Override
    public @NonNull String toString() {
        return "0";
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
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
    protected <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitFalse(this);
    }
    
    @Override
    protected void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitFalse(this);
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
