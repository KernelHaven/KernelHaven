package net.ssehub.kernel_haven.code_model.ast;

/**
 * <p>
 * Represents un-parsed string of code inside the AST. See class comment of {@link ISyntaxElement}.
 * </p>
 * <p>
 * This can be any of:
 * <ul>
 *      <li>{@link Code}</li>
 *      <li>{@link CodeList}</li>
 *      <li>{@link CppBlock} (only containing {@link Code}s and/or {@link CppBlock}s)</li>
 * </ul>
 * </p>
 * 
 * @author Adam
 */
public interface ICode extends ISyntaxElement {

}
