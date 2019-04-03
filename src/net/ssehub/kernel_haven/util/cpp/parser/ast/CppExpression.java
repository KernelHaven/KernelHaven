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
package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Abstract superclass for CPP expression AST.
 *
 * @author Adam
 */
public abstract class CppExpression {

    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    /**
     * Turns this node into a string.
     * 
     * @param indentation The indentation to prepend at the start.
     * 
     * @return A string representation of this node with <code>indentation</code> at the start.
     */
    protected abstract @NonNull String toString(@NonNull String indentation);
    
    /**
     * Accepts the given visitor.
     * 
     * @param visitor The visitor to accept.
     * @param <T> The return type of the visitor.
     * 
     * @return The return value of the visitor.
     * 
     * @throws ExpressionFormatException If the visitor throws an {@link ExpressionFormatException}.
     */
    public abstract <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException;
    
}
