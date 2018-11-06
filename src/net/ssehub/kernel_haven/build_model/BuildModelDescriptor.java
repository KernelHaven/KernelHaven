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
    
    /**
     * Creates a descriptor for a standard {@link BuildModel} (all standard values).
     */
    public BuildModelDescriptor() {
        this.keyType = KeyType.FILE;
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

}
