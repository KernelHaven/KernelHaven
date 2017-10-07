package net.ssehub.kernel_haven.util.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * A writer for writing structured, table-like data.
 *
 * @author Adam
 */
public interface ITableWriter extends Closeable {
    
    /**
     * Writes a single row of data. The first call of this is used to determine the header of the table. This means,
     * that subsequent calls to this method must provide the same kind of data (i.e. they all must have the same class).
     * 
     * @param row The object representing the row data. This object should have the {@link TableRow} annotation to mark
     *      it as a table row compatible type. Otherwise, this method just uses its {@link #toString()} method to fill
     *      a single field of data per row. 
     * 
     * @throws IOException If writing to the file fails.
     * @throws IllegalArgumentException If different types of rows (i.e. different classes) are passed to the same
     *      writer.
     */
    public void writeRow(Object row) throws IOException, IllegalArgumentException;
    
    /**
     * Writes a single row of data. The given fields are written directly. This method should not be mixed with the
     * {@link #writeRow(Object)} method, since the other method creates an internal state for the structure of
     * data to extract from the objects.
     * 
     * @param fields The field values to write. Not null. May be empty.
     * 
     * @throws IOException If writing to the file fails.
     */
    public void writeRow(String... fields) throws IOException;

}
