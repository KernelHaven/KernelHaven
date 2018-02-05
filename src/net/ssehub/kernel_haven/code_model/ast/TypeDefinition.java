package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

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
    
    private SyntaxElement declaration;
    private TypeDefType type;
    
    public TypeDefinition(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement declaration,
        TypeDefType type) {
        
        super(presenceCondition, sourceFile);
        this.declaration = declaration;
        this.type = type;
    }
    
    public SyntaxElement getDeclaration() {
        return declaration;
    }

    @Override
    protected String elementToString() {
        return type.name() + "-Definition\n"
            + (declaration == null ? "\t\t\t\tnull" : declaration.toString("\t\t\t\t")); // TODO
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitTypeDefinition(this);
    }

}
