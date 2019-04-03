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
package net.ssehub.kernel_haven.util.cpp.parser;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An operator token.
 *
 * @author Adam
 */
final class OperatorToken extends CppToken {

    private @NonNull CppOperator operator;
    
    /**
     * Creates a new operator token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param operator The operator that this token represents.
     */
    public OperatorToken(int pos, @NonNull CppOperator operator) {
        super(pos);
        this.operator = operator;
    }
    
    /**
     * Returns the operator that this token represents.
     * 
     * @return The operator of this token.
     */
    public @NonNull CppOperator getOperator() {
        return operator;
    }
    
    @Override
    public int getLength() {
        return operator.getSymbol().length();
    }
    
    @Override
    public @NonNull String toString() {
        return "Operator('" + operator.getSymbol() + "')";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = super.equals(obj);
        if (equal && obj instanceof OperatorToken) {
            equal = ((OperatorToken) obj).operator == this.operator;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + operator.hashCode();
    }
    
}
