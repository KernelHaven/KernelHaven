package net.ssehub.kernel_haven.util.cpp.parser;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A token representing a literal integer value.
 *
 * @author Adam
 */
final class LiteralToken extends CppToken {

    private long value;
    
    private int length;
    
    /**
     * Creates this literal token. 
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param length The length of the original text token.
     * @param value The value of this token.
     */
    public LiteralToken(int pos, int length, long value) {
        super(pos);
        this.length = length;
        this.value = value;
    }

    /**
     * Returns the value of this token.
     * 
     * @return The value of this token.
     */
    public long getValue() {
        return value;
    }
    
    @Override
    public int getLength() {
        return length;
    }

    @Override
    public @NonNull String toString() {
        return "Literal('" + String.valueOf(value) + "')";
    }

}
