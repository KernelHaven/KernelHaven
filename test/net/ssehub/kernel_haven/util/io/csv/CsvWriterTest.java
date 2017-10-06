package net.ssehub.kernel_haven.util.io.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import net.ssehub.kernel_haven.util.io.TableElement;
import net.ssehub.kernel_haven.util.io.TableRow;

/**
 * Tests the {@link CsvWriter}.
 *
 * @author Adam
 */
public class CsvWriterTest {

    /**
     * A simple structure to be written. 
     */
    @TableRow
    private static class Simple {
        
        @TableElement(name = "Integer", index = 0)
        private int integer;
        
        @TableElement(name = "String", index = 1)
        private String str;
     
        /**
         * Creates an instance.
         * 
         * @param integer The integer value.
         * @param str The string value.
         */
        public Simple(int integer, String str) {
            this.integer = integer;
            this.str = str;
        }
        
    }
    
    /**
     * A simple structure to be written, without annotations. 
     */
    private static class NoAnnotations {
        
        private char character;
        
        private String str;
        
        /**
         * Creates an instance.
         * 
         * @param character The character value.
         * @param str The string value.
         */
        public NoAnnotations(char character, String str) {
            this.character = character;
            this.str = str;
        }
        
        @Override
        public String toString() {
            return character + " " + str;
        }
        
    }
    
    /**
     * Tests whether a simple structure with annotations can be written.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteAnnotationsSimple() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.writeRow(new Simple(1, "one"));
            writer.writeRow(new Simple(2, "two"));
            writer.writeRow(new Simple(3, "three"));
            
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
            writer.writeRow(new Simple(1, "one"));
            writer.writeRow(new NoAnnotations('b', "three"));
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
            writer.writeRow(new NoAnnotations('a', "one"));
            writer.writeRow(new NoAnnotations('b', "two"));
            writer.writeRow(new NoAnnotations('c', "three"));
            
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
            writer.writeRow(new Simple(1, "on;e"));
            writer.writeRow(new Simple(2, "tw\no"));
            writer.writeRow(new Simple(3, "th;re\"e"));
            
        }
        
        assertThat(out.toString(), is("Integer;String\n1;\"on;e\"\n2;\"tw\no\"\n3;\"th;re\"\"e\"\n"));
    }

}
