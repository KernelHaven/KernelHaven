package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

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
    public static final @NonNull True INSTANCE = new True();

    private static final long serialVersionUID = 2252789480365343658L;
    
    /**
     * Don't allow any instances except the singleton.
     */
    private True() {}

    @Override
    public @NonNull String toString() {
        return "1";
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
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
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitTrue(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitTrue(this);
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
