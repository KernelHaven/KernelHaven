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
package net.ssehub.kernel_haven.util.io.json;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A boolean value.
 * 
 * @author Adam
 */
public class JsonBoolean extends JsonValue<Boolean> {

    public static final @NonNull JsonBoolean TRUE = new JsonBoolean(true);
    
    public static final @NonNull JsonBoolean FALSE = new JsonBoolean(false);
    
    private boolean value;

    /**
     * Singleton constructor.
     * 
     * @param value The boolean value.
     */
    private JsonBoolean(boolean value) {
        this.value = value;
    }
    
    /**
     * Returns a {@link JsonBoolean} with the given boolean value.
     * 
     * @param value The value to get the {@link JsonBoolean} for.
     * 
     * @return The correct {@link JsonBoolean} instance.
     */
    public static @NonNull JsonBoolean get(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public @NonNull Boolean getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        return value ? "true" : "false";
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

}
