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
     * Don't allow any instances except the singleton.
     */
    private False() {}
    
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
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitFalse(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitFalse(this);
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
