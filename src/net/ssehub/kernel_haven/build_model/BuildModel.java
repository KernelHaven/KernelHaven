package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
