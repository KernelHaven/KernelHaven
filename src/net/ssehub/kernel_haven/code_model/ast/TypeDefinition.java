package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a declaration (and initialization) of a new data structure, outside of a function. More precisely one of:
 * <ul>
 *     <li><a href="http://www.srcml.org/doc/c_srcML.html#struct-definition">Struct definition/declaration</a></li>
 *     <li><a href="http://www.srcml.org/doc/c_srcML.html#enum-definition">Enum definition/declaration</a></li>
 * </ul>
 * 
 * TODO SE: is this list complete? should we have a reference to srcML here?
 * 
 * @author El-Sharkawy
 */
public class TypeDefinition extends AbstractSyntaxElementWithChildreen {

    /**
     * The type of typedef.
     */
    public static enum TypeDefType {
        STRUCT, ENUM, TYPEDEF, UNION;
    }
    
    private @NonNull ICode declaration;
    
    private @NonNull TypeDefType type;
    
    /**
     * Creates a {@link TypeDefinition}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param declaration The declaration of this element.
     * @param type The type of typedef that this is.
     */
    public TypeDefinition(@NonNull Formula presenceCondition, @NonNull ICode declaration,
            @NonNull TypeDefType type) {
        
        super(presenceCondition);
        this.declaration = declaration;
        this.type = type;
    }
    
    /**
     * Returns the declaration of this type.
     * 
     * @return The declaration of this type.
     */
    public @NonNull ICode getDeclaration() {
        return declaration;
    }
    
    /**
     * Returns the type of this typedef.
     * 
     * @return The type of this typedef.
     */
    public TypeDefType getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-Definition\n" + declaration.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitTypeDefinition(this);
    }

}
