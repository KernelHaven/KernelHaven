package net.ssehub.kernel_haven.util.io.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.util.FormatException;

/**
 * Tests the {@link JsonParser}.
 *
 * @author Adam
 */
@RunWith(Parameterized.class)
public class ParameterizedJsonParserTest {
    
    private String input;
    
    private JsonElement expectedOutput;
    
    /**
     * Creates the test instance.
     * 
     * @param input The input string to parse.
     * @param expectedOutput The expected output.
     * @param name The name of the test. Ignored.
     */
    public ParameterizedJsonParserTest(String input, JsonElement expectedOutput, String name) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
    
    /**
     * Creates the parameters for this test.
     * 
     * @return The parameters of this test.
     */
    @Parameters(name = "{2}: {0}")
    public static Collection<Object[]> getParameters() {
        JsonObject o1 = new JsonObject();
        o1.putElement("someKey", new JsonNumber(42));
        
        JsonObject o2 = new JsonObject();
        o2.putElement("k1", new JsonNumber(42));
        o2.putElement("k2", new JsonString("hey"));
        
        JsonList l1 = new JsonList();
        l1.addElement(new JsonNumber(-23));
        
        JsonList l2 = new JsonList();
        l2.addElement(new JsonNumber(-23));
        l2.addElement(new JsonString("ho"));
        
        return Arrays.asList(
            new Object[] {"5264", new JsonNumber(5264), "positive integer"},
            new Object[] {"-654564", new JsonNumber(-654564), "negative integer"},
            
            new Object[] {"\"\"", new JsonString(""), "empty string"},
            new Object[] {"\"hello world\"", new JsonString("hello world"), "simple string"},
            
            new Object[] {"\"a \\\\ \\\" \\/ \\b \\n \\r \\t \\u004B \\u004c b\"",
                new JsonString("a \\ \" / \b \n \r \t K L b"), "escaped string"},
            
            new Object[] {"[]", new JsonList(), "empty list"},
            new Object[] {"\n[ \t ] \r", new JsonList(), "empty list with whitespace"},
            new Object[] {"[-23]", l1, "list with one element"},
            new Object[] {"[-23 , \"ho\"]", l2, "list with two elements"},
            
            new Object[] {"{}", new JsonObject(), "empty object"},
            new Object[] {" {\t } \n", new JsonObject(), "empty object with whitespace"},
            new Object[] {"{\"someKey\": 42}", o1, "object with one element"},
            new Object[] {" {  \r\n\t\"someKey\" \t: \n 42  } ", o1, "object with one element and whitespace"},
            new Object[] {"{\"k2\": \"hey\" , \"k1\": 42}", o2, "object with two elements"},
            
            new Object[] {"null", JsonNull.INSTANCE, "null value"},
            new Object[] {"true", JsonBoolean.TRUE, "true value"},
            new Object[] {"false", JsonBoolean.FALSE, "true value"},
            
            new Object[] {"\t54   \n ", new JsonNumber(54), "whitespaces arround number"}
        );
    }
    
    /**
     * The actual test method. Tests if parsing the input results in the expected output.
     * 
     * @throws FormatException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    @SuppressWarnings("null")
    public void test() throws FormatException, IOException {
        try (JsonParser parser = new JsonParser(new StringReader(input))) {
            assertThat(parser.parse(), is(expectedOutput));
        }
    }

}
