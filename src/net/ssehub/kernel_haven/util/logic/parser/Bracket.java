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
 * A bracket token of an expression to be parsed.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Bracket extends Token {
    
    private boolean closing;
    
    /**
     * Creates a bracket token.
     * 
     * @param pos The position in the expression where this token starts.
     * @param closing Whether this is an opening or a closing bracket.
     */
    public Bracket(int pos, boolean closing) {
        super(pos);
        this.closing = closing;
    }
    
    /**
     * Checks whether this is a closing bracket or not.
     * 
     * @return Whether this is an opening or a closing bracket.
     */
    public boolean isClosing() {
        return closing;
    }
    
    @Override
    public int getLength() {
        return 1;
    }
    
    @Override
    public String toString() {
        return "[Bracket: " + (closing ? "closing" : "open") + "]";
    }
    
}
