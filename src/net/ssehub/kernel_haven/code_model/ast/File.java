package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
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
    protected File(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.path = new java.io.File(json.getString("filePath"));
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
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + path.hashCode();
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof File && super.equals(other, checker);
        
        if (equal) {
            File o = (File) other;
            
            equal = this.path.equals(o.path);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("filePath", new JsonString(notNull(path.getPath().replace(java.io.File.separatorChar, '/'))));
    }

}
