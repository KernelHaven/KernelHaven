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
package net.ssehub.kernel_haven.config;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A single setting in the configuration. This holds the property key and other useful information. Instances of
 * this are registered in a {@link Configuration} object. After that, the values can accessed through this instance of
 * this class. It is recommended to create <code>public static final</code> instances of this class.
 * 
 * @param <T> The that the data should be represented as.
 *
 * @author Adam
 */
public class Setting<T> {
    
    /**
     * The type of setting. At register time, checks are done to verify that the value specified in the properties
     * is a valid instance of this type. This type also specifies the return type, and thus the generic of this instance
     * should be set accordingly.
     */
    public static enum Type {
        
        /**
         * A string value. Generic should be {@link String}.
         */
        STRING,
        
        /**
         * A integer value. Generic should be {@link Integer}.
         */
        INTEGER,
        
        /**
         * A {@link File} pointing to an existing directory. Generic should be {@link File}.
         */
        DIRECTORY,
        
        /**
         * A {@link File} pointing to an existing file. Generic should be {@link File}. The path may be absolute,
         * relative to current working directory, or relative to the source_tree setting (first match in this order is
         * used).
         */
        FILE,
        
        /**
         * A {@link File} without any restrictions. Generic should be {@link File}.
         */
        PATH,
        
        /**
         * A boolean value. Generic should be {@link Boolean}.
         */
        BOOLEAN,
        
        /**
         * A regular expression. Generic should be {@link Pattern}.
         */
        REGEX,
        
        /**
         * A comma separated list of strings. Generic should be {@link List} with generic String.
         */
        STRING_LIST,
        
        /**
         * A list of strings. Each value has its own key, based on the key of the setting and a suffix with a number:
         * {@code <key>.<number>} The number starts with 0 and increases by 1 for each successive
         * element in the list. Generic should be {@link List} with generic String.
         * <p>
         * The mandatory or default value attributes have no effect on this type of setting.
         */
        SETTING_LIST,
        
        /**
         * A value of a Java enum. <b>Do not create a {@link Setting} with this</b>, but rather use the
         * {@link EnumSetting} class.
         */
        ENUM,
        
    }
    
    private @NonNull String key;
    
    private @NonNull Type type;
    
    private boolean mandatory;
    
    private @Nullable String defaultValue;
    
    private @NonNull String description;

    /**
     * Creates a new setting.
     * 
     * @param key The key in the properties file.
     * @param type The type of setting. The generic should be set accordingly. Based on this type, checks are done at
     *      register time (and possibly exceptions thrown).
     * @param mandatory Whether this setting is mandatory. If this is <code>true</code>, the properties file does not
     *      contain this key, and the default value is <code>null</code>, then an exception is thrown when the setting
     *      is registered.
     * @param defaultValue The default value to use if the key is not specified in the properties. May be
     *      <code>null</code>, in which case no default value is used.
     * @param description The description of this setting. Currently not used, but should be provided for documentation
     *      purposes.
     */
    public Setting(@NonNull String key, @NonNull Type type, boolean mandatory, @Nullable String defaultValue,
            @NonNull String description) {
        
        this.key = key;
        this.type = type;
        this.mandatory = mandatory;
        this.defaultValue = defaultValue;
        this.description = description;
    }
    
    /**
     * The key of this setting.
     * 
     * @return The key that this setting is specified as in the properties.
     */
    public @NonNull String getKey() {
        return key;
    }
    
    /**
     * The type of setting. This influences the data type that values are represented in Java (and thus should be
     * reflected by the generic of this instance).
     * 
     * @return The type of setting.
     */
    public @NonNull Type getType() {
        return type;
    }
    
    /**
     * Whether this is a mandatory setting.
     * 
     * @return Whether this setting is mandatory or not.
     */
    public boolean isMandatory() {
        return mandatory;
    }
    
    /**
     * The default value to be used if the key is not specified in the properties.
     * 
     * @return The default value; <code>null</code> if no default value should be used.
     */
    public @Nullable String getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * The description of this setting. Can be used to explain the user what this setting is supposed to configure.
     * 
     * @return The description text. May contain line breaks.
     */
    public @NonNull String getDescription() {
        return description;
    }
    
    @Override
    public @NonNull String toString() {
        return "Setting(" + key + ")"; 
    }

}
