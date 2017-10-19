package net.ssehub.kernel_haven.util.logic;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * Tests the {@link VariableFinder} class.
 * 
 * @author Adam
 */
public class VariableFinderTest {

    /**
     * Tests a simple variable.
     */
    @Test
    public void testSimple() {
        Variable a = new Variable("A");
        
        VariableFinder finder = new VariableFinder();
        finder.visit(a);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a))));
    }
    
    /**
     * Tests a complex formula.
     */
    @Test
    public void tesComplex() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Variable c = new Variable("C");
        
        Formula f = new Conjunction(new Disjunction(a, new Negation(new Conjunction(b, False.INSTANCE))),
                new Conjunction(new Negation(True.INSTANCE), new Conjunction(a, c)));
        
        VariableFinder finder = new VariableFinder();
        finder.visit(f);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A", "B", "C")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a, b, c))));
    }
    
    /**
     * Tests the clear method.
     */
    @Test
    public void testClear() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        
        VariableFinder finder = new VariableFinder();
        finder.visit(a);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a))));
        
        finder.clear();
        finder.visit(b);

        assertThat(finder.getVariableNames(), is(Arrays.asList("B")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(b))));
        

        finder.visit(a);
        assertThat(finder.getVariableNames(), is(Arrays.asList("A", "B")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a, b))));
    }
    
}
