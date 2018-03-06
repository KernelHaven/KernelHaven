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
@SuppressWarnings("null")
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
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c", "d"}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(2));
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
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {""}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c", "d"}));
            assertThat(reader.getLineNumber(), is(3));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(3));
        }
    }
    
    /**
     * Tests reading data with escaped fields.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscaped() throws IOException {
        String csv = "1;\"2;3\";3\na;\"b\nc\";d\ne;f\"\"g;h\n\"9;8\"\"7\n6\"\n\"\"\"a\"\"\"";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2;3", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b\nc", "d"}));
            assertThat(reader.getLineNumber(), is(3)); // increased by 2, because escaped \n
            assertThat(reader.readNextRow(), is(new String[] {"e", "f\"\"g", "h"}));
            assertThat(reader.getLineNumber(), is(4));
            assertThat(reader.readNextRow(), is(new String[] {"9;8\"7\n6"}));
            assertThat(reader.getLineNumber(), is(6)); // increased by 2, because escaped \n
            assertThat(reader.readNextRow(), is(new String[] {"\"a\""}));
            assertThat(reader.getLineNumber(), is(7));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(7));
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
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1\"2", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"1\"\"2", "3"}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), is(new String[] {"a\"b", "c\"", "d"}));
            assertThat(reader.getLineNumber(), is(3));
            assertThat(reader.readNextRow(), is(new String[] {"a\"b\"c"}));
            assertThat(reader.getLineNumber(), is(4));
            assertThat(reader.readNextRow(), is(new String[] {"xy\"z;a\n"}));
            assertThat(reader.getLineNumber(), is(6)); // increased by 2, because escaped \n
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(6));
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
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b;c", "d"}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(2));
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
            assertThat(reader.getLineNumber(), is(0));
            
            Simple obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(1));
            assertThat(obj.getStr(), is("abc"));
            assertThat(reader.getLineNumber(), is(1));
            
            obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(2));
            assertThat(obj.getStr(), is("def"));
            assertThat(reader.getLineNumber(), is(2));
            
            assertThat(reader.readAsObject(new Simple.SimpleFactory()), nullValue());
            assertThat(reader.getLineNumber(), is(2));
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
            assertThat(reader.getLineNumber(), is(0));
            
            Simple obj = reader.readAsObject(new Simple.SimpleFactory());
            assertThat(obj.getInteger(), is(1));
            assertThat(obj.getStr(), is("abc"));
            assertThat(reader.getLineNumber(), is(1));
            
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
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readFull(), is(new String[][] {
                new String[] {"1", "2", "3"},
                new String[] {"a", "b", "c", "d"}
            }));
            assertThat(reader.getLineNumber(), is(2));
        }
    }
    
    /**
     * Tests whether \r and \r\n are considered to be line-breaks, too.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testOtherLineBreaks() throws IOException {
        String csv = "1;2;3\ra;b;c\r\nx;y;z";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c"}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), is(new String[] {"x", "y", "z"}));
            assertThat(reader.getLineNumber(), is(3));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(3));
        }
    }
    
    /**
     * Tests whether \r and \r\n are handled correctly when they appear in escaped fields.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testOtherLineBreaksInEscaped() throws IOException {
        String csv = "1;2;\"3\r\"\r\na;\"b\r\nb\";c\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3\r"}));
            assertThat(reader.getLineNumber(), is(2)); // increased by 2, because of escaped \r
            assertThat(reader.readNextRow(), is(new String[] {"a", "b\r\nb", "c"}));
            assertThat(reader.getLineNumber(), is(4)); // increased by 2, because of escaped \r\n
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(4));
        }
    }
    
    /**
     * Tests reading data with escaped fields. This tests whether an escaped quote in front of a delimiter is handled
     * correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscapeQuotesBeforeDelimiter() throws IOException {
        String csv = "a;\"b\"\";\";c";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b\";", "c"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(1));
        }
    }
    
    /**
     * Tests reading data with escaped fields. This tests whether an escaped quote after of a delimiter is handled
     * correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscapeQuotesAfterDelimiter() throws IOException {
        String csv = "a;\"b;\"\"b\";c";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b;\"b", "c"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(1));
        }
    }
    
    /**
     * Tests reading data with escaped fields. This tests whether an escaped quote after of a delimiter at the end of
     * a field is handled correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscapeQuotesAfterDelimiterAtEndOfField() throws IOException {
        String csv = "a;\"b;\"\"\";c";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b;\"", "c"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(1));
        }
    }
    
    /**
     * Tests reading data with escaped fields. This tests whether an escaped quote after and before a delimiter is
     * handled correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testEscapeQuotesAfterAndBeforeDelimiter() throws IOException {
        String csv = "a;\"b\"\";\"\"b\";c";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b\";\"b", "c"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(1));
        }
    }
    
    /**
     * Tests reading data with escaped fields. This test case derived from a bug that was found with real-world data.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testDifficultEscape() throws IOException {
        String csv = "net.ssehub.kernel_haven.code_model.SyntaxElement;0;34;32;arch/x86/include/asm/alternative.h;1;1;"
                + "StringLit;1;Value\nnet.ssehub.kernel_haven.code_model.SyntaxElement;1;34;32;arch/x86/include/asm/"
                + "alternative.h;1;1;\"Literal: \"\";\"\"\";0\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {
                "net.ssehub.kernel_haven.code_model.SyntaxElement",
                "0",
                "34",
                "32",
                "arch/x86/include/asm/alternative.h",
                "1",
                "1",
                "StringLit",
                "1",
                "Value"
            }));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {
                "net.ssehub.kernel_haven.code_model.SyntaxElement",
                "1",
                "34",
                "32",
                "arch/x86/include/asm/alternative.h",
                "1",
                "1",
                "Literal: \";\"",
                "0"
            }));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(2));
        }
    }
    
    /**
     * Tests line numbers with a trailing \n.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testTrailingLineBreak() throws IOException {
        String csv = "1;2;3\na;b;c;d\n";
        
        try (CsvReader reader = new CsvReader(new ByteArrayInputStream(csv.getBytes()))) {
            assertThat(reader.getLineNumber(), is(0));
            assertThat(reader.readNextRow(), is(new String[] {"1", "2", "3"}));
            assertThat(reader.getLineNumber(), is(1));
            assertThat(reader.readNextRow(), is(new String[] {"a", "b", "c", "d"}));
            assertThat(reader.getLineNumber(), is(2));
            assertThat(reader.readNextRow(), nullValue());
            assertThat(reader.getLineNumber(), is(2));
        }
    }

}
