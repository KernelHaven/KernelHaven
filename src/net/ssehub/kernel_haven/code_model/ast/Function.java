package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a function definition. The nested children inside this element is the function body (usually a single
 * {@link CompoundStatement}).
 *
 * @author Adam
 */
public class Function extends AbstractSyntaxElementWithNesting {

    private @NonNull String name;
    
    private @NonNull ICode header;
    
    /**
     * Creates a {@link Function}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param name The name of this function.
     * @param header The header of this function as a code string.
     */
    public Function(@NonNull Formula presenceCondition, @NonNull String name, @NonNull ICode header) {
        super(presenceCondition);
        this.header = header;
        this.name = name;
    }
    
    /**
     * Returns the header of this function as a code string.
     * 
     * @return The header of this function as a code string.
     */
    public @NonNull ICode getHeader() {
        return header;
    }
    
    /**
     * Returns the name of this function.
     * 
     * @return The name of this function.
     */
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Function " + name + "\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFunction(this);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + name.hashCode() + header.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof Function && super.equals(obj)) {
            Function other = (Function) obj;
            equal = this.name.equals(other.name) && this.header.equals(other.header);
        }
        
        return equal;
    }

}
