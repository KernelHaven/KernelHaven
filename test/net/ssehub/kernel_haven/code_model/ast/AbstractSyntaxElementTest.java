package net.ssehub.kernel_haven.code_model.ast;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link AbstractSyntaxElementNoNesting} and {@link AbstractSyntaxElementWithNesting} classes.
 *
 * @author Adam
 */
@SuppressWarnings("null")
public class AbstractSyntaxElementTest {

    /**
     * Tests the {@link AbstractSyntaxElementNoNesting#toString()} method on
     * {@link AllAstTests#createFullAst()}.
     */
    @Test
    public void testToString() {
        String s = AllAstTests.createFullAst().toString();

        assertEquals("[1] File dummy_test.c\n"
                + "\t[1] #INCLUDE\n"
                + "\t\t[1] < stdio.h >\n"
                + "\t[1] Function simpleFunction\n"
                + "\t\t[1] void simpleFunction ( )\n"
                + "\t\t[1] IF (3 siblings)\n"
                + "\t\t\t[1] if ( 1 > 2 )\n"
                + "\t\t\t[1] CompoundStatement\n"
                + "\t\t\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t\t\t[1] return 1 ;\n"
                + "\t\t\t[1] ELSE_IF (3 siblings)\n"
                + "\t\t\t\t[1] else if ( 1 < 2 )\n"
                + "\t\t\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t\t\t[1] ;\n"
                + "\t\t\t[1] ELSE (3 siblings)\n"
                + "\t\t\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t\t\t[1] ;\n"
                + "\t\t[1] Label:\n"
                + "\t\t\t[1] lbl:\n"
                + "\t\t[1] Switch (2 cases)\n"
                + "\t\t\t[1] switch ( 3 )\n"
                + "\t\t\t[1] CASE\n"
                + "\t\t\t\t[1] 1\n"
                + "\t\t\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t\t\t[1] ;\n"
                + "\t\t\t[1] DEFAULT\n"
                + "\t\t\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t\t\t[1] ;\n"
                + "\t\t[1] INSTRUCTION-Statement:\n"
                + "\t\t\t[1] CodeList\n"
                + "\t\t\t\t[1] int a = \n"
                + "\t\t\t\t[A] #IFDEF A\n"
                + "\t\t\t\t\t[A] 1\n"
                + "\t\t\t\t[!A] #ELSE !A\n"
                + "\t\t\t\t\t[!A] 2\n"
                + "\t\t\t\t[1] ;\n"
                + "\t\t[1] Comment:\n"
                + "\t\t\t[1] /* test */\n"
                + "\t\t[1] WHILE-loop\n"
                + "\t\t\t[1] while ( 1 )\n"
                + "\t\t\t[1] TYPEDEF-Definition\n"
                + "\t\t\t\t[1] typedef int int_32 ;\n", s);
    }
    
    /**
     * A mock for testing purposes.
     */
    private static class PseudoSyntaxElement extends AbstractSyntaxElementNoNesting {

        /**
         * Creates this mock.
         * 
         * @param presenceCondition The pc.
         */
        public PseudoSyntaxElement(Formula presenceCondition) {
            super(presenceCondition);
        }

        @Override
        public @NonNull String elementToString(@NonNull String indentation) {
            return "PseudoSyntaxElement";
        }

        @Override
        public void accept(@NonNull ISyntaxElementVisitor visitor) {
        }
        
    }
    
    /**
     * A mock for testing purposes.
     */
    private static class PseudoSyntaxElementWC extends AbstractSyntaxElementWithNesting {

        /**
         * Creates this mock.
         * 
         * @param presenceCondition The pc.
         */
        public PseudoSyntaxElementWC(Formula presenceCondition) {
            super(presenceCondition);
        }

        @Override
        public @NonNull String elementToString(@NonNull String indentation) {
            return "PseudoSyntaxElement";
        }

        @Override
        public void accept(@NonNull ISyntaxElementVisitor visitor) {
        }
        
    }
    
    /**
     * Tests the default values for attributes.
     */
    @Test
    public void testDefaults() {
        Formula f = new Variable("A");
        
        PseudoSyntaxElement element = new PseudoSyntaxElement(f);
        
        assertThat(element.getCondition(), nullValue());
        assertThat(element.getLineStart(), is(-1));
        assertThat(element.getLineEnd(), is(-1));
        assertThat(element.getPresenceCondition(), is(f));
        assertThat(element.getSourceFile(), is(new java.io.File("<unknown>")));
        assertThat(element.getNestedElementCount(), is(0));
        
        try {
            element.getNestedElement(0);
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        
        try {
            element.addNestedElement(new PseudoSyntaxElement(True.INSTANCE));
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
    
    /**
     * Tests that values for attributes are stored correctly.
     */
    @Test
    public void testStorage() {
        Formula f = new Variable("A");
        
        PseudoSyntaxElement element = new PseudoSyntaxElement(f);
        
        element.setCondition(f);
        element.setLineStart(2);
        element.setLineEnd(10);
        element.setSourceFile(new java.io.File("test.c"));
        
        assertThat(element.getCondition(), is(f));
        assertThat(element.getLineStart(), is(2));
        assertThat(element.getLineEnd(), is(10));
        assertThat(element.getPresenceCondition(), is(f));
        assertThat(element.getSourceFile(), is(new java.io.File("test.c")));
        assertThat(element.getNestedElementCount(), is(0));
    }
    
    /**
     * Tests the toString() method with a really long condition.
     */
    @Test
    public void testToStringLongCondition() {
        Formula varLong = new Variable("VAR_THIS_IS_A_VERY_LONG_VARIABLE_NAME_THAT_IS_LONGER_THAN_64_CHARACTERS");
        
        PseudoSyntaxElement element = new PseudoSyntaxElement(True.INSTANCE);
        element.setCondition(varLong);
        
        assertThat(element.toString(), is("[...] PseudoSyntaxElement"));
    }
    
    /**
     * Tests the toString() method with a <code>null</code> condition.
     */
    @Test
    public void testToStringNullCondition() {
        PseudoSyntaxElement element = new PseudoSyntaxElement(True.INSTANCE);
        
        assertThat(element.toString(), is("[<null>] PseudoSyntaxElement"));
    }
    
    /**
     * Tests that {@link AbstractSyntaxElementWithNesting} correctly stores children.
     */
    @Test
    public void testChildStorage() {
        PseudoSyntaxElementWC element = new PseudoSyntaxElementWC(True.INSTANCE);
        
        assertThat(element.getNestedElementCount(), is(0));
        try {
            element.getNestedElement(-1);
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            element.getNestedElement(0);
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        
        element.addNestedElement(new PseudoSyntaxElement(True.INSTANCE));
        assertThat(element.getNestedElementCount(), is(1));
        assertThat(element.getNestedElement(0), notNullValue());
        try {
            element.getNestedElement(1);
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        
        element.addNestedElement(new PseudoSyntaxElement(True.INSTANCE));
        assertThat(element.getNestedElementCount(), is(2));
        assertThat(element.getNestedElement(0), notNullValue());
        assertThat(element.getNestedElement(1), notNullValue());
        try {
            element.getNestedElement(2);
            fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
    
    /**
     * Tests the iterator of {@link AbstractSyntaxElementWithNesting}.
     */
    @Test
    public void testIterator() {
        PseudoSyntaxElementWC element = new PseudoSyntaxElementWC(True.INSTANCE);
        element.addNestedElement(new PseudoSyntaxElement(True.INSTANCE));
        element.addNestedElement(new PseudoSyntaxElement(True.INSTANCE));
        
        Iterator<ISyntaxElement> it = element.iterator();
        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), notNullValue());
        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), notNullValue());
        assertThat(it.hasNext(), is(false));
    }

}
