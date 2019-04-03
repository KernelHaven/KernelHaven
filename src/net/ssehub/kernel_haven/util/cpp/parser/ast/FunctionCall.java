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

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A function call.
 *
 * @author Adam
 */
public class FunctionCall extends CppExpression {

    private @NonNull String functionName;
    
    private @Nullable CppExpression argument;
    
    /**
     * Creates a new {@link FunctionCall}.
     * 
     * @param functionName The name of the called function.
     * @param argument The expression inside the brackets of the call.
     */
    public FunctionCall(@NonNull String functionName, @Nullable CppExpression argument) {
        this.functionName = functionName;
        this.argument = argument;
    }
    
    /**
     * Returns the name of the called function.
     * 
     * @return The name of the called function.
     */
    public @NonNull String getFunctionName() {
        return functionName;
    }
    
    /**
     * Returns the expression inside the brackets of the function call.
     * 
     * @return The argument of the function. May be <code>null</code>.
     */
    public @Nullable CppExpression getArgument() {
        return argument;
    }
    
    /**
     * Overrides the argument (parameter) for the function.
     * 
     * @param argument The new argument.
     */
    public void setArgument(@Nullable CppExpression argument) {
        this.argument = argument;
    }
    
    @Override
    public <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitFunctionCall(this);
    }

    @Override
    protected @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder(indentation).append("Function ").append(functionName);
        
        indentation += '\t';
        CppExpression argument = this.argument;
        if (argument != null) {
            result.append('\n').append(argument.toString(indentation));
        }
    
        return notNull(result.toString());
    }
    
}
