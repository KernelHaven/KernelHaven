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

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A number value of JSON.
 * 
 * @author Adam
 */
public class JsonNumber extends JsonValue<Number> {

    private @NonNull Number value;
    
    /**
     * Creates a number.
     * 
     * @param value The value of this number.
     */
    public JsonNumber(@NonNull Number value) {
        this.value = value;
    }
    
    @Override
    public @NonNull Number getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        return notNull(value.toString());
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitNumber(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonNumber) {
            JsonNumber o = (JsonNumber) other;
            equal = this.value.equals(o.value);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
}
