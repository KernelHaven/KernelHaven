/*
 * Copyright 2019 University of Hildesheim, Software Systems Engineering
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
package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A setting containing a list of values of a specified type.
 *
 * @param <T> The type of values in the list.
 *
 * @author Adam
 */
public class ListSetting<T> extends Setting<@NonNull List<T>> {

    private @NonNull Type nestedType;
    
    /**
     * Creates a {@link ListSetting}.
     * 
     * @param key The key in the properties file.
     * @param nestedType The {@link Type} of elements that are contained in this list. The generic should be set
     *      accordingly. Based on this type, checks are done at register time (and possibly exceptions thrown).
     * @param mandatory Whether this setting is mandatory. If this is <code>true</code> and the properties file does
     *      not contain this key, then an exception is thrown when the setting is registered. If this is
     *      <code>false</code> and the properties file does not contain this key, an empty list is created (rather than
     *      <code>null</code>.
     * @param description The description of this setting. Currently not used, but should be provided for documentation
     *      purposes.
     */
    public ListSetting(@NonNull String key, @NonNull Type nestedType, boolean mandatory, @NonNull String description) {
        super(key, Type.LIST, mandatory, null, description);
        
        this.nestedType = nestedType;
    }
    
    /**
     * Creates a {@link ListSetting}.
     * 
     * @param key The key in the properties file.
     * @param nestedType The {@link Type} of elements that are contained in this list. The generic should be set
     *      accordingly. Based on this type, checks are done at register time (and possibly exceptions thrown).
     * @param defaultValue The default value for this list. This is used if the key does not appear in the properties
     *      file. The <code>toString()</code> method of all elements will be used to store the default value.
     * @param description The description of this setting. Currently not used, but should be provided for documentation
     *      purposes.
     */
    public ListSetting(@NonNull String key, @NonNull Type nestedType, @NonNull List<T> defaultValue,
            @NonNull String description) {
        super(key, Type.LIST, true, defaultToString(defaultValue), description);
        
        this.nestedType = nestedType;
    }
    
    /**
     * Returns the type of elements that are contained in this list.
     * 
     * @return The type of the contained elements.
     */
    public @NonNull Type getNestedType() {
        return nestedType;
    }
    
    /**
     * Converts a list to a default setting string.
     * 
     * @param list The list to convert.
     * 
     * @return The list turned into a string that can be used as a default value.
     * 
     * @throws RuntimeException If any of the elements in the list contain a comma.
     */
    private static @Nullable String defaultToString(@NonNull List<?> list) {
        StringBuilder result = new StringBuilder();
        
        for (Object o : list) {
            if (o != null && o.toString().indexOf(',') != -1) {
                throw new RuntimeException("Default values for list setting may not contain commas");
            }
            
            result.append(o).append(", ");
        }
        
        if (result.length() > 0) {
            result.delete(result.length() - 2, result.length()); // remove trailing ", "
        }
        
        return notNull(result.toString());
    }
    
}
