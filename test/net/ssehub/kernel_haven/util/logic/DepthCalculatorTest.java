package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the {@link DepthCalculator}.
 * 
 * @author Adam
 */
public class DepthCalculatorTest {

    /**
     * Tests that the depth of a single variable is 0.
     */
    @Test
    public void testSingleVariable() {
        assertThat(new DepthCalculator().visit(new Variable("A")), is(0));
    }
    
    /**
     * Tests that the depth of a single constant is 0.
     */
    @Test
    public void testConstants() {
        assertThat(new DepthCalculator().visit(True.INSTANCE), is(0));
        assertThat(new DepthCalculator().visit(False.INSTANCE), is(0));
    }
    
    /**
     * Tests that the depth of a single operator is 1.
     */
    @Test
    public void testSingleOperator() {
        assertThat(new DepthCalculator().visit(or("A", "B")), is(1));
        assertThat(new DepthCalculator().visit(and("A", "B")), is(1));
        assertThat(new DepthCalculator().visit(not("A")), is(1));
    }
    
    /**
     * Tests that a nested operator is added correctly.
     */
    @Test
    public void testNestedOperators() {
        assertThat(new DepthCalculator().visit(or("A", and("B", "C"))), is(2));
        assertThat(new DepthCalculator().visit(not(not(not("A")))), is(3));
        assertThat(new DepthCalculator().visit(and("A", or(and("B", "D"), "C"))), is(3));
    }
    
    /**
     * Tests that a nested operator is added correctly when two sides have the same depth.
     */
    @Test
    public void testSameDepthOnTwoSides() {
        assertThat(new DepthCalculator().visit(or(and("A", "C"), and("B", "C"))), is(2));
    }
    
}
