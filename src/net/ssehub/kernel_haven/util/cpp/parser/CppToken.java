package net.ssehub.kernel_haven.util.cpp.parser;

/**
 * A token used by the {@link CppParser}.
 *
 * @author Adam
 */
abstract class CppToken {

    private int pos;
    
    /**
     * Creates a new token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     */
    public CppToken(int pos) {
        this.pos = pos;
    }
    
    /**
     * Returns the position inside the expression where this tokens starts.
     * 
     * @return The position inside the expression where this tokens starts.
     */
    public int getPos() {
        return pos;
    }
    
    /**
     * Returns the length of this token.
     * 
     * @return The length of this token.
     */
    public abstract int getLength();
    
    @Override
    public abstract String toString();
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof CppToken) {
            equal = this.pos == ((CppToken) obj).pos;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(pos);
    }
    
}
