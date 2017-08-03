package net.ssehub.kernel_haven;

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
    
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public SetUpException(String message, Throwable cause) {
        super(message, cause);
    }

}
