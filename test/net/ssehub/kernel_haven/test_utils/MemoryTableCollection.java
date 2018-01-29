package net.ssehub.kernel_haven.test_utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * An {@link ITableCollection} that creates {@link MemoryTableWriter}s. Useful to access the result of a test execution
 * in a programmatic way (via {@link MemoryTableWriter#getTable(String)}.
 * 
 * @author Adam
 */
public class MemoryTableCollection implements ITableCollection {

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public @NonNull ITableReader getReader(@NonNull String name) throws IOException {
        throw new IOException("The MemoryTableCollection can not read");
    }

    @Override
    public @NonNull Set<@NonNull String> getTableNames() throws IOException {
        return MemoryTableWriter.getTableNames();
    }

    @Override
    public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException {
        return new MemoryTableWriter(name);
    }

    @Override
    public @NonNull Set<@NonNull File> getFiles() throws IOException {
        return new HashSet<>();
    }

}
