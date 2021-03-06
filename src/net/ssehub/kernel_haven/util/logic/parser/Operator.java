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
 * An operator token of an expression to be parsed.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Operator {

    private String symbol;
    
    private boolean binary;
    
    private int precedence;
    
    /**
     * Creates a new operator token.
     * 
     * @param symbol The string how this operator is represented inside an expression string.
     * @param binary Whether this is a binary or unary operator.
     * @param precedence The precedence level of this operator.
     */
    public Operator(String symbol, boolean binary, int precedence) {
        this.symbol = symbol;
        this.binary = binary;
        this.precedence = precedence;
    }
    
    /**
     * The symbol.
     * 
     * @return The string how this operator is represented inside an expression string.
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * Whether this is an operator with two sides or just one.
     * 
     * @return Whether this is a binary or unary operator.
     */
    public boolean isBinary() {
        return binary;
    }
    
    /**
     * Retrieves the precedence level of this operator. Operators with higher
     * precedence levels are evaluated first, compared to operators with lower
     * precedence levels. Brackets can of course override this precedence order.
     * 
     * @return The precedence level of this operator.
     */
    public int getPrecedence() {
        return precedence;
    }
    
    @Override
    public String toString() {
        return "[Operator: " + symbol + "]";
    }
    
}
