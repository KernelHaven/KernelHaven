package net.ssehub.kernel_haven.code_model.ast;

/**
 * <p>
 * Represents unparsed string of code inside the AST. See class comment of {@link ISyntaxElement}.
 * </p>
 * <p>
 * This can contain variability (e.g. ifdef blocks), so instances of this interface can be any of:
 * <ul>
 *      <li>{@link Code} (representing the unparsed code string)</li>
 *      <li>{@link CodeList} (a list containing {@link Code}s and/or {@link CppBlock}s)</li>
 *      <li>{@link CppBlock} (only containing {@link Code}s and/or {@link CppBlock}s)</li>
 * </ul>
 * </p>
 * 
 * @author Adam
 */
public interface ICode extends ISyntaxElement {

}
