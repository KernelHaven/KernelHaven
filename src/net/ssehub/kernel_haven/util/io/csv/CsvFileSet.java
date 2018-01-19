package net.ssehub.kernel_haven.util.io.csv;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A collection of pre-defined CSV files. This is an alternative to {@link CsvFileCollection} that allows setting an
 * arbitrary set of files instead of basing it on a common name scheme. The absolute path of the files is their sheet
 * name.
 *  
 * @author adam
 */
public class CsvFileSet implements ITableCollection {

    private @NonNull Map<String, File> files;
    
    /**
     * Creates a new set of CSV files.
     * 
     * @param files The files that are contained in this set.
     */
    public CsvFileSet(@NonNull Set<@NonNull File> files) {
        this.files = new HashMap<>();
        for (File file : files) {
            this.files.put(file.getAbsolutePath(), file);
        }
    }
    
    /**
     * Creates a new set of CSV files.
     * 
     * @param files The files that are contained in this set.
     */
    public CsvFileSet(@NonNull File @NonNull ... files) {
        this.files = new HashMap<>();
        for (File file : files) {
            this.files.put(file.getAbsolutePath(), file);
        }
    }
    
    @Override
    public @NonNull CsvReader getReader(@NonNull String name) throws IOException {
        File file = files.get(name);
        if (file == null) {
            throw new FileNotFoundException("File " + name + " is not contained in this set");
        }
        
        return new CsvReader(new FileInputStream(file));
    }
    
    @Override
    public @NonNull Set<@NonNull String> getTableNames() throws IOException {
        @SuppressWarnings("null")
        @NonNull Set<@NonNull String> result = notNull(files.keySet());
        return result;
    }
    
    @Override
    public @NonNull CsvWriter getWriter(@NonNull String name) throws IOException {
        File file = files.get(name);
        FileOutputStream out;
        if (file != null) {
            out = new FileOutputStream(file);
        } else {
            file = new File(name);
            out = new FileOutputStream(file);
            // do this after output stream is created, to ensure that we don't have filenames in files that are not
            //  valid.
            files.put(file.getAbsolutePath(), file);
        }
        
        return new CsvWriter(out);
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public @NonNull Set<@NonNull File> getFiles() throws IOException {
        Set<@NonNull File> files = new HashSet<>();
        
        for (File f : this.files.values()) {
            files.add(notNull(f));
        }
        
        return files;
    }
    
}
