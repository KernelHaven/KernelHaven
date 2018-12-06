package net.ssehub.kernel_haven.code_model.ast;

import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
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
     * De-serializes the given JSON to a {@link CodeElement}. This is the inverse operation to
     * {@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param json The JSON do de-serialize.
     * @param deserializeFunction The function to use for de-serializing secondary nested elements. Do not use this to
     *      de-serialize the {@link CodeElement}s in the primary nesting structure!
     *      (i.e. {@link #getNestedElement(int)})
     * 
     * @throws FormatException If the JSON does not have the expected format.
     */
    protected Comment(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.comment = (ICode) deserializeFunction.apply(json.getObject("comment"));
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
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + hasher.hashCode((AbstractCodeElement<?>) comment);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof Comment && super.equals(other, checker);
        
        if (equal) {
            Comment o = (Comment) other;
            
            equal = checker.isEqual((AbstractCodeElement<?>) this.comment, (AbstractCodeElement<?>) o.comment);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("comment", serializeFunction.apply(comment));
    }

}
