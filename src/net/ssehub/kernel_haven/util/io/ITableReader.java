/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

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
    public @NonNull String @Nullable [] readNextRow() throws IOException;
    
    /**
     * Reads the complete (remaining) table data. 
     * 
     * @return The complete table data. First dimensions are rows, second dimensions are columns in the rows. Not
     *      all rows have the same number of columns.
     * 
     * @throws IOException If reading the file fails.
     */
    public default @NonNull String @NonNull [] @NonNull [] readFull() throws IOException {
        List<@NonNull String[]> rows = new LinkedList<>();
        String[] row;
        while ((row = readNextRow()) != null) {
            rows.add(row);
        }
        @SuppressWarnings("null")
        @NonNull String @NonNull [] @NonNull [] result = rows.toArray(new String[0][]);
        return result;
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
        public T create(@NonNull String @NonNull [] fields) throws FormatException;
        
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
    public default <T> T readAsObject(@NonNull Factory<T> factory) throws IOException, FormatException {
        T result = null;
        @NonNull String[] row = readNextRow();
        if (row != null) {
            result = factory.create(row);
        }
        return result;
    }
    
    /**
     * Returns the line number of the row that was read in the last read call, starting at one. This is not guaranteed
     * to always be increased by one (e.g. a CSV file may contain escaped line breaks). This line number should help
     * a user to find the relevant row, thus it is useful for error messages.
     * 
     * @return The line number of the last read column. 0, if no column was read yet.
     */
    public int getLineNumber();
    
}
