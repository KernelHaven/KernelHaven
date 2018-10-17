package net.ssehub.kernel_haven.variability_model;

import java.io.File;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Storage class for the source location. Stores sourcefile as well as
 * linennumber. This is used for representing the location of fragments of code.
 * 
 * @author marvin
 * @author moritz
 *
 *
 */
public class SourceLocation {
    
    private @NonNull File source;
    
    private int lineNumber;

    /**
     * Constructor for source location.
     * 
     * @param source
     *            the sourcefile containing the codefragment. This is relative to the source tree.
     * @param lineNumber
     *            the line in the sourcefile
     */
    public SourceLocation(@NonNull File source, int lineNumber) {
        this.source = source;
        this.lineNumber = lineNumber;
    }

    /**
     * Gets a File-object representing the sourcefile.
     * 
     * @return source the file source where the code is found. Relative to the source tree.
     */

    public @NonNull File getSource() {
        return source;
    }

    /**
     * Gets the line number of the codefragment.
     * 
     * 
     * @return lineNumber the number where the code is found.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int hashCode() {
        return source.hashCode() + new Integer(lineNumber).hashCode();
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean success = false;

        if (obj instanceof SourceLocation) {
            SourceLocation other = (SourceLocation) obj;
            success = other.lineNumber == this.lineNumber && other.source.equals(this.source);
        }

        return success;
    }

    @Override
    public @NonNull String toString() {
        return source.getPath() + ":" + lineNumber;
    }

}
