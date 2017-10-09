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
 * Tests the {@link CsvFileCollection} class.
 *
 * @author Adam
 */
public class CsvFileCollectionTest {
    
    private static final File TESTDATA = new File("testdata/csv");

    /**
     * Tests whether existing table names are found correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetTableNames() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(2));
            assertThat(tables, hasItems("table_1", "table_2"));
        }
            
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "other"))) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(1));
            assertThat(tables, hasItems("index"));
        }
    }
    
    /**
     * Tests whether get reader works on an existing file.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetReader() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            try (CsvReader reader = collection.getReader("table_1")) {
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
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            collection.getReader("doesnt_exist");
        }
    }
    
    /**
     * Tests whether the getFile() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    public void testGetFile() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            assertThat(collection.getFile("table_1").getName(), is("test_table_1.csv"));
            assertThat(collection.getFile("doesnt_exist").getName(), is("test_doesnt_exist.csv"));
        }
    }
    
    /**
     * Tests whether trying to get the file for a table with an invalid table name throws a correct exception.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testGetFileInvalidName() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            collection.getFile("in$valid");
        }
    }
    
    /**
     * Tests whether creating a new file with a writer works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetWriter() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "other"))) {
            Set<String> tables = collection.getTableNames();
            assertThat(tables.size(), is(1));
            assertThat(tables, hasItems("index"));
            
            try (CsvWriter writer = collection.getWriter("content")) {
                writer.writeRow("a", "b",  "c");
            }
            
            tables = collection.getTableNames();
            assertThat(tables.size(), is(2));
            assertThat(tables, hasItems("index", "content"));
            
            File file = collection.getFile("content");
            FileContentsAssertion.assertContents(file, "a;b;c\n");
            file.delete();
        }
    }
    
    /**
     * Tests whether the getFiles() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetFiles() throws IOException {
        try (CsvFileCollection collection = new CsvFileCollection(new File(TESTDATA, "test"))) {
            Set<File> files = collection.getFiles();
            assertThat(files.size(), is(2));
            assertThat(files, hasItems(
                    new File("testdata/csv/test_table_1.csv"), new File("testdata/csv/test_table_2.csv")));
        }
    }
    
}
