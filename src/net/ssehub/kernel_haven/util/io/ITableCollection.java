package net.ssehub.kernel_haven.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A collection of tables. Access to the different nested tables is available via {@link ITableReader}s and
 * {@link ITableWriter}s. Each table has a uniquely identifying name.
 *
 * @author Adam
 */
public interface ITableCollection extends Closeable {
    
    /**
     * Creates a reader for the table with the specified name.
     * 
     * @param name The name of the table to create a reader for.
     * @return A reader for the specified table.
     * 
     * @throws IOException If creating the reader fails.
     */
    public @NonNull ITableReader getReader(@NonNull String name) throws IOException;
    
    /**
     * Retrieves a set of all available table names.
     * 
     * @return A set containing all tables that are in this collection. Never <code>null</code>, but may be empty.
     * 
     * @throws IOException If creating the set fails.
     */
    public @NonNull Set<@NonNull String> getTableNames() throws IOException;
    
    /**
     * Creates a writer for the table with the specified name. If such a table already exists, it is deleted and
     * overwritten by this writer.
     * 
     * @param name The name of the table to create.
     * @return A writer for the table with the specified name.
     * 
     * @throws IOException If creating the writer fails.
     */
    public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException;
    
    /**
     * Retrieves the set of files that this collection is stored in. This is different from
     * {@link #getTableNames()}, since this method is concerned with the actual files in the file system that the data
     * is stored in, rather than the abstract notion of tables. 
     * 
     * @return The set of files that this collection is stored in. Never <code>null</code>, but may be empty.
     * 
     * @throws IOException If collecting the files fails for some reason.
     */
    public @NonNull Set<@NonNull File> getFiles() throws IOException;

}
