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
package net.ssehub.kernel_haven.code_model.simple_ast;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * An element in the syntax tree that indicates an error.
 * 
 * @author Adam
 */
public class ErrorSyntaxElement implements ISyntaxElementType {

    private @NonNull String message;
    
    /**
     * Creates a new error syntax element.
     * 
     * @param message The message to be displayed.
     */
    public ErrorSyntaxElement(@NonNull String message) {
        this.message = message;
    }
    
    /**
     * Retrieves the message for this error.
     * 
     * @return This error message.
     */
    public @NonNull String getMessage() {
        return message;
    }
    
    @Override
    public @NonNull String toString() {
        return "Error: " + message;
    }
    
    @Override
    public int hashCode() {
        return message.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof ErrorSyntaxElement) {
            equal = this.message.equals(((ErrorSyntaxElement) obj).message);
        }
        
        return equal;
    }
    
}
