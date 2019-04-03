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
package net.ssehub.kernel_haven.build_model;

import java.io.File;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Describes which information / features a {@link BuildModel} provides.
 * 
 * @author Adam
 */
public class BuildModelDescriptor {
    
    /**
     * Different types of keys that a {@link BuildModel} may store. The {@link BuildModel#getPc(File)} method
     * automatically adapts to the correct querying method.
     */
    public static enum KeyType {
        
        /**
         * The {@link BuildModel} contains specific files as keys. When using {@link BuildModel#getPc(File)}, the given
         * file has to match exactly with a stored key. This is the default.
         */
        FILE,
        
        /**
         * The {@link BuildModel} contains directory entries as keys. When using {@link BuildModel#getPc(File)}, the
         * given file does not have to match a stored key. Instead, the (parent) folders of the input file are checked
         * (recursively).
         */
        DIRECTORY,
        
        /**
         * Combines {@link #FILE} and {@link #DIRECTORY}: First searches for exact file match, then for a matching
         * parent directory.
         */
        FILE_AND_DIRECTORY;
        
    }
    
    private @NonNull KeyType keyType;
    
    private boolean caseSensitive;
    
    /**
     * Creates a descriptor for a standard {@link BuildModel} (all standard values).
     */
    public BuildModelDescriptor() {
        this.keyType = KeyType.FILE;
        this.caseSensitive = true;
    }
    
    /**
     * Returns the {@link KeyType} of this {@link BuildModel}.
     * 
     * @return The {@link KeyType} of this {@link BuildModel}.
     */
    public @NonNull KeyType getKeyType() {
        return keyType;
    }
    
    /**
     * Sets the {@link KeyType} for this {@link BuildModel}. This method should only be called by the extractor
     * that created the {@link BuildModel}.
     * 
     * @param keyType The new {@link KeyType} for this {@link BuildModel}.
     */
    public void setKeyType(@NonNull KeyType keyType) {
        this.keyType = keyType;
    }
    
    /**
     * Whether the matching of filenames in the {@link BuildModel} is case-sensitive or not. Default: <code>true</code>.
     * 
     * @return Whether to match case-sensitive.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
    
    /**
     * Sets whether the matching of filenames in the {@link BuildModel} should be case-sensitive or not.
     * Default: <code>true</code>. This method should only be called by the extractor that created the
     * {@link BuildModel}.
     * 
     * @param caseSensitive The new case-sensitive value.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

}
