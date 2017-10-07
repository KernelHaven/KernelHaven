package net.ssehub.kernel_haven.util.io.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import net.ssehub.kernel_haven.util.io.TableRowMetadataTest;

/**
 * Tests the {@link CsvWriter}.
 *
 * @author Adam
 */
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
            writer.writeRow(new TableRowMetadataTest.Simple(1, "one"));
            writer.writeRow(new TableRowMetadataTest.Simple(2, "two"));
            writer.writeRow(new TableRowMetadataTest.Simple(3, "three"));
            
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
            writer.writeRow(new TableRowMetadataTest.Simple(1, "one"));
            writer.writeRow(new TableRowMetadataTest.NoAnnotations('b', "three"));
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
            writer.writeRow(new TableRowMetadataTest.NoAnnotations('a', "one"));
            writer.writeRow(new TableRowMetadataTest.NoAnnotations('b', "two"));
            writer.writeRow(new TableRowMetadataTest.NoAnnotations('c', "three"));
            
        }
        
        assertThat(out.toString(), is("a one\nb two\nc three\n"));
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
            writer.writeRow(new TableRowMetadataTest.Simple(1, "on;e"));
            writer.writeRow(new TableRowMetadataTest.Simple(2, "tw\no"));
            writer.writeRow(new TableRowMetadataTest.Simple(3, "th;re\"e"));
            
        }
        
        assertThat(out.toString(), is("Integer;String\n1;\"on;e\"\n2;\"tw\no\"\n3;\"th;re\"\"e\"\n"));
    }
    
    /**
     * Tests the field based {@link CsvWriter#writeRow(String...)} method.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testFieldBasedWriting() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeRow("one", "two", "three");
            writer.writeRow("1", "2");
            writer.writeRow("empty");
            writer.writeRow();
            writer.writeRow("1;2");
            
        }
        
        assertThat(out.toString(), is("one;two;three\n1;2\nempty\n\n\"1;2\"\n"));
    }

}
