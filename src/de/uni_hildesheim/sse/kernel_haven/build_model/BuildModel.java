package de.uni_hildesheim.sse.kernel_haven.build_model;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.uni_hildesheim.sse.kernel_haven.util.logic.Formula;

/**
 * Represents the build model. It represents file-level presence conditions.
 * 
 * @author Moritz
 * @author Adam
 */
public class BuildModel implements Iterable<File> {

    private Map<File, Formula> fileFormulaMapping;

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
    public void add(File file, Formula pc) {
        fileFormulaMapping.put(file, pc);
    }

    /**
     * Retrieves the presence condition for a given file. Null if not present.
     * 
     * @param file
     *            given file. Must not be null.
     * @return the presence condition of the given file.
     */
    public Formula getPc(File file) {
        return fileFormulaMapping.get(file);
    }

    /**
     * Checks if the given file is contained.
     * @param file the given file. Must not be null.
     * @return whether it is contained.
     */
    public boolean contains(File file) {
        return fileFormulaMapping.containsKey(file);
    }
    
    /**
     * Removes the given file.
     * @param file the given file. Must not be null.
     */
    public void delete(File file) {
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
    public Iterator<File> iterator() {
        return fileFormulaMapping.keySet().iterator();
    }

}
