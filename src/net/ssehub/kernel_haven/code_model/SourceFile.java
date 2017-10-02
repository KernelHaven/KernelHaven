package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a single file from the source tree.
 * 
 * @author Johannes
 * @author Adam
 */
public class SourceFile implements Iterable<CodeElement> {

    /**
     * This path is relative to the source tree.
     */
    private File path;

    /**
     * This are the toplevel elements which are not nested in other elements.
     */
    private List<CodeElement> elements;

    /**
     * Constructs a Sourcefile.
     * 
     * @param path
     *            The relative path to the source file in the source tree. Must
     *            not be <code>null</code>.
     */
    public SourceFile(File path) {
        this.path = path;
        elements = new LinkedList<>();
    }

    /**
     * Retrieves the path of this file which is relative to the source tree.
     * 
     * @return The path.
     */
    public File getPath() {
        return path;
    }

    /**
     * Adds a element to the end of the list.
     * 
     * @param element
     *            The element to add. Must not be <code>null</code>.
     */
    public void addElement(CodeElement element) {
        this.elements.add(element);
    }

    /**
     * Iterates over the top elements not nested in other elements.
     * @return an iterator over top elements.
     */
    @Override
    public Iterator<CodeElement> iterator() {
        return elements.iterator();
    }
    
    /**
     * Returns the number of top elements (not nested in other elements).
     *  
     * @return the number of blocks.
     */
    public int getTopElementCount() {
        return elements.size();
    }

}
