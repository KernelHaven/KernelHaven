package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * A single element of an abstract syntax tree (AST). Other {@link ISyntaxElement}s can be nested inside of this
 * element.
 * </p>
 * <p>
 * The ASTs created by this class are not fully parsed. Some leaf-nodes are instances of {@link Code}, which contain
 * unparsed code strings. If unparsed code elements contain variability (e.g. ifdef), then {@link CodeList} is used,
 * which contains {@link Code} and {@link CppBlock} children (the {@link CppBlock}s contain {@link Code} or more
 * {@link CppBlock}s).
 * </p>
 * 
 * @author Adam
 */
public interface ISyntaxElement extends CodeElement {

    @Override
    public @NonNull ISyntaxElement getNestedElement(int index) throws IndexOutOfBoundsException;
    
    /**
     * Replaces the given nested element with the given new element. This method should only be called by the extractor
     * that creates the AST.
     * 
     * @param oldElement The old element to replace.
     * @param newElement The new element to replace with.
     * 
     * @throws NoSuchElementException If oldElement is not nested inside this one.
     */
    public void replaceNestedElement(@NonNull ISyntaxElement oldElement, @NonNull ISyntaxElement newElement)
            throws NoSuchElementException;
    
    /**
     * Sets the source file that this element comes from.
     * 
     * @param sourceFile The source element that this element comes from.
     * 
     * @see #getSourceFile()
     */
    public void setSourceFile(@NonNull File sourceFile);
    
    /**
     * Sets the immediate condition of this element. This method should only be called by the extractor
     * that creates the AST.
     * 
     * @param condition The immediate condition of this element.
     * 
     * @see #getCondition()
     */
    public void setCondition(@Nullable Formula condition);
    
    /**
     * Sets the presence condition of this element. This method should only be called by the extractor
     * that creates the AST.
     * 
     * @param presenceCondition The presence condition of this element.
     * 
     * @see #getPresenceCondition()
     */
    public void setPresenceCondition(@NonNull Formula presenceCondition);
    
    /**
     * Sets the starting line of this element.
     * 
     * @param lineStart The starting line.
     * 
     * @see #getLineStart()
     */
    public void setLineStart(int lineStart);
    
    /**
     * Sets the end line of this element.
     * 
     * @param lineEnd The end line.
     * 
     * @see #getLineEnd()
     */
    public void setLineEnd(int lineEnd);
    
    /**
     * Converts this single element into a string. This should not consider nested elements. However, other attributes
     * may be added in additional lines with the given indentation + "\t".
     * 
     * @param indentation The initial indentation to use for multiple lines.
     * 
     * @return A string representation of this single element.
     */
    public @NonNull String elementToString(@NonNull String indentation);
    
    /**
     * Converts this element and all its nested elements into a string.
     * 
     * @param indentation The initial indentation to use.
     * 
     * @return A string representation of the full hierarchy starting from this element.
     */
    public @NonNull String toString(@NonNull String indentation);

    /**
     * Accept this visitor.
     * 
     * @param visitor The visitor to accept.
     */
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
    
    /**
     * Iterates over the {@link ISyntaxElement}s nested inside this element. Not recursively.
     * 
     * @return An iterable over the nested elements.
     */
    public Iterable<@NonNull ISyntaxElement> iterateNestedSyntaxElements();
    
}
