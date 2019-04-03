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
 * A bracket token.
 *
 * @author Adam
 */
final class Bracket extends CppToken {

    private boolean closing;
    
    /**
     * Creates a bracket token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param closing Whether this is a closing (<code>false</code>) or opening (<code>true</code>) bracket.
     */
    public Bracket(int pos, boolean closing) {
        super(pos);
        this.closing = closing;
    }
    
    /**
     * Returns whether this is a closing bracket.
     * 
     * @return Whether this is a closing bracket.
     */
    public boolean isClosing() {
        return closing;
    }
    
    /**
     * Returns whether this is a opening bracket.
     * 
     * @return Whether this is a opening bracket.
     */
    public boolean isOpening() {
        return !closing;
    }
    
    @Override
    public int getLength() {
        return 1;
    }
    
    @Override
    public @NonNull String toString() {
        return "Bracket('" + (closing ? ')' : '(') + "')";
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = super.equals(obj);
        if (equal && obj instanceof Bracket) {
            equal = ((Bracket) obj).closing == this.closing;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + Boolean.hashCode(this.closing);
    }
    
}
