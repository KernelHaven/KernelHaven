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
 * The <code>null</code> value of JSON.
 * 
 * @author Adam
 */
public class JsonNull extends JsonValue<JsonNull> {

    public static final @NonNull JsonNull INSTANCE = new JsonNull();
    
    /**
     * Singleton constructor.
     */
    private JsonNull() {
    }
    
    @Override
    public @NonNull JsonNull getValue() {
        return this;
    }
    
    @Override
    public @NonNull String toString() {
        return "null";
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitNull(this);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return 13;
    }

}
