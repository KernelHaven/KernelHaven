package net.ssehub.kernel_haven.util.io.csv;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import net.ssehub.kernel_haven.test_utils.FileContentsAssertion;

/**
 * Tests the {@link CsvFileSet} class.
 * 
 * @author Adam
 */
public class CsvFileSetTest {

    private static final File TESTDATA = new File("testdata/csv");
    
    private static final File FILE_1 = new File(TESTDATA, "test_table_1.csv");
    private static final File FILE_2 = new File(TESTDATA, "test_table_2.csv");
    private static final File NEW_FILE = new File(TESTDATA, "new_file.csv");

    /**
     * Tests whether existing table names are found correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetTableNames() throws IOException {
        try (CsvFileSet collection = new CsvFileSet(FILE_1)) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(1));
            assertThat(tables, hasItems(FILE_1.getAbsolutePath()));
        }
            
        try (CsvFileSet collection = new CsvFileSet(FILE_1, FILE_2)) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(2));
            assertThat(tables, hasItems(FILE_1.getAbsolutePath(), FILE_2.getAbsolutePath()));
        }
    }
    
    /**
     * Tests whether get reader works on an existing file.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetReader() throws IOException {
        try (CsvFileSet collection = new CsvFileSet(FILE_1)) {
            try (CsvReader reader = collection.getReader(FILE_1.getAbsolutePath())) {
                assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c"}));
                assertThat(reader.readNextRow(), is(new String[] {"d", "e", "f"}));
                assertThat(reader.readNextRow(), nullValue());
            }
        }
    }
    
    /**
     * Tests whether trying to get a reader on a non-existing table throws a correct exception.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testGetReaderNotExisting() throws IOException {
        try (CsvFileSet collection = new CsvFileSet(FILE_1)) {
            collection.getReader(FILE_2.getAbsolutePath());
        }
    }
    
    /**
     * Tests whether creating a new file with a writer works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetWriter() throws IOException {
        try (CsvFileSet collection = new CsvFileSet(FILE_1)) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(1));
            assertThat(collection.getFiles().size(), is(1));
            assertThat(tables, hasItems(FILE_1.getAbsolutePath()));
            
            try (CsvWriter writer = collection.getWriter(NEW_FILE.getAbsolutePath())) {
                writer.writeRow("a", "b",  "c");
            }
            
            tables = collection.getTableNames();
            assertThat(tables.size(), is(2));
            assertThat(collection.getFiles().size(), is(2));
            assertThat(tables, hasItems(FILE_1.getAbsolutePath(), NEW_FILE.getAbsolutePath()));
            assertThat(collection.getFiles(), hasItems(FILE_1, NEW_FILE.getCanonicalFile()));
            
            FileContentsAssertion.assertContents(NEW_FILE, "a;b;c\n");
            NEW_FILE.delete();
        }
    }
    
    /**
     * Tests whether the getFiles() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetFiles() throws IOException {
        try (CsvFileSet collection = new CsvFileSet(FILE_1)) {
            Set<File> files = collection.getFiles();
            assertThat(files.size(), is(1));
            assertThat(files, hasItems(FILE_1));
        }
        
        try (CsvFileSet collection = new CsvFileSet(FILE_1, FILE_2)) {
            Set<File> files = collection.getFiles();
            assertThat(files.size(), is(2));
            assertThat(files, hasItems(FILE_1, FILE_2));
        }
    }
    
}
