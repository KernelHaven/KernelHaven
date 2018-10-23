package net.ssehub.kernel_haven.util.io.json;

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
public class ParameterizedJsonParserNegativeTest {
    
    private String input;
    
    /**
     * Creates the test instance.
     * 
     * @param input The input string to parse.
     * @param name The name of the test. Ignored.
     */
    public ParameterizedJsonParserNegativeTest(String input, String name) {
        this.input = input;
    }
    
    /**
     * Creates the parameters for this test.
     * 
     * @return The parameters of this test.
     */
    @Parameters(name = "{1}: {0}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
            new Object[] {"52 64", "number with space"},
            new Object[] {"52a64", "number with letter"},
            
            new Object[] {"526.", "number with missing fraction"},
            new Object[] {"52621321321321325454543637547854654654654654642523432423423423423423", "unparseable long"},
            new Object[] {"526E", "number with missing exponent"},
            new Object[] {"526E+", "number with only sign as exponent"},
            
            new Object[] {"treu", "true typo"},
            new Object[] {"fasle", "false typo"},
            
            new Object[] {"nul", "null typo"},
            
            new Object[] {"[ 53, 42 ", "missing end list"},
            new Object[] {"[ 53, 42, ]", "list dangling comma"},
            
            new Object[] {"{ \"a\": 42 ", "missing end object"},
            new Object[] {"{ \"a\": 42, }", "object dangling comma"},
            new Object[] {"{ \"a\" 42, }", "object missing colon"},
            new Object[] {"{ \"a\" = 42, }", "object equals instead of colon"},
            new Object[] {"{ a = 42 }", "object key not string"},
            
            new Object[] {"\"a \\g b\"", "string with wrong quotation"},
            new Object[] {"\"a \\u123 b\"", "string with bad \\u value"},
            
            new Object[] {"\"hello world", "string with missing end-quote"}
        );
    }
    
    /**
     * The actual test method. Tests if parsing the input results in the expected output.
     * 
     * @throws FormatException unwanted.
     * @throws IOException unwanted.
     */
    @Test(expected = FormatException.class)
    public void test() throws FormatException, IOException {
        try (JsonParser parser = new JsonParser(new StringReader(input))) {
            parser.parse();
        }
    }

}
