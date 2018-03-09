package net.ssehub.kernel_haven.util.logic.parser;

/**
 * A token of an expression to be parsed. One of the three child classes:
 * <ul>
 *      <li>{@link Bracket}</li>
 *      <li>{@link Operator}</li>
 *      <li>{@link Identifier}</li>
 * </ul>
 * 
 * @author Adam (from KernelMiner project)
 */
public abstract class Token {
    
    private int pos;
    
    /**
     * Creates a new token.
     * 
     * @param pos The position in the expression where this token starts.
     */
    public Token(int pos) {
        this.pos = pos;
    }
    
    /**
     * Returns the position in the expression where this token starts.
     * @return The position in the expression where this token starts.
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

}
