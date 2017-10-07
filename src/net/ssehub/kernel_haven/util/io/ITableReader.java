package net.ssehub.kernel_haven.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A reader for reading structured, table-like data.
 *
 * @author Adam
 */
public interface ITableReader extends Closeable {

    /**
     * Reads the next row of data.
     * 
     * @return The next row of data. <code>null</code> if the end of stream has been reached.
     * 
     * @throws IOException If reading the file fails.
     */
    public String[] readNextRow() throws IOException;
    
    /**
     * Reads the complete (remaining) table data. 
     * 
     * @return The complete table data. First dimensions are rows, second dimensions are columns in the rows. Not
     *      all rows have the same number of columns.
     * 
     * @throws IOException If reading the file fails.
     */
    public default String[][] readFull() throws IOException {
        List<String[]> rows = new LinkedList<String[]>();
        String[] row;
        while ((row = readNextRow()) != null) {
            rows.add(row);
        }
        return rows.toArray(new String[0][]);
    }
    
}
