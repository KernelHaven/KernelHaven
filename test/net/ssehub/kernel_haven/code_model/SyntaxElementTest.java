package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the syntax element class.
 * 
 * @author Adam
 */
public class SyntaxElementTest {

    /**
     * Tests getters for nested elements. 
     */
    @Test
    public void testNestedStructure() {
        SyntaxElement main = new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, True.INSTANCE, True.INSTANCE);
        SyntaxElement nested1 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, True.INSTANCE);
        SyntaxElement nested2 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, True.INSTANCE);
        SyntaxElement nested3 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, True.INSTANCE);
        
        main.addNestedElement(nested1);
        main.addNestedElement(nested2, "nested");
        main.addNestedElement(nested3, "nested");
        
        assertThat(main.getNestedElementCount(), is(3));
        assertThat(main.getNestedElement(0), sameInstance(nested1));
        assertThat(main.getNestedElement(1), sameInstance(nested2));
        assertThat(main.getNestedElement(2), sameInstance(nested3));
        
        assertThat(main.getNestedElement("nested"), sameInstance(nested2));
        assertThat(main.getNestedElement("other"), nullValue());
        
        Iterator<SyntaxElement> iter = main.iterateNestedSyntaxElements().iterator();
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested1));
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested2));
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested3));
        assertThat(iter.hasNext(), is(false));
    }
    
    /**
     * Tests whether adding a wrong type of child correctly throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWrongSyntaxElement() {
        SyntaxElement main = new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, True.INSTANCE, True.INSTANCE);
        main.addNestedElement(new CodeBlock(True.INSTANCE));
    }
    
    /**
     * Tests whether {@link SyntaxElement#setDeserializedRelations(java.util.List)} correctly sets the relations
     * for the following elements.
     */
    @Test
    public void testSetDeserializedRelations() {
        SyntaxElement main = new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, True.INSTANCE, True.INSTANCE);
        SyntaxElement nested1 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, True.INSTANCE);
        SyntaxElement nested2 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, True.INSTANCE);
        
        List<String> relations = new LinkedList<>();
        relations.add("relation1");
        relations.add("relation2");
        main.setDeserializedRelations(relations);

        assertThat(main.getNestedElement("relation1"), nullValue());
        
        main.addNestedElement(nested1);
        main.addNestedElement(nested2);

        assertThat(main.getNestedElementCount(), is(2));
        assertThat(main.getNestedElement("relation1"), sameInstance(nested1));
        assertThat(main.getNestedElement("relation2"), sameInstance(nested2));
    }
    
    /**
     * Tests the toString() method. This is a rather complicated method, thus this gets its own test case.
     */
    @Test
    public void testToString() {
        Formula varA = new Variable("VAR_A");
        Formula varB = new Variable("VAR_B");
        Formula varLong = new Variable("VAR_THIS_IS_A_VERY_LONG_VARIABLE_NAME_THAT_IS_LONGER_THAN_64_CHARACTERS");
        
        SyntaxElement main = new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, varA, varA);
        SyntaxElement nested1 = new SyntaxElement(SyntaxElementTypes.BREAK_STATEMENT, True.INSTANCE, varA);
        SyntaxElement nested2 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, varB, new Conjunction(varA, varB));
        SyntaxElement nested3 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, varLong, 
                new Conjunction(varA, varLong));
        nested3.setSourceFile(new File("testfile.c"));
        nested3.setLineStart(1);
        nested3.setLineEnd(2);
        
        main.addNestedElement(nested1);
        main.addNestedElement(nested2, "second");
        main.addNestedElement(nested3, "third");
        
        assertThat(main.toString(), is(" [VAR_A] [<unknown>:-1] CompoundStatement\n"
                + "\t [1] [<unknown>:-1] BreakStatement\n"
                + "\tsecond [VAR_B] [<unknown>:-1] ExprStatement\n" 
                + "\tthird [...] [testfile.c:1] ExprStatement\n"));
        
        System.out.println(main.toString());
    }
    
}
