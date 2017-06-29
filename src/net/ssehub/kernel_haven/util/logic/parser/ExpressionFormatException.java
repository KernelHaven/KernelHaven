package net.ssehub.kernel_haven.util.logic.parser;

/**
 * An exception to indicate that parsing an expression string failed.
 * 
 * @author Adam (from KernelMiner project)
 */
public class ExpressionFormatException extends Exception {

    private static final long serialVersionUID = 6254538502877777240L;

    /**
     * Creates a new exception indicating that parsing an expression failed.
     * 
     * @param message A message describing the failure.
     */
    public ExpressionFormatException(String message) {
        super(message);
    }
    
}
