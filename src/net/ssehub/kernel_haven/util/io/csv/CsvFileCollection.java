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
package net.ssehub.kernel_haven.util.io.csv;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A collection of CSV files. The files share a common base name. The individual "table" names are suffixes added to
 * this base, followed by the file extension: <code>&lt;base&gt;_&lt;name&gt;.csv</code>
 * <p>
 * Only table names that match [ A-Za-z0-9_'\\-\\+\\.\\(\\)]+ are allowed.
 *
 * @author Adam
 */
public class CsvFileCollection implements ITableCollection {
    
    private static final Pattern ALLOWED_NAMES = Pattern.compile("[ A-Za-z0-9_'\\-\\+\\.\\(\\)]+"); 
    
    private @NonNull File baseName;

    /**
     * Creates a new collection of CSV file.
     * 
     * @param baseName The location and base-name for all the CSV files.
     */
    public CsvFileCollection(@NonNull File baseName) {
        // strip the .csv file suffix
        if (baseName.getName().endsWith(".csv")) {
            baseName = new File(baseName.getParentFile(),
                    baseName.getName().substring(0, baseName.getName().length() - ".csv".length()));
        }
        this.baseName = baseName;
    }

    @Override
    public @NonNull CsvReader getReader(@NonNull String name) throws IOException {
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
    public @NonNull File getFile(@NonNull String tableName) throws IOException {
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
    public @NonNull Set<@NonNull String> getTableNames() {
        Set<@NonNull String> result = new HashSet<>();
        
        File dir = baseName.getParentFile();
        
        for (File file : dir.listFiles()) {
            String filename = file.getName();
            if (filename.startsWith(baseName.getName()) && filename.endsWith(".csv")) {
                String tableName = notNull(filename.substring(baseName.getName().length() + 1,
                        filename.length() - ".csv".length()));
                
                if (ALLOWED_NAMES.matcher(tableName).matches()) {
                    result.add(tableName);
                }
            }
        }
        
        return result;
    }

    @Override
    public @NonNull CsvWriter getWriter(@NonNull String name) throws IOException {
        return new CsvWriter(new FileOutputStream(getFile(name)));
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public @NonNull Set<@NonNull File> getFiles() throws IOException {
        Set<@NonNull File> files = new HashSet<>();
        
        for (String name : getTableNames()) {
            files.add(getFile(name));
        }
        
        return files;
    }

}
