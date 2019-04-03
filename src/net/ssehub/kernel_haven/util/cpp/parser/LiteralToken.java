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
 * A token representing a literal integer value.
 *
 * @author Adam
 */
final class LiteralToken extends CppToken {

    private @NonNull Number value;
    
    private int length;
    
    /**
     * Creates this literal token. 
     * 
     * @param pos The position inside the expression where this tokens starts.
     * @param length The length of the original text token.
     * @param value The value of this token.
     */
    public LiteralToken(int pos, int length, @NonNull Number value) {
        super(pos);
        this.length = length;
        this.value = value;
    }

    /**
     * Returns the value of this token.
     * 
     * @return The value of this token.
     */
    public @NonNull Number getValue() {
        return value;
    }
    
    @Override
    public int getLength() {
        return length;
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = super.equals(obj);
        if (equal && obj instanceof LiteralToken) {
            LiteralToken other = (LiteralToken) obj;
            equal = other.value.equals(this.value) && this.length == other.length;
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + value.hashCode() + Integer.hashCode(length);
    }

    @Override
    public @NonNull String toString() {
        return "Literal('" + String.valueOf(value) + "')";
    }

}
