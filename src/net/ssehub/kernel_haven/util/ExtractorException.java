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
 * Exception thrown if an extractor fails.
 * 
 * @author Adam
 * @author Moritz
 */
public class ExtractorException extends Exception {

    private static final long serialVersionUID = 8036527012134674472L;
    
    /**
     * Creates a new {@link ExtractorException}.
     */
    public ExtractorException() {
    }
    
    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param message The message to show.
     */
    public ExtractorException(@Nullable String message) {
        super(message);
    }
    
    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(@Nullable Throwable cause) {
        super(cause);
    }
    

    /**
     * Creates a new {@link ExtractorException}.
     * 
     * @param message The message to show.
     * @param cause The exception that caused the failure.
     */
    public ExtractorException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
