package net.ssehub.kernel_haven.util.io.csv;

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

/**
 * A collection of pre-defined CSV files. This is an alternative to {@link CsvFileCollection} that allows setting an
 * arbitrary set of files instead of basing it on a common name scheme. The absolute path of the files is their sheet
 * name.
 *  
 * @author adam
 */
public class CsvFileSet implements ITableCollection {

    private Map<String, File> files;
    
    /**
     * Creates a new set of CSV files.
     * 
     * @param files The files that are contained in this set.
     */
    public CsvFileSet(Set<File> files) {
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
    public CsvFileSet(File... files) {
        this.files = new HashMap<>();
        for (File file : files) {
            this.files.put(file.getAbsolutePath(), file);
        }
    }
    
    @Override
    public CsvReader getReader(String name) throws IOException {
        File file = files.get(name);
        if (file == null) {
            throw new FileNotFoundException("File " + name + " is not contained in this set");
        }
        
        return new CsvReader(new FileInputStream(file));
    }
    
    @Override
    public Set<String> getTableNames() throws IOException {
        return files.keySet();
    }
    
    @Override
    public CsvWriter getWriter(String name) throws IOException {
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
    public Set<File> getFiles() throws IOException {
        Set<File> files = new HashSet<>();
        
        for (File f : this.files.values()) {
            files.add(f);
        }
        
        return files;
    }
    
}
