package net.ssehub.kernel_haven.util.io;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import net.ssehub.kernel_haven.util.io.TableCollectionReaderFactoryTest.InvalidHandler;
import net.ssehub.kernel_haven.util.io.TableCollectionReaderFactoryTest.TestHandler;
import net.ssehub.kernel_haven.util.io.csv.CsvFileCollection;

/**
 * Tests the {@link TableCollectionWriterFactory}.
 * 
 * @author Adam
 *
 */
public class TableCollectionWriterFactoryTest {

    /**
     * Tests whether the {@link TableCollectionWriterFactory} factory correctly creates CSV collections.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCsv() throws IOException {
        ITableCollection collection = TableCollectionWriterFactory.INSTANCE.createCollection(new File("test.csv"));
        assertThat(collection, CoreMatchers.instanceOf(CsvFileCollection.class));
        collection.close();
    }
    
    /**
     * Tests whether the {@link TableCollectionWriterFactory} factory correctly creates a newly registereted handler.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testNewHandler() throws IOException {
        TableCollectionWriterFactory.INSTANCE.registerHandler("something", TestHandler.class);
        ITableCollection collection = TableCollectionWriterFactory.INSTANCE.createCollection(
                new File("test.something"));
        assertThat(collection, CoreMatchers.instanceOf(TestHandler.class));
        collection.close();
    }
    
    /**
     * Tests whether the {@link TableCollectionWriterFactory} factory correctly throws an exception if an invalid file
     * suffix is passed to it.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidTsv() throws IOException {
        TableCollectionWriterFactory.INSTANCE.createCollection(new File("test.tsv"));
    }
    
    /**
     * Tests whether the {@link TableCollectionWriterFactory} factory correctly throws an exception if an invalid file
     * suffix is passed to it.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidTxt() throws IOException {
        TableCollectionWriterFactory.INSTANCE.createCollection(new File("test.txt"));
    }
    
    /**
     * Tests a file name with no suffix.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testNoSuffix() throws IOException {
        TableCollectionWriterFactory.INSTANCE.createCollection(new File("some_file_name"));
    }
    
    /**
     * Tests a file name with no suffix (only dot at end).
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testEmptySuffix() throws IOException {
        TableCollectionWriterFactory.INSTANCE.createCollection(new File("some_file_name."));
    }
    
    /**
     * Tests whether the {@link TableCollectionWriterFactory} factory throws an exception if the handler does not have
     * a proper constructor.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidHandler() throws IOException {
        TableCollectionWriterFactory.INSTANCE.registerHandler("something", InvalidHandler.class);
        TableCollectionWriterFactory.INSTANCE.createCollection(new File("test.something"));
    }
    
}
