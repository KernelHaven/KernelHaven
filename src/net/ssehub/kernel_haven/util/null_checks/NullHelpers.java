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
package net.ssehub.kernel_haven.util.null_checks;

/**
 * Contains helper methods for {@link NonNull} and {@link Nullable} annotations. These are useful when static analysis
 * tools are used. 
 * 
 * @author Adam
 */
public class NullHelpers {

    /**
     * Don't allow any instances.
     */
    private NullHelpers() {
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param <T> The type of the value to convert.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * @param msg An error message for the assertion if the value is <code>null</code>.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static final <T> @NonNull T notNull(@Nullable T value, @Nullable String msg) throws AssertionError {
        if (value == null) {
            throw new AssertionError(msg);
        }
        return value;
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param <T> The type of the value to convert.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static final <T> @NonNull T notNull(@Nullable T value) throws AssertionError {
        if (value == null) {
            throw new AssertionError("Supplied value is null, but was expected not null");
        }
        return value;
    }
    
    /**
     * Declares an unmarked value as {@link Nullable}. This may be needed for some calls of methods from libraries.
     * 
     * @param <T> The type of the value to declare.
     * 
     * @param value The value that may be <code>null</code>.
     * @return The {@link Nullable} annotated value.
     */
    public static final <T> @Nullable T maybeNull(T value) {
        return value;
    }
    
}
