package net.ssehub.kernel_haven.util.io.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.util.io.ITableCollection;

/**
 * A collection of CSV files. The files share a common base name. The individual "table" names are suffixes added to
 * this base, followed by the file extension: <code>&lt;base&gt;_&lt;name&gt;.csv</code>
 * <br/>
 * Only table names that match [A-Za-z0-9_]+ are allowed.
 *
 * @author Adam
 */
public class CsvFileCollection implements ITableCollection {
    
    private static final Pattern ALLOWED_NAMES = Pattern.compile("[A-Za-z0-9_]+"); 
    
    private File baseName;

    /**
     * Creates a new collection of CSV file.
     * 
     * @param baseName The location and base-name for all the CSV files.
     */
    public CsvFileCollection(File baseName) {
        this.baseName = baseName;
    }

    @Override
    public CsvReader getReader(String name) throws IOException {
        return new CsvReader(new FileInputStream(getFile(name)));
    }
    
    /**
     * Creates the file for the given table name. The file does not have to exist.
     * 
     * @param tableName The name of the table to get the file for.
     * @return The file that the table is stored in (or would be stored in, if it existed).
     * 
     * @throws IOException If the table name is invalid.
     */
    public File getFile(String tableName) throws IOException {
        if (!ALLOWED_NAMES.matcher(tableName).matches()) {
            throw new IOException("Can only access tables with names that match " + ALLOWED_NAMES.pattern());
        }
        
        File parent = baseName.getParentFile();
        if (parent == null) {
            parent = baseName.getCanonicalFile().getParentFile();
            if (parent == null) {
                throw new IOException("Can't get parent directory for " + baseName.getPath());
            }
        }
        
        return new File(parent, baseName.getName() + "_" + tableName + ".csv");
    }

    @Override
    public Set<String> getTableNames() {
        Set<String> result = new HashSet<>();
        
        File dir = baseName.getParentFile();
        
        for (File file : dir.listFiles()) {
            String filename = file.getName();
            if (filename.startsWith(baseName.getName()) && filename.endsWith(".csv")) {
                String tableName = filename.substring(baseName.getName().length() + 1,
                        filename.length() - ".csv".length());
                
                if (ALLOWED_NAMES.matcher(tableName).matches()) {
                    result.add(tableName);
                }
            }
        }
        
        return result;
    }

    @Override
    public CsvWriter getWriter(String name) throws IOException {
        return new CsvWriter(new FileOutputStream(getFile(name)));
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public Set<File> getFiles() throws IOException {
        Set<File> files = new HashSet<>();
        
        for (String name : getTableNames()) {
            files.add(getFile(name));
        }
        
        return files;
    }

}
