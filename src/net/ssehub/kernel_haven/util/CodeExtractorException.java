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

import java.io.File;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A special kind of {@link ExtractorException} that is thrown by code extractors.
 * This also stores which of the files that are parsed caused the exception.
 * 
 * @author Adam
 * @author Alice
 */
public class CodeExtractorException extends ExtractorException {

    private static final long serialVersionUID = 2204081793641378563L;
    
    private @NonNull File cause;
    
    /**
     * Creates a new {@link CodeExtractorException}.
     * @param cause The file in which this exception occurred.
     * @param message The message to be displayed.
     */
    public CodeExtractorException(@NonNull File cause, @NonNull String message) {
        super(cause.getPath() + ": " + message);
        this.cause = cause;
    }
    
    /**
     * Creates a new {@link CodeExtractorException}.
     * @param cause The file in which this exception occurred.
     * @param nested The exception that caused this exception. 
     */
    public CodeExtractorException(@NonNull File cause, @Nullable Throwable nested) {
        super(cause.getPath(), nested);
        this.cause = cause;
    }
    
    /**
     * The file that caused this exception.
     * 
     * @return The file that caused this exception.
     */
    public @NonNull File getCausingFile() {
        return cause;
    }

}
