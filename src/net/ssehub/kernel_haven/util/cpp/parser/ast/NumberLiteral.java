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
 * A number literal.
 *
 * @author Adam
 */
public class NumberLiteral extends CppExpression {

    private @NonNull Number value;
    
    /**
     * Creates a new literal number.
     * 
     * @param value The literal value.
     */
    public NumberLiteral(@NonNull Number value) {
        this.value = value;
    }
    
    /**
     * Returns the literal value. This is saved as a long or double, since CPP doesn't know data types and a normal
     * integer may overflow.
     * 
     * @return The literal value.
     */
    public @NonNull Number getValue() {
        return value;
    }

    @Override
    public <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitLiteral(this);
    }
    
    @Override
    protected @NonNull String toString(@NonNull String indentation) {
        return indentation + "Literal " + value;
    }
    
}
