package net.ssehub.kernel_haven.util.cpp.parser;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An identifier token.
 *
 * @author Adam
 */
class IdentifierToken extends CppToken {

    private @NonNull String name;
    
    /**
     * Creates a new identifier token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param name The name of the identifier.
     */
    public IdentifierToken(int pos, @NonNull String name) {
        super(pos);
        this.name = name;
    }
    
    /**
     * Returns the name of this identifier.
     * 
     * @return The name of this identifier.
     */
    public @NonNull String getName() {
        return name;
    }
    
    /**
     * Overrides the name of this identifier.
     * 
     * @param name The new name of this identifier.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }
    
    @Override
    public @NonNull String toString() {
        return "Identifier('" + name + "' pos=" + getPos() + ")";
    }

    @Override
    public int getLength() {
        return name.length();
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = super.equals(obj);
        if (equal && obj instanceof IdentifierToken) {
            equal = ((IdentifierToken) obj).name.equals(this.name);
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + name.hashCode();
    }

}
