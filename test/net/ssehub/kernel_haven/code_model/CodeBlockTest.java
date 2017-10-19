package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;

/**
 * Tests the {@link CodeBlock} class.
 *
 * @author Adam
 */
public class CodeBlockTest {

    private static final VariableCache CACHE = new VariableCache();
    private static final Parser<Formula> PARSER = new Parser<>(new CStyleBooleanGrammar(CACHE));
    
    /**
     * Tests the different getters for nested elements.
     */
    @Test
    public void testStructure() {
        CodeBlock main = new CodeBlock(1, 100, new File("test.c"), True.INSTANCE, True.INSTANCE);
        CodeBlock nested1 = new CodeBlock(True.INSTANCE);
        CodeBlock nested2 = new CodeBlock(True.INSTANCE);
        
        main.addNestedElement(nested1);
        main.addNestedElement(nested2);
        
        assertThat(main.getNestedElementCount(), is(2));
        assertThat(main.getNestedElement(0), sameInstance(nested1));
        assertThat(main.getNestedElement(1), sameInstance(nested2));
        
        Iterator<CodeBlock> iter = main.iterateNestedBlocks().iterator();
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested1));
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested2));
        assertThat(iter.hasNext(), is(false));
    }
    
    /**
     * Tests whether adding a wrong type of child correctly throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWrongSyntaxElement() {
        CodeBlock main = new CodeBlock(True.INSTANCE);
        main.addNestedElement(new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, True.INSTANCE, True.INSTANCE));
    }
    
    /**
     * Tests the serializeCsv() method.
     */
    @Test
    public void testSerializeCsv() {
        CodeBlock main = new CodeBlock(1, 100, new File("test.c"), True.INSTANCE, True.INSTANCE);
        CodeBlock nested1 = new CodeBlock(True.INSTANCE);
        main.addNestedElement(nested1); // to check that nesting has on effect on the CSV
        
        assertThat(main.serializeCsv(), is(Arrays.asList("1", "100", "test.c", "1", "1")));
        assertThat(nested1.serializeCsv(), is(Arrays.asList("-1", "-1", "<unknown>", "null", "1")));
    }
    
    /**
     * Tests that creating a block from CSV works correctly.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testCreateFromCsv() throws FormatException {
        String[] csv1 = {"1", "100", "test.c", "1", "1"};
        CodeBlock b1 = CodeBlock.createFromCsv(csv1, PARSER);
        
        assertThat(b1.getLineStart(), is(1));
        assertThat(b1.getLineEnd(), is(100));
        assertThat(b1.getSourceFile(), is(new File("test.c")));
        assertThat(b1.getCondition(), is(True.INSTANCE));
        assertThat(b1.getPresenceCondition(), is(True.INSTANCE));
        assertThat(b1.getNestedElementCount(), is(0));
        
        String[] csv2 = {"-1", "-1", "<unknown>", "null", "1"};
        CodeBlock b2 = CodeBlock.createFromCsv(csv2, PARSER);
        
        assertThat(b2.getLineStart(), is(-1));
        assertThat(b2.getLineEnd(), is(-1));
        assertThat(b2.getSourceFile(), is(new File("<unknown>")));
        assertThat(b2.getCondition(), nullValue());
        assertThat(b2.getPresenceCondition(), is(True.INSTANCE));
        assertThat(b2.getNestedElementCount(), is(0));
    }
    
    /**
     * Tests that a malformed CSV correctly throws an exception.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidConditionFormula() throws FormatException {
        String[] csv = {"1", "100", "test.c", "1 &&", "1"};
        CodeBlock.createFromCsv(csv, PARSER);
    }
    
    /**
     * Tests that a malformed CSV correctly throws an exception.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidPCFormula() throws FormatException {
        String[] csv = {"1", "100", "test.c", "1", "1 &&"};
        CodeBlock.createFromCsv(csv, PARSER);
    }
    
    /**
     * Tests that a malformed CSV correctly throws an exception.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testTooShortCsv() throws FormatException {
        String[] csv = {"1", "100", "test.c", "1"};
        CodeBlock.createFromCsv(csv, PARSER);
    }
    
    /**
     * Tests that a malformed CSV correctly throws an exception.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testTooLongCsv() throws FormatException {
        String[] csv = {"1", "100", "test.c", "1", "1", "too long"};
        CodeBlock.createFromCsv(csv, PARSER);
    }
    
}
