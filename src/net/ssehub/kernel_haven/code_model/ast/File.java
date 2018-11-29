package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a complete source file. The nested children in this are the top-level elements in the file (e.g. function
 * definitions).
 *
 * @author Adam
 */
public class File extends AbstractSyntaxElementWithNesting {

    private java.io.@NonNull File path;
    
    /**
     * Creates a {@link File}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param path The location that this parsed file comes from.
     */
    public File(@NonNull Formula presenceCondition, java.io.@NonNull File path) {
        super(presenceCondition);
        this.path = path;
    }

    /**
     * Returns the location that this parsed file comes from.
     * 
     * @return The location of this file.
     */
    public java.io.@NonNull File getPath() {
        return path;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "File " + path + "\n";
    }
    

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFile(this);
    }

}
