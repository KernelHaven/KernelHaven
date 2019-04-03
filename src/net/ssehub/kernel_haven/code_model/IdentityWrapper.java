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
package net.ssehub.kernel_haven.code_model;

import java.util.HashMap;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A wrapper around objects so that they can be used as keys in a {@link HashMap} based on object identity.
 * 
 * @param <T> The type that is held by this wrapper.
 */
final class IdentityWrapper<T> {


    private @NonNull T data;
    
    /**
     * Creates a wrapper for the given object.
     * 
     * @param data The object.
     */
    public IdentityWrapper(@NonNull T data) {
        this.data = data;
    }
    
    /**
     * Returns the object held by this wrapper.
     * 
     * @return The held object.
     */
    public @NonNull T getData() {
        return data;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IdentityWrapper && ((IdentityWrapper<?>) obj).data == this.data;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(data);
    }
    
}
