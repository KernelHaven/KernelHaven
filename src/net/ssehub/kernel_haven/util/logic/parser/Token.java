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
 * A token of an expression to be parsed. One of the three child classes:
 * <ul>
 *      <li>{@link Bracket}</li>
 *      <li>{@link Operator}</li>
 *      <li>{@link Identifier}</li>
 * </ul>
 * 
 * @author Adam (from KernelMiner project)
 */
public abstract class Token {
    
    private int pos;
    
    /**
     * Creates a new token.
     * 
     * @param pos The position in the expression where this token starts.
     */
    public Token(int pos) {
        this.pos = pos;
    }
    
    /**
     * Returns the position in the expression where this token starts.
     * @return The position in the expression where this token starts.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Returns the length of this token.
     * 
     * @return The length of this token.
     */
    public abstract int getLength();

}
