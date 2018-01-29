package net.ssehub.kernel_haven.test_utils;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.io.TableRowMetadata;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>An {@link ITableWriter} that keeps all results in memory. The results can be accessed in a static way via the
 * name. passed to this writer in the constructor. Creating multiple writers with the same name will overwrite the
 * existing table.
 * </p>
 * <p>
 * All objects are stored as is, i.e. {@link ITableWriter#writeObject(Object)} is stored as a one-column row
 * ({@link TableRowMetadata} is not used).
 * </p>
 * 
 * @author Adam
 */
public class MemoryTableWriter implements ITableWriter {
    
    private static @NonNull Map<@NonNull String, List<@Nullable Object @NonNull []>> tables = new HashMap<>();
    
    private @NonNull String name;
    
    /**
     * Creates a {@link MemoryTableWriter} with the given name.
     * 
     * @param name The name to save the table as. Overwrites existing tables with the same name.
     */
    public MemoryTableWriter(@NonNull String name) {
        this.name = name;
        tables.put(name, new LinkedList<>());
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public void writeObject(@NonNull Object row) {
        tables.get(name).add(new @Nullable Object[] {row});
        
    }

    @Override
    public void writeRow(@Nullable Object... columns) {
        tables.get(name).add(notNull(columns)); // TODO: notNull() needed here, because @NonNull is commented out
    }
    
    /**
     * Retrieves the table with the given name.
     * 
     * @param name The name of the table.
     * @return The table, or <code>null</code> if a table with the given name does not exist.
     */
    public static @Nullable List<@Nullable Object  @NonNull []> getTable(@NonNull String name) {
        return tables.get(name);
    }
    
    /**
     * Returns a set containing all existing table names.
     * 
     * @return All existing table names.
     */
    public static @NonNull Set<@NonNull String> getTableNames() {
        return notNull(tables.keySet());
    }
    
    /**
     * Clears the table, should be called after each test execution.
     */
    public static void clear() {
        tables.clear();
    }

}
