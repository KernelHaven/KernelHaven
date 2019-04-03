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
package net.ssehub.kernel_haven.util.logic.parser;

/**
 * A token containing an {@link Operator}. Basically just an {@link Operator} with a position.
 * 
 * @author Adam
 */
public final class OperatorToken extends Token {

    private Operator operator;
    
    /**
     * Creates an operator token.
     * 
     * @param pos The position in the expression where this token starts.
     * @param operator The operator that this token represents.
     */
    public OperatorToken(int pos, Operator operator) {
        super(pos);
        this.operator = operator;
    }

    /**
     * Returns the operator of this token.
     * @return The operator of this token.
     */
    public Operator getOperator() {
        return operator;
    }
    
    @Override
    public int getLength() {
        return operator.getSymbol().length();
    }
    
    @Override
    public String toString() {
        return "[Operator: " + operator.getSymbol() + "]";
    }
    
}
