package net.ssehub.kernel_haven.util.io;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import net.ssehub.kernel_haven.util.io.csv.CsvArchive;
import net.ssehub.kernel_haven.util.io.csv.CsvFileSet;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link TableCollectionReaderFactory}.
 * 
 * @author Adam
 *
 */
public class TableCollectionReaderFactoryTest {

    /**
     * A test handler that does nothing.
     */
    @SuppressWarnings("null")
    public static class TestHandler implements ITableCollection {

        /**
         * Does nothing.
         * 
         * @param file Ignored.
         */
        public TestHandler(File file) {
        }
        
        @Override
        public void close() throws IOException {
        }

        @Override
        public @NonNull ITableReader getReader(@NonNull String name) throws IOException {
            return null;
        }

        @Override
        public @NonNull Set<@NonNull String> getTableNames() throws IOException {
            return null;
        }

        @Override
        public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException {
            return null;
        }

        @Override
        public @NonNull Set<@NonNull File> getFiles() throws IOException {
            return null;
        }
        
    }
    
    /**
     * A test handler that has no proper constructor.
     */
    @SuppressWarnings("null")
    public static class InvalidHandler implements ITableCollection {

        @Override
        public void close() throws IOException {
        }

        @Override
        public @NonNull ITableReader getReader(@NonNull String name) throws IOException {
            return null;
        }

        @Override
        public @NonNull Set<@NonNull String> getTableNames() throws IOException {
            return null;
        }

        @Override
        public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException {
            return null;
        }

        @Override
        public @NonNull Set<@NonNull File> getFiles() throws IOException {
            return null;
        }
        
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory correctly creates CSV collections.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCsv() throws IOException {
        ITableCollection collection = TableCollectionReaderFactory.INSTANCE.openFile(new File("test.csv"));
        assertThat(collection, CoreMatchers.instanceOf(CsvFileSet.class));
        collection.close();
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory correctly creates CSV archives.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCsvArchive() throws IOException {
        ITableCollection collection = TableCollectionReaderFactory.INSTANCE.openFile(new File("test.csv.zip"));
        assertThat(collection, CoreMatchers.instanceOf(CsvArchive.class));
        collection.close();
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory correctly creates a newly registereted handler.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testNewHandler() throws IOException {
        TableCollectionReaderFactory.INSTANCE.registerHandler("something", TestHandler.class);
        ITableCollection collection = TableCollectionReaderFactory.INSTANCE.openFile(new File("test.something"));
        assertThat(collection, CoreMatchers.instanceOf(TestHandler.class));
        collection.close();
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory correctly throws an exception if an invalid file
     * suffix is passed to it.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidTsv() throws IOException {
        TableCollectionReaderFactory.INSTANCE.openFile(new File("test.tsv"));
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory correctly throws an exception if an invalid file
     * suffix is passed to it.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidTxt() throws IOException {
        TableCollectionReaderFactory.INSTANCE.openFile(new File("test.txt"));
    }
    
    /**
     * Tests a file name with no suffix.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testNoSuffix() throws IOException {
        TableCollectionReaderFactory.INSTANCE.openFile(new File("some_file_name"));
    }
    
    /**
     * Tests a file name with no suffix (only dot at end).
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testEmptySuffix() throws IOException {
        TableCollectionReaderFactory.INSTANCE.openFile(new File("some_file_name."));
    }
    
    /**
     * Tests whether the {@link TableCollectionReaderFactory} factory throws an exception if the handler does not have
     * a proper constructor.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidHandler() throws IOException {
        TableCollectionReaderFactory.INSTANCE.registerHandler("something", InvalidHandler.class);
        TableCollectionReaderFactory.INSTANCE.openFile(new File("test.something"));
    }
    
}
