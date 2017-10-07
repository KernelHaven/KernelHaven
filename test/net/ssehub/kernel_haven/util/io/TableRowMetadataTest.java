package net.ssehub.kernel_haven.util.io;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the {@link TableRowMetadata} class.
 *
 * @author Adam
 */
public class TableRowMetadataTest {
    
    /**
     * A simple table row structure to be written. 
     */
    @TableRow
    public static class Simple {
        
        private int integer;
        
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
        
        /**
         * The integer value.
         * @return Integer value.
         */
        @TableElement(name = "Integer", index = 0)
        public int getInteger() {
            return integer;
        }
        
        /**
         * The string value.
         * 
         * @return The String value.
         */
        @TableElement(name = "String", index = 1)
        public String getStr() {
            return str;
        }
        
    }
    
    /**
     * A simple table row structure to be written, without annotations. 
     */
    public static class NoAnnotations {
        
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
     * Tests the isTablRow() method.
     */
    @Test
    public void testIsTableRow() {
        assertThat(TableRowMetadata.isTableRow(Simple.class), is(true));
        assertThat(TableRowMetadata.isTableRow(NoAnnotations.class), is(false));
    }
    
    /**
     * Tests whether the constructor throws an exception for non-annotated classes.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        new TableRowMetadata(NoAnnotations.class);
    }
    
    /**
     * Tests whether the header names are read correctly.
     */
    @Test
    public void testHeaders() {
        TableRowMetadata metadata = new TableRowMetadata(Simple.class);

        assertThat(metadata.getHeaders().length, is(2));
        assertThat(metadata.getHeaders()[0], is("Integer"));
        assertThat(metadata.getHeaders()[1], is("String"));
    }
    
    /**
     * Tests the isSameClass() method.
     */
    @Test
    public void testIsSameClass() {
        TableRowMetadata metadata = new TableRowMetadata(Simple.class);
        
        assertThat(metadata.isSameClass(new Simple(1, "")), is(true));
        assertThat(metadata.isSameClass(new NoAnnotations('a', "")), is(false));
    }
    
    /**
     * Tests the getContent() method.
     * 
     * @throws ReflectiveOperationException unwanted.
     */
    @Test
    public void testGetContent() throws ReflectiveOperationException {
        TableRowMetadata metadata = new TableRowMetadata(Simple.class);
        
        String[] content = metadata.getContent(new Simple(34, "thirtythree"));
        assertThat(content.length, is(2));
        assertThat(content[0], is("34"));
        assertThat(content[1], is("thirtythree"));
    }
    
    /**
     * Tests the getContent() method with an invalid instance.
     * 
     * @throws ReflectiveOperationException wanted.
     */
    @Test(expected = ReflectiveOperationException.class)
    public void testGetContentInvalid() throws ReflectiveOperationException {
        TableRowMetadata metadata = new TableRowMetadata(Simple.class);
        
        metadata.getContent(new NoAnnotations('b', "bee"));
    }

}
