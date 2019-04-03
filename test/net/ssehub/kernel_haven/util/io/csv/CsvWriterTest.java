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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.text.AbstractWriter;

import org.junit.Test;

import net.ssehub.kernel_haven.util.io.ITableRow;
import net.ssehub.kernel_haven.util.io.TableRowMetadataTest;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Tests the {@link CsvWriter} and {@link AbstractWriter} classes.
 *
 * @author Adam
 */
@SuppressWarnings("null")
public class CsvWriterTest {

    /**
     * Tests whether a simple structure with annotations can be written.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteAnnotationsSimple() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeObject(new TableRowMetadataTest.Simple(1, "one"));
            writer.writeObject(new TableRowMetadataTest.Simple(2, "two"));
            writer.writeObject(new TableRowMetadataTest.Simple(3, "three"));
            
        }
        
        assertThat(out.toString(), is("Integer;String\n1;one\n2;two\n3;three\n"));
    }
    
    /**
     * Tests whether different input types correctly cause an {@link IllegalArgumentException}.
     * 
     * @throws IOException unwanted.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentClasses() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeObject(new TableRowMetadataTest.Simple(1, "one"));
            writer.writeObject(new TableRowMetadataTest.NoAnnotations('b', "three"));
        }
    }
    
    /**
     * Tests whether a simple structure without annotations can be written.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteNoAnnotationsSimple() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeObject(new TableRowMetadataTest.NoAnnotations('a', "one"));
            writer.writeObject(new TableRowMetadataTest.NoAnnotations('b', "two"));
            writer.writeObject(new TableRowMetadataTest.NoAnnotations('c', "three"));
            
        }
        
        assertThat(out.toString(), is("a one\nb two\nc three\n"));
    }
    
    /**
     * A simple table row that implements {@link ITableRow}.
     */
    private static class SimpleInterfaceRow implements ITableRow {

        private @Nullable String @NonNull [] content;
        
        private @Nullable String @NonNull [] header;
        
        /**
         * Creates a {@link SimpleInterfaceRow}.
         * 
         * @param content The content.
         * @param header The header.
         */
        public SimpleInterfaceRow(@Nullable String @NonNull [] content, @Nullable String @NonNull [] header) {
            this.content = content;
            this.header = header;
        }

        @Override
        public @Nullable String @NonNull [] getHeader() {
            return header;
        }

        @Override
        public @Nullable String @NonNull [] getContent() {
            return content;
        }
        
    }
    
    /**
     * Tests whether a simple structure which implements the {@link ITableRow} interface can be written.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteInterface() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeObject(new SimpleInterfaceRow(new String[] {"a", "b", "c"},
                    new String[] {"Column 1", "Column 2", "Column 3"}));
            writer.writeObject(new SimpleInterfaceRow(new String[] {"d", "e", "f"}, new String[0]));
            
        }
        
        assertThat(out.toString(), is("Column 1;Column 2;Column 3\na;b;c\nd;e;f\n"));
    }
    
    /**
     * Tests whether fields are correctly escaped.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscape() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeRow("1", "on;e");
            writer.writeRow("2", "tw\no");
            writer.writeRow("3", "th;re\"e");
            writer.writeRow("4", "fo\"ur");
        }
        
        assertThat(out.toString(), is("1;\"on;e\"\n2;\"tw\no\"\n3;\"th;re\"\"e\"\n4;\"fo\"\"ur\"\n"));
    }
    
    /**
     * Tests the field based {@link CsvWriter#writeRow(Object...)} method.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testFieldBasedWriting() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeRow("one", "two", "three");
            writer.writeRow("1", "2");
            writer.writeObject("empty");
            writer.writeRow();
            writer.writeObject("1;2");
            
        }
        
        assertThat(out.toString(), is("one;two;three\n1;2\nempty\n\n\"1;2\"\n"));
    }
    
    /**
     * Tests whether a different separator char is correctly used.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testDifferentSeparator() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out, ',')) {
            writer.writeRow("a", "b;c", "c");
            writer.writeRow("d", "e", "f");
        }
        
        assertThat(out.toString(), is("a,b;c,c\nd,e,f\n"));
    }
    
    /**
     * Tests that the {@link CsvWriter#writeRow(Object...)} handles non-string parameters correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteRowWithNonStrings() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeRow("a", 2, 'c');
            writer.writeRow(new TableRowMetadataTest.NoAnnotations('z', "abc"), null, true);
        }
        
        assertThat(out.toString(), is("a;2;c\nz abc;;true\n"));
        
    }
    
}
