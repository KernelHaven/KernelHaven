package net.ssehub.kernel_haven.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;

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
    
    /**
     * A factory for creating objects from data fields.
     *  
     * @param <T> The type of object to create.
     */
    public static interface Factory<T> {
        
        /**
         * Creates an object from the given fields.
         *
         * @param fields The data fields to create the object from.
         * @return The object that was created from the given fields.
         * 
         * @throws FormatException If the fields are invalid for creating an object of this type.
         */
        public T create(String[] fields) throws FormatException;
        
    }
    
    /**
     * Reads the next row and converts it into an object.
     * 
     * @param factory A factory to convert the row into an object.
     * @return The read object. <code>null</code> if the end of stream has been reached.
     * 
     * @param <T> The type of object to create.
     * 
     * @throws IOException If reading the file fails.
     * @throws FormatException If the factory throws a format exception.
     */
    public default <T> T readAsObject(Factory<T> factory) throws IOException, FormatException {
        T result = null;
        String[] row = readNextRow();
        if (row != null) {
            result = factory.create(row);
        }
        return result;
    }
    
}