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
package net.ssehub.kernel_haven.util;

import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Exception for unexpected structure of file-content.
 * 
 * @author Adam
 * @author Moritz
 *
 */
public class FormatException extends Exception {

    
    private static final long serialVersionUID = 2081277470741239201L;

    /**
     * Creates a new FormatException.
     */
    public FormatException() {

    }
    
    /**
     * Creates a new FormatException.
     * 
     * @param message The message to display.
     */
    public FormatException(@Nullable String message) {
        super(message);
    }
    
    /**
     * Creates a new FormatException.
     * 
     * @param cause The exception that caused this exception.
     */
    public FormatException(@Nullable Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new FormatException.
     * 
     * @param message The message to display.
     * @param cause The exception that caused this exception.
     */
    public FormatException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
    
}
