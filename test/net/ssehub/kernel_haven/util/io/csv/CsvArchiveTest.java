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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link CsvArchive}.
 * 
 * @author Adam
 */
public class CsvArchiveTest {

    private static final @NonNull File TESTDATA = new File("testdata/csv");
    
    private static final @NonNull File EXISTING = new File(TESTDATA, "archive.csv.zip");
    
    private static final @NonNull File NEW = new File(TESTDATA, "new.csv.zip");
    
    /**
     * Tests the {@link CsvArchive#getTableNames()} method.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetTableNames() throws IOException {
        assertThat(EXISTING.exists(), is(true));
        
        try (CsvArchive archive = new CsvArchive(EXISTING)) {
            Set<@NonNull String> expected = new HashSet<>();
            expected.add("Table 1");
            expected.add("Another Table");
            assertThat(archive.getTableNames(), is(expected));
        }
    }
    
    /**
     * Tests that reading an existing file works.
     * 
     * @throws IOException unwanted.
     */
    @SuppressWarnings("null")
    @Test
    public void testGetReaderOnExisting() throws IOException {
        assertThat(EXISTING.exists(), is(true));
        
        try (CsvArchive archive = new CsvArchive(EXISTING)) {
            
            try (ITableReader reader = archive.getReader("Table 1")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Hello", "World"}
                }));
            }
            try (ITableReader reader = archive.getReader("Another Table")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Key", "Data"},
                    new String[] {"A", "1"},
                    new String[] {"B", "2"}
                }));
            }
        }
    }
    
    /**
     * Tests that writing (and subsquent reading) of a new file works.
     * 
     * @throws IOException unwanted.
     */
    @SuppressWarnings("null")
    @Test
    public void testWriteNewArchive() throws IOException {
        assertThat(NEW.exists(), is(false));
        
        try (CsvArchive archive = new CsvArchive(NEW)) {
            
            try (ITableWriter writer = archive.getWriter("A")) {
                writer.writeRow("Hello", "World");
            }
            
            try (ITableWriter writer = archive.getWriter("Table B")) {
                writer.writeRow("Key", "Data");
                writer.writeRow("A", "1");
                writer.writeRow("B", "2");
            }
            
            // check by reading in currently written file
            try (ITableReader reader = archive.getReader("A")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Hello", "World"}
                }));
            }
            try (ITableReader reader = archive.getReader("Table B")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Key", "Data"},
                    new String[] {"A", "1"},
                    new String[] {"B", "2"}
                }));
            }
            
        }
        
        // check by re-opening written file
        assertThat(NEW.exists(), is(true));
        try (CsvArchive archive = new CsvArchive(NEW)) {
            Set<@NonNull String> expected = new HashSet<>();
            expected.add("A");
            expected.add("Table B");
            assertThat(archive.getTableNames(), is(expected));
            
            try (ITableReader reader = archive.getReader("A")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Hello", "World"}
                }));
            }
            try (ITableReader reader = archive.getReader("Table B")) {
                assertThat(reader.readFull(), is(new String[][] {
                    new String[] {"Key", "Data"},
                    new String[] {"A", "1"},
                    new String[] {"B", "2"}
                }));
            }
        }
        
        NEW.delete();
    }
    
    /**
     * Tests whether trying to get a reader on a non-existing table throws a correct exception.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testGetReaderNotExisting() throws IOException {
        try (CsvArchive collection = new CsvArchive(EXISTING)) {
            collection.getReader("doesnt_exist");
        }
    }
    
    /**
     * Tests whether trying to get a reader on a table with an invalid name throws a correct exception.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testGetReaderInvalidName() throws IOException {
        try (CsvArchive collection = new CsvArchive(EXISTING)) {
            collection.getReader("in$valid");
        }
    }
    
    /**
     * Tests that getFiles() returns the archive file.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetFiles() throws IOException {
        try (CsvArchive collection = new CsvArchive(EXISTING)) {
            Set<@NonNull File> expected = new HashSet<>();
            expected.add(EXISTING);
            
            assertThat(collection.getFiles(), is(expected));
        }
    }
    
}
