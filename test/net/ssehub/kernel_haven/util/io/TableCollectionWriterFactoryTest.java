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
package net.ssehub.kernel_haven.util.io;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import net.ssehub.kernel_haven.util.io.TableCollectionReaderFactoryTest.InvalidHandler;
import net.ssehub.kernel_haven.util.io.TableCollectionReaderFactoryTest.TestHandler;
import net.ssehub.kernel_haven.util.io.csv.CsvArchive;
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
     * Tests whether the {@link TableCollectionWriterFactory} factory correctly creates CSV archives.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCsvArchive() throws IOException {
        File zipFile = new File("test.csv.zip");
        ITableCollection collection = TableCollectionWriterFactory.INSTANCE.createCollection(zipFile);
        assertThat(collection, CoreMatchers.instanceOf(CsvArchive.class));
        collection.close();
        zipFile.delete();
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
