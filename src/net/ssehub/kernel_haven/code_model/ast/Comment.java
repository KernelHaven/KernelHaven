package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a comment. This can also appear inside unparsed {@link Code}, thus it implements {@link ICode}.
 * 
 * @author El-Sharkawy
 */
public class Comment extends AbstractSyntaxElementNoNesting implements ICode {

    private @NonNull ICode comment;
    
    /**
     * Sole constructor.
     * 
     * @param presenceCondition The presents condition of this comment.
     * @param comment The text content of the comment
     */
    public Comment(@NonNull Formula presenceCondition, @NonNull ICode comment) {
        super(presenceCondition);
        this.comment = comment;
    }
    
    /**
     * Returns the text of the comment.
     * 
     * @return The text content of this comment.
     */
    public @NonNull ICode getComment() {
        return comment;
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Comment:\n" + comment.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitComment(this);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + comment.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof Comment && super.equals(obj)) {
            Comment other = (Comment) obj;
            equal = this.comment.equals(other.comment);
        }
        
        return equal;
    }

}
