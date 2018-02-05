package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a declaration (and initialization) of a new data structure, outside of a function. More precisely one of:
 * <ul>
 *     <li><a href="http://www.srcml.org/doc/c_srcML.html#struct-definition">Struct definition/declaration</a></li>
 *     <li><a href="http://www.srcml.org/doc/c_srcML.html#enum-definition">Enum definition/declaration</a></li>
 * </ul>
 * @author El-Sharkawy
 *
 */
public class TypeDefinition extends SyntaxElementWithChildreen {

    public static enum TypeDefType {
        STRUCT, ENUM, TYPEDEF, UNION;
    }
    
    private @NonNull SyntaxElement declaration;
    
    private @NonNull TypeDefType type;
    
    public TypeDefinition(@NonNull Formula presenceCondition, @NonNull SyntaxElement declaration,
            @NonNull TypeDefType type) {
        
        super(presenceCondition);
        this.declaration = declaration;
        this.type = type;
    }
    
    public @NonNull SyntaxElement getDeclaration() {
        return declaration;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        return type.name() + "-Definition\n" + declaration.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitTypeDefinition(this);
    }

}
