package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the {@link FormulaBuilder} methods.
 *
 * @author Adam
 */
public class FormulaBuilderTest {

    /**
     * Tests the utility methods for creating conjunctions.
     */
    @Test
    public void testConjunctions() {
        assertThat(and(True.INSTANCE, False.INSTANCE),
                is(new Conjunction(True.INSTANCE, False.INSTANCE)));
        
        assertThat(and("A", False.INSTANCE),
                is(new Conjunction(new Variable("A"), False.INSTANCE)));
        
        assertThat(and(True.INSTANCE, "B"),
                is(new Conjunction(True.INSTANCE, new Variable("B"))));
        
        assertThat(and("A", "B"),
                is(new Conjunction(new Variable("A"), new Variable("B"))));
    }
    
    /**
     * Tests the utility methods for creating disjunctions.
     */
    @Test
    public void testDisunctions() {
        assertThat(or(True.INSTANCE, False.INSTANCE),
                is(new Disjunction(True.INSTANCE, False.INSTANCE)));
        
        assertThat(or("A", False.INSTANCE),
                is(new Disjunction(new Variable("A"), False.INSTANCE)));
        
        assertThat(or(True.INSTANCE, "B"),
                is(new Disjunction(True.INSTANCE, new Variable("B"))));
        
        assertThat(or("A", "B"),
                is(new Disjunction(new Variable("A"), new Variable("B"))));
    }
    
    /**
     * Tests the utility methods for creating negations.
     */
    @Test
    public void testNegations() {
        assertThat(not(True.INSTANCE),
                is(new Negation(True.INSTANCE)));
        
        assertThat(not("A"),
                is(new Negation(new Variable("A"))));
    }
    
    /**
     * Test all methods combined.
     */
    @Test
    public void testCombined() {
        assertThat(or("A", and(not("B"), "C")),
                is(new Disjunction(new Variable("A"),
                        new Conjunction(new Negation(new Variable("B")), new Variable("C")))));
    }
    
}
