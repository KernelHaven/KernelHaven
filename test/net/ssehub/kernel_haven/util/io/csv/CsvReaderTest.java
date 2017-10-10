package net.ssehub.kernel_haven.util.io.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.io.TableRowMetadataTest.Simple;

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
        String csv = "1\"2;3\n1\"\"2;3\na\"b;c\";d\n\"a\"b\"c\"\n\"xy\"z;a\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readNextRow(), is(new String[] {"1\"2", "3"}));
            assertThat(reader.readNextRow(), is(new String[] {"1\"\"2", "3"}));
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
    
    /**
     * Tests the {@link ITableReader#readAsObject(net.ssehub.kernel_haven.util.io.ITableReader.Factory)} method.
     * 
     * @throws IOException unwanted.
     * @throws FormatException unwanted.
     */
    @Test
    public void testReadObject() throws IOException, FormatException {
        String csv = "1;abc\n2;def";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            Simple obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(1));
            assertThat(obj.getStr(), is("abc"));
            
            obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(2));
            assertThat(obj.getStr(), is("def"));
            
            assertThat(reader.readAsObject(new Simple.SimpleFactory()), nullValue());
        }
    }
    
    /**
     * Tests the {@link ITableReader#readAsObject(net.ssehub.kernel_haven.util.io.ITableReader.Factory)} method.
     * 
     * @throws IOException unwanted.
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testReadObjectFormatException() throws IOException, FormatException {
        String csv = "1;abc\nnot_a_number;def";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            Simple obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(1));
            assertThat(obj.getStr(), is("abc"));
            
            reader.readAsObject(new Simple.SimpleFactory());
        }
    }
    
    /**
     * Tests the {@link ITableReader#readFull()} method.
     * 
     * @throws IOException unwanted.
     */
    public void testReadFull() throws IOException {
        String csv = "1;2;3\na;b;c;d";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.readFull(), is(new String[][] {
                new String[] {"1", "2", "3"},
                new String[] {"a", "b", "c", "d"}
            }));
        }
    }

}
