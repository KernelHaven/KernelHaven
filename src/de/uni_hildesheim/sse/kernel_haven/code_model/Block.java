package de.uni_hildesheim.sse.kernel_haven.code_model;

import java.util.Iterator;
import java.util.List;

import de.uni_hildesheim.sse.kernel_haven.util.logic.Formula;

/**
 * Represents a Block inside a {@link SourceFile}.
 * 
 * @author Johannes
 * @author Adam
 */
public abstract class Block implements Iterable<Block> {

    /**
     * Iterates over the blocks nested inside this block.
     * 
     * @return an iterator over the nested blocks.
     */
    @Override
    public abstract Iterator<Block> iterator();

    /**
     * Returns the number of nested blocks (not recursively).
     * 
     * @return the number of blocks.
     */
    public abstract int getNestedBlockCount();

    /**
     * Returns the line where this block starts in the source file.
     * 
     * @return the start line number.
     */
    public abstract int getLineStart();

    /**
     * Returns the line where this block ends in the source file.
     * 
     * @return the end line number.
     */
    public abstract int getLineEnd();

    /**
     * Returns the immediate condition of this block. This is condition not
     * considering the parent of this block, etc.
     * 
     * @return the condition. May be <code>null</code> if this concept dosen't
     *         apply for the concrete subclass.
     */
    public abstract Formula getCondition();

    /**
     * Returns the presence condition of this block.
     * 
     * @return the presence condition. Must not be <code>null</code>.
     */
    public abstract Formula getPresenceCondition();
    
    /**
     * Serializes this block as a CSV line. This does not consider nested blocks.
     * Extending classes also need a createFromCsv(String[], Parser<Formula>) method that deserializes
     * this output.
     * 
     * @return The CSV parts representing this block.
     */
    public abstract List<String> serializeCsv();
    
    /**
     * Adds a child to the end of the list.
     * 
     * @param block
     *            The child to add.
     */
    public abstract void addChild(Block block);

}
