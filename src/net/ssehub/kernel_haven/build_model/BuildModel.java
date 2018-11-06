package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

import net.ssehub.kernel_haven.build_model.BuildModelDescriptor.KeyType;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents the build model. It represents file-level presence conditions.
 * 
 * @author Adam
 * @author Moritz
 */
public class BuildModel implements Iterable<@NonNull File> {
    
    private @NonNull BuildModelDescriptor descriptor;
    
    private @NonNull Map<@NonNull File, Formula> fileFormulaMapping;
    
    /**
     * A custom {@link File} that does equality checks based on the case-sensitive setting in the descriptor.
     */
    private class InternalFile extends File {

        private static final long serialVersionUID = -2222438911242442999L;

        /**
         * Converts the given file to an {@link InternalFile}.
         * 
         * @param file The file that this object should represent.
         */
        public InternalFile(File file) {
            super(file.getPath());
        }
        
        @Override
        public File getParentFile() {
            File parent = super.getParentFile();
            if (parent != null) {
                parent = new InternalFile(parent);
            }
            return parent;
        }
        
        @Override
        public int hashCode() {
            String path = getPath();
            if (!(descriptor.isCaseSensitive())) {
                path = path.toLowerCase();
            }
            
            return path.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean result = false;
            if ((obj != null) && (obj instanceof File)) {
                result = filesEqual(this, (File) obj);
            }
            return result;
        }
        
    }
    
    /**
     * Instantiates a new and empty BuildModel. The key type is {@link KeyType#FILE}.
     */
    public BuildModel() {
        fileFormulaMapping = new HashMap<>();
        this.descriptor = new BuildModelDescriptor();
    }
    
    /**
     * Returns the {@link BuildModelDescriptor} for this build model.
     * 
     * @return A descriptor for this model.
     */
    public @NonNull BuildModelDescriptor getDescriptor() {
        return descriptor;
    }
    
    /**
     * Overrides the descriptor for this model.
     * 
     * @param descriptor The new descriptor.
     */
    void setDescriptor(@NonNull BuildModelDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Adds a new entry to the collection of files. Existing entries are
     * overwritten.
     * 
     * @param file
     *            relative to the source directory. Must not be null.
     * @param pc
     *            the presence condition. Must not be null.
     */
    public void add(@NonNull File file, @NonNull Formula pc) {
        file = new InternalFile(file);
        
        fileFormulaMapping.put(file, pc);
    }

    /**
     * Retrieves the presence condition for a given file. Automatically uses the correct query method according to
     * {@link BuildModelDescriptor#getKeyType()}.
     * 
     * @param file The file to get the presence condition for.
     * @return The presence condition of the given file, or <code>null</code> if the given file does not appear in this
     *      {@link BuildModel}.
     */
    public @Nullable Formula getPc(@NonNull File file) {
        file = new InternalFile(file);
        
        Formula result;
        if (descriptor.getKeyType() == KeyType.FILE) {
            result = fileFormulaMapping.get(file);
            
        } else if (descriptor.getKeyType() == KeyType.DIRECTORY) {
            result = null;
            File parent = file.getParentFile();
            
            while (parent != null && result == null) {
                result = fileFormulaMapping.get(parent);
                parent = parent.getParentFile();
            }
            
        } else { // FILE_AND_DIRECTORY
            File f = file;
            result = null;
            while (result == null && f != null) {
                result = fileFormulaMapping.get(f);
                f = f.getParentFile();
            }
            
        }
        return result;
    }
    
    /**
     * Returns the stored presence condition for the given key. As opposed to {@link #getPc(File)}, this does <b>not</b>
     * adapt to the {@link KeyType}; this method directly returns what is stored inside the internal map at the given
     * key.
     * 
     * @param key The key to query.
     * 
     * @return The stored PC for that key.
     */
    public @Nullable Formula getPcDirect(@NonNull File key) {
        key = new InternalFile(key);
        
        return fileFormulaMapping.get(key);
    }

    /**
     * Checks if the given file key is contained. Note that this checks if exactly this key is contained; it does not
     * consider {@link KeyType#DIRECTORY}.
     * 
     * @param file The file key to check.
     * 
     * @return Whether the key is contained.
     */
    public boolean containsKey(@NonNull File file) {
        file = new InternalFile(file);
        
        return fileFormulaMapping.containsKey(file);
    }
    
    /**
     * Checks if the given file is contained. Note that this adapts to the {@link KeyType} (unlike
     * {@link #containsKey(File)}).
     * 
     * @param file The file to check.
     * 
     * @return Whether a formula for the given file is contained.
     */
    public boolean containsFile(@NonNull File file) {
        return getPc(file) != null;
    }
    
    /**
     * Removes the given file.
     * @param file the given file. Must not be null.
     */
    public void delete(@NonNull File file) {
        file = new InternalFile(file);
        
        fileFormulaMapping.remove(file);
    }
    
    /**
     * Returns the number of files in the build model.
     * 
     * @return The number of files.
     */
    public int getSize() {
        return fileFormulaMapping.size();
    }
    
    /**
     * Checks if the two given files are equal. Considers {@link BuildModelDescriptor#isCaseSensitive()}.
     * 
     * @param file1 The first file.
     * @param file2 The second file.
     * 
     * @return Whether the two files are equal.
     */
    public boolean filesEqual(@NonNull File file1, @NonNull File file2) {
        File f1 = file1;
        File f2 = file2;
        
        BiFunction<String, String, Boolean> checker;
        if (descriptor.isCaseSensitive()) {
            checker = String::equals;
        } else {
            checker = String::equalsIgnoreCase;
        }
        
        // check if all path elements match
        boolean equal = true;
        while (equal && f1 != null && f2 != null) {
            if (!checker.apply(f1.getName(), f2.getName())) {
                equal = false;
            } else {
                f1 = f1.getParentFile();
                f2 = f2.getParentFile();
            }
        }
        
        if (equal && (f1 != null || f2 != null)) {
            // paths have not same length
            equal = false;
        }
        
        return equal;
    }
    
    @Override
    public @NonNull Iterator<@NonNull File> iterator() {
        return notNull(fileFormulaMapping.keySet().iterator());
    }
    
    @Override
    public @NonNull String toString() {
        // For debugging purpose only
        return notNull(fileFormulaMapping.toString());
    }

}
