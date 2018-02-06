package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a code element inside a {@link SourceFile}.
 * 
 * @author Johannes
 * @author Adam
 */
public interface CodeElement {

    /**
     * Iterates over the elements nested inside this element. Not recursively.
     * 
     * @return An iterable over the nested elements.
     */
    public default Iterable<@NonNull CodeElement> iterateNestedElements() {
        return new Iterable<@NonNull CodeElement>() {
            
            @Override
            public @NonNull Iterator<@NonNull CodeElement> iterator() {
                return new Iterator<@NonNull CodeElement>() {

                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < getNestedElementCount();
                    }

                    @Override
                    public CodeElement next() {
                        return getNestedElement(index++);
                    }
                };
            }
        };
    }

    /**
     * Returns the number of nested elements (not recursively).
     * 
     * @return the number of elements.
     */
    public abstract int getNestedElementCount();
    
    /**
     * Returns a single nested element inside this element.
     * 
     * @param index The index of the element to return.
     * 
     * @return The element at the position index.
     * 
     * @throws IndexOutOfBoundsException If index >= getNestedElementCount().
     */
    public abstract @NonNull CodeElement getNestedElement(int index) throws IndexOutOfBoundsException;

    /**
     * Adds a nested element to the end of the list.
     * 
     * @param element The element to add.
     * 
     * @throws IndexOutOfBoundsException If the concrete class conceptually does not allow any more children.
     */
    public abstract void addNestedElement(@NonNull CodeElement element) throws IndexOutOfBoundsException;
    
    /**
     * Returns the line where this element starts in the source file.
     * 
     * @return The start line number. -1 if not available.
     */
    public abstract int getLineStart();

    /**
     * Returns the line where this element ends in the source file.
     * 
     * @return The end line number. -1 if not available.
     */
    public abstract int getLineEnd();
    
    /**
     * Returns the source file that this element originates from.
     * 
     * @return The source file location relative to the source tree. <code>new File("&lt;unknown&gt;")</code> if not
     *      available.
     */
    public abstract @NonNull File getSourceFile();

    /**
     * Returns the immediate condition of this element. This condition is not
     * considering the parent of this element, etc.
     * 
     * @return the condition. May be <code>null</code> if this concept dosen't
     *         apply for the concrete subclass.
     */
    public abstract @Nullable Formula getCondition();

    /**
     * Returns the presence condition of this element.
     * 
     * @return the presence condition. Must not be <code>null</code>.
     */
    public abstract @NonNull Formula getPresenceCondition();
    
    /**
     * Serializes this element as a CSV line. This does not consider nested elements.
     * Extending classes also need a createFromCsv(String[], Parser<Formula>) method that deserializes
     * this output.
     * 
     * @return The CSV parts representing this element.
     */
    public abstract @NonNull List<@NonNull String> serializeCsv();
    
}
