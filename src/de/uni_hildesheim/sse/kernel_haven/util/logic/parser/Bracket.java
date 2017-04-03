package de.uni_hildesheim.sse.kernel_haven.util.logic.parser;

/**
 * A bracket token of an expression to be parsed.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Bracket extends Token {
    
    private boolean closing;
    
    /**
     * Creates a bracket token.
     * 
     * @param closing Whether this is an opening or a closing bracket.
     */
    public Bracket(boolean closing) {
        this.closing = closing;
    }
    
    /**
     * Checks whether this is a closing bracket or not.
     * 
     * @return Whether this is an opening or a closing bracket.
     */
    public boolean isClosing() {
        return closing;
    }
    
    @Override
    public String toString() {
        return "[Bracket: " + (closing ? "closing" : "open") + "]";
    }
    
}
