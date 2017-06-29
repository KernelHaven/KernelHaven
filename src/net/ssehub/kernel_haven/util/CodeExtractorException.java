package net.ssehub.kernel_haven.util;

import java.io.File;

/**
 * A special kind of {@link ExtractorException} that is thrown by code extractors.
 * This also stores which of the files that are parsed caused the exception.
 * 
 * @author Adam
 * @author Alice
 */
public class CodeExtractorException extends ExtractorException {

    private static final long serialVersionUID = 2204081793641378563L;
    
    private File cause;
    
    /**
     * Creates a new {@link CodeExtractorException}.
     * @param cause The file in which this exception occurred.
     * @param message The message to be displayed.
     */
    public CodeExtractorException(File cause, String message) {
        super(cause.getPath() + ": " + message);
        this.cause = cause;
    }
    
    /**
     * Creates a new {@link CodeExtractorException}.
     * @param cause The file in which this exception occurred.
     * @param nested The exception that caused this exception. 
     */
    public CodeExtractorException(File cause, Throwable nested) {
        super(cause.getPath(), nested);
        this.cause = cause;
    }
    
    /**
     * The file that caused this exception.
     * 
     * @return The file that caused this exception.
     */
    public File getCausingFile() {
        return cause;
    }

}
