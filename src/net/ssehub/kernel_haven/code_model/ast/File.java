package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a complete source file. The nested children in this are the top-level elements in the file (e.g. function
 * definitions).
 *
 * @author Adam
 */
public class File extends AbstractSyntaxElementWithChildreen {

    /**
     * Creates a {@link File}.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public File(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "File\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFile(this);
    }

}
