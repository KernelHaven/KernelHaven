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
public class SourceFile implements Iterable<Block> {

    /**
     * This path is relative to the source tree.
     */
    private File path;

    /**
     * This are the toplevel blocks which are not nested in other blocks.
     */
    private List<Block> blocks;

    /**
     * Constructs a Sourcefile.
     * 
     * @param path
     *            The relative path to the source file in the source tree. Must
     *            not be <code>null</code>.
     */
    public SourceFile(File path) {
        this.path = path;
        blocks = new LinkedList<>();
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
     * Adds a block to the end of the list.
     * 
     * @param block
     *            The block to add. Must not be <code>null</code>.
     */
    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    /**
     * Iterates over the top blocks not nested in other blocks.
     * @return an iterator over top blocks.
     */
    @Override
    public Iterator<Block> iterator() {
        return blocks.iterator();
    }
    
    /**
     * Returns the number of top blocks (not nested in other blocks).
     *  
     * @return the number of blocks.
     */
    public int getTopBlockCount() {
        return blocks.size();
    }

}
