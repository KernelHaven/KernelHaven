package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a comment.
 * 
 * @author El-Sharkawy
 */
public class Comment extends SyntaxElement {

    private @NonNull SyntaxElement comment;
    
    /**
     * Sole constructor.
     * 
     * @param presenceCondition The presents condition of this comment.
     * @param comment The text content of the comment
     */
    public Comment(@NonNull Formula presenceCondition, @NonNull SyntaxElement comment) {
        super(presenceCondition);
        this.comment = comment;
    }
    
    /**
     * Returns the text of the comment.
     * 
     * @return The text content of this comment.
     */
    public @NonNull SyntaxElement getComment() {
        return comment;
    }
    
    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return "Comment:\n" + comment.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitComment(this);
    }

}
