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
 * A JSON string.
 * 
 * @author Adam
 */
public class JsonString extends JsonValue<String> {

    private @NonNull String value;

    /**
     * Creates a JSON string with the given content.
     * 
     * @param value The string content.
     */
    public JsonString(@NonNull String value) {
        this.value = value;
    }

    @Override
    public @NonNull String getValue() {
        return value;
    }
    
    @Override
    public @NonNull String toString() {
        return '"' + jsonEscape(value) + '"';
    }

    @Override
    public <T> T accept(@NonNull JsonVisitor<T> visitor) {
        return visitor.visitString(this);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (other instanceof JsonString) {
            JsonString o = (JsonString) other;
            equal = this.value.equals(o.value);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    /**
     * Escapes the given string so that it is JSON compatible.
     * 
     * @param str The string to escape.
     * 
     * @return The properly escaped string.
     */
    public static @NonNull String jsonEscape(@NonNull String str) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            switch (c) {
            case '"':
                result.append("\\\"");
                break;
            case '\\':
                result.append("\\\\");
                break;
            case '\b':
                result.append("\\b");
                break;
            case '\n':
                result.append("\\n");
                break;
            case '\r':
                result.append("\\r");
                break;
            case '\t':
                result.append("\\t");
                break;
            case '\f':
                result.append("\\f");
                break;
                
            default:
                result.append(c);
                break;
            }
        }
        
        return notNull(result.toString());
    }
        
}
