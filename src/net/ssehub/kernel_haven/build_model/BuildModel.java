package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents the build model. It represents file-level presence conditions.
 * 
 * @author Moritz
 * @author Adam
 */
public class BuildModel implements Iterable<@NonNull File> {

    private @NonNull Map<@NonNull File, Formula> fileFormulaMapping;

    /**
     * Instantiates a new and empty BuildModel.
     */
    public BuildModel() {
        fileFormulaMapping = new HashMap<>();
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
     * Retrieves the presence condition for a given file. Null if not present.
     * 
     * @param file
     *            given file. Must not be null.
     * @return the presence condition of the given file.
     */
    public @Nullable Formula getPc(@NonNull File file) {
        return fileFormulaMapping.get(file);
    }

    /**
     * Checks if the given file is contained.
     * 
     * @param file the given file. Must not be null.
     * @return whether it is contained.
     */
    public boolean contains(@NonNull File file) {
        return fileFormulaMapping.containsKey(file);
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
