package de.uni_hildesheim.sse.kernel_haven;

/**
 * An exception that is thrown if the setup of the pipeline fails.
 * 
 * @author Adam
 * @author Manu
 */
public class SetUpException extends Exception {

    private static final long serialVersionUID = -7292429707209634352L;

    /**
     * Creates a new {@link SetUpException}.
     */
    public SetUpException() {
    }

    /**
     * Creates a new {@link SetUpException}.
     * 
     * @param message
     *            A message describing the failure.
     */
    public SetUpException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link SetUpException}.
     * 
     * @param cause
     *            The exception that caused this exception.
     */
    public SetUpException(Throwable cause) {
        super(cause);
    }

}
