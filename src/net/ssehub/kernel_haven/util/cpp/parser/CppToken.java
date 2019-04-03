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
 * A token used by the {@link CppParser}.
 *
 * @author Adam
 */
abstract class CppToken {

    private int pos;
    
    /**
     * Creates a new token.
     * 
     * @param pos The position inside the expression where this tokens starts.
     */
    public CppToken(int pos) {
        this.pos = pos;
    }
    
    /**
     * Returns the position inside the expression where this tokens starts.
     * 
     * @return The position inside the expression where this tokens starts.
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
    
    @Override
    public abstract @NonNull String toString();
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = false;
        if (obj instanceof CppToken) {
            equal = this.pos == ((CppToken) obj).pos;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(pos);
    }
    
}
