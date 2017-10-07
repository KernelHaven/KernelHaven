package net.ssehub.kernel_haven.util.io.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests the {@link CsvReader} class.
 *
 * @author Adam
 */
public class CsvReaderTest {
    
    /**
     * Tests reading simple data.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testSimple() throws IOException {
        String csv = "1;2;3\na;b;c;d";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c", "d"}));
            assertThat(reader.readNextRow(), nullValue());
        }
    }
    
    /**
     * Tests reading data with empty fields and lines.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEmptyData() throws IOException {
        String csv = "1;;3\n\na;b;c;d";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readNextRow(), is(new String[] {"1", "", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {""}));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c", "d"}));
            assertThat(reader.readNextRow(), nullValue());
        }
    }
    
    /**
     * Tests reading data with escaped fields.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscaped() throws IOException {
        String csv = "1;\"2;3\";3\na;\"b\nc\";d\ne;f\"\"g;h\n\"9;8\"\"7\n6\"\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readNextRow(), is(new String[] {"1", "2;3", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b\nc", "d"}));
            assertThat(reader.readNextRow(), is(new String[] {"e", "f\"\"g", "h"}));
            assertThat(reader.readNextRow(), is(new String[] {"9;8\"7\n6"}));
            assertThat(reader.readNextRow(), nullValue());
        }
    }
    
    /**
     * Tests reading data with improperly escaped fields. This is based on the behavior of LibreOffice Calc.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testMalformedEscape() throws IOException {
        String csv = "1\"2;3\na\"b;c\";d\n\"a\"b\"c\"\n\"xy\"z;a\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readNextRow(), is(new String[] {"1\"2", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {"a\"b", "c\"", "d"}));
            assertThat(reader.readNextRow(), is(new String[] {"a\"b\"c"}));
            assertThat(reader.readNextRow(), is(new String[] {"xy\"z;a\n"}));
            assertThat(reader.readNextRow(), nullValue());
        }
    }
    
    /**
     * Tests reading with a different separator.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testDifferentSeparator() throws IOException {
        String csv = "1,2,3\na,b;c,d";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()), ',')) {
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b;c", "d"}));
            assertThat(reader.readNextRow(), nullValue());
        }
    }

}
