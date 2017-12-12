package net.ssehub.kernel_haven.util;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Disjunction;
import net.ssehub.kernel_haven.util.logic.False;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the {@link FormulaCache}.
 * 
 * @author Adam
 */
public class FormulaCacheTest {

    /**
     * Tests a single call to getSerializedFormula().
     */
    @Test
    public void testGetSerializedFormulaSingle() {
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        
        Formula f = new Conjunction(new Disjunction(a, b), new Disjunction(new Negation(a), False.INSTANCE));
        
        FormulaCache cache = new FormulaCache();
        
        assertThat(cache.getSerializedFormula(f), CoreMatchers.is(f.toString()));
    }
    
    /**
     * Tests a two calls to getSerializedFormula() with the same reference.
     */
    @Test
    public void testGetSerializedFormulaSameFormula() {
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        
        Formula f = new Conjunction(new Disjunction(a, b), new Disjunction(new Negation(a), False.INSTANCE));
        
        FormulaCache cache = new FormulaCache();
        
        String expected = f.toString();
        assertThat(cache.getSerializedFormula(f), CoreMatchers.is(expected));
        assertThat(cache.getSerializedFormula(f), CoreMatchers.is(expected));
    }
    
    /**
     * Tests a two calls to getSerializedFormula() with equal formulas (not same reference).
     */
    @Test
    public void testGetSerializedFormulaEqualFormula() {
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        
        Formula f1 = new Conjunction(new Disjunction(a, b), new Disjunction(new Negation(a), False.INSTANCE));
        Formula f2 = new Conjunction(new Disjunction(a, b), new Disjunction(new Negation(a), False.INSTANCE));
        
        FormulaCache cache = new FormulaCache();
        
        String expected = f1.toString();
        assertThat(cache.getSerializedFormula(f1), CoreMatchers.is(expected));
        assertThat(cache.getSerializedFormula(f2), CoreMatchers.is(expected));
    }
    
}
