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
package net.ssehub.kernel_haven;

import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An exception that is thrown if the setup of the pipeline fails.
 * 
 * @author Adam
 * @author Manu
 */
public class SetUpException extends Exception {

    private static final long serialVersionUID = -7292429707209634352L;

    /**
     * Creates a new {@link SetUpException}.
     */
    public SetUpException() {
    }

    /**
     * Creates a new {@link SetUpException}.
     * 
     * @param message
     *            A message describing the failure.
     */
    public SetUpException(@Nullable String message) {
        super(message);
    }

    /**
     * Creates a new {@link SetUpException}.
     * 
     * @param cause
     *            The exception that caused this exception.
     */
    public SetUpException(@Nullable Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public SetUpException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
