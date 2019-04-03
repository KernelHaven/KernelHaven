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
 * An identifier token of an expression to be parsed.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Identifier extends Token {

    private String name;
    
    /**
     * Creates an identifier token.
     * 
     * @param pos The position in the expression where this token starts.
     * @param name The name of the identifier.
     */
    public Identifier(int pos, String name) {
        super(pos);
        this.name = name;
    }
    
    /**
     * The name of the identifier.
     * 
     * @return The name of the identifier.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the identifier.
     * 
     * @param name The new name of this identifier.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int getLength() {
        return name.length();
    }
    
    @Override
    public String toString() {
        return "[Identifier: " + name + "]";
    }
    
}
