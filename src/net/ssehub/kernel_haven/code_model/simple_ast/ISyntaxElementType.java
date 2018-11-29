package net.ssehub.kernel_haven.code_model.simple_ast;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A type of an element in an AST.
 * 
 * @author adam
 */
public interface ISyntaxElementType {

    @Override
    public @NonNull String toString();
    
}
