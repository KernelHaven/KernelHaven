package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.False;
import net.ssehub.kernel_haven.util.logic.Formula;

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
        Formula f = and(or("A", "B"), or(not("A"), False.INSTANCE));
        
        FormulaCache cache = new FormulaCache();
        
        assertThat(cache.getSerializedFormula(f), CoreMatchers.is(f.toString()));
    }
    
    /**
     * Tests a two calls to getSerializedFormula() with the same reference.
     */
    @Test
    public void testGetSerializedFormulaSameFormula() {
        Formula f = and(or("A", "B"), or(not("A"), False.INSTANCE));
        
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
        Formula f1 = and(or("A", "B"), or(not("A"), False.INSTANCE));
        Formula f2 = and(or("A", "B"), or(not("A"), False.INSTANCE));
        
        FormulaCache cache = new FormulaCache();
        
        String expected = f1.toString();
        assertThat(cache.getSerializedFormula(f1), CoreMatchers.is(expected));
        assertThat(cache.getSerializedFormula(f2), CoreMatchers.is(expected));
    }
    
}
