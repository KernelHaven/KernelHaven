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
package net.ssehub.kernel_haven.util.logic;

import java.io.Serializable;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean formula.
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public abstract class Formula implements Serializable {
    
    private static final long serialVersionUID = -2811872324947850301L;

    /**
     * Returns the precedence of this boolean operation. Higher means that this operation is evaluated
     * before operations with lower precedence. This has no semantic meaning for execution, though, since we
     * are organized in a tree structure. This is only used for proper parenthesis placement in toString().
     * 
     * @return The precedence of this operation.
     */
    protected abstract int getPrecedence();
    
    /**
     * Converts the formula into a string representation.
     * 
     * @return A string representation of this formula, in a C-style like format.
     */
    @Override
    public abstract @NonNull String toString();
    
    /**
     * Converts the formula into a string representation.
     * 
     * @param result The result object to which the result in a C-style like format shall be appended to
     *     (must not be <tt>null</tt>) .
     */
    public abstract void toString(@NonNull StringBuilder result);
    
    /**
     * Checks whether two {@link Formula}s are equal. {@link Formula}s are equal,
     * if they contain the same operators in the same hierarchy with the same
     * variable names.
     */
    @Override
    public abstract boolean equals(@Nullable Object obj);
    
    
    @Override
    public abstract int hashCode();
    
    /**
     * Visiting method for visitors.
     * 
     * @param visitor A visitor, which shall visit <tt>this</tt> formula.
     * 
     * @param <T> The return type of the visitor
     * 
     * @return The return value for the visitor.
     */
    public abstract <T> T accept(@NonNull IFormulaVisitor<T> visitor);
    
    /**
     * Visiting method for void visitors.
     * 
     * @param visitor A visitor, which shall visit <tt>this</tt> formula.
     */
    public abstract void accept(@NonNull IVoidFormulaVisitor visitor);
    
}
