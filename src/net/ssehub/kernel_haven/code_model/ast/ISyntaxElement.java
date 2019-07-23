/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;
import java.util.NoSuchElementException;

import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A single element of an abstract syntax tree (AST). Other {@link ISyntaxElement}s can be nested inside of this
 * element.
 * <p>
 * The ASTs created by this class are not fully parsed. Some leaf-nodes are instances of {@link Code}, which contain
 * unparsed code strings. If unparsed code elements contain variability (e.g. ifdef), then {@link CodeList} is used,
 * which contains {@link Code} and {@link CppBlock} children (the {@link CppBlock}s contain {@link Code} or more
 * {@link CppBlock}s).
 * 
 * @author Adam
 */
public interface ISyntaxElement extends CodeElement<ISyntaxElement> {

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
     * Returns whether any child element (fully recursive) or this element itself is an {@link ErrorElement}.
     * If not explicitly set by the extractor, this is <code>false</code>.
     * 
     * @return Whether this sub-tree contains an {@link ErrorElement}.
     */
    public boolean containsErrorElement();
    
    /**
     * Sets whether any child element (fully recursive) or this element itself is an {@link ErrorElement}.
     * This method should only be called by the extractor that creates the AST.
     *
     * @param containsErrorElement The new value for containsErrorElement.
     */
    public void setContainsErrorElement(boolean containsErrorElement);
    
    /**
     * Accept this visitor.
     * 
     * @param visitor The visitor to accept.
     */
    public abstract void accept(@NonNull ISyntaxElementVisitor visitor);
    
}
