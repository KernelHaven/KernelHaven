package net.ssehub.kernel_haven.util.io.csv;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.util.ZipArchive;
import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * A collection of CSV files in a {@link ZipArchive}. The table name plus a ".csv" suffix is used as the filename
 * inside the archive.
 * </p>
 * <p>
 * Only table names that match [ A-Za-z0-9_'\\-\\+\\.]+ are allowed.
 * </p>
 * 
 * @author Adam
 */
public class CsvArchive implements ITableCollection {

    private static final Pattern ALLOWED_NAMES = Pattern.compile("[ A-Za-z0-9_'\\-\\+\\.]+"); 
    
    private @NonNull File archiveFile;
    
    private @NonNull ZipArchive archive;

    /**
     * Opens a {@link CsvArchive} with the given zip file. If no such archive exists (yet), an empty archive is
     * created. 
     * 
     * @param zipFile The location of the zip file. This usually ends with ".csv.zip".
     * @throws IOException If creating the archive fails.
     */
    public CsvArchive(@NonNull File zipFile) throws IOException {
        archive = new ZipArchive(zipFile);
        archiveFile = zipFile;
    }
    
    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public @NonNull ITableReader getReader(@NonNull String name) throws IOException {
        File inArchive = new File(tableNameToFileName(name));
        return new CsvReader(archive.getInputStream(inArchive));
    }

    @Override
    public @NonNull Set<@NonNull String> getTableNames() throws IOException {
        Set<@NonNull String> result = new HashSet<>();
        for (File f : archive.listFiles()) {
            result.add(notNull(f.getPath().replaceAll("\\.csv$", "")));
        }
        return result;
    }

    @Override
    public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException {
        File inArchive = new File(tableNameToFileName(name));
        return new CsvWriter(archive.getOutputStream(inArchive));
    }

    @Override
    public @NonNull Set<@NonNull File> getFiles() throws IOException {
        Set<@NonNull File> result = new HashSet<>();
        result.add(archiveFile);
        return result;
    }
    
    /**
     * Creates a filename for the location inside the archive, from a table name.
     * 
     * @param tableName The table name to get the filename for.
     * 
     * @return The filename for the given table name.
     * 
     * @throws IOException If the table name is not an allowed name.
     */
    private @NonNull String tableNameToFileName(@NonNull String tableName) throws IOException {
        if (!ALLOWED_NAMES.matcher(tableName).matches()) {
            throw new IOException("Can only access tables with names that match " + ALLOWED_NAMES.pattern()
                + ", but was: " + tableName);
        }
        
        return tableName + ".csv";
    }
    
}
