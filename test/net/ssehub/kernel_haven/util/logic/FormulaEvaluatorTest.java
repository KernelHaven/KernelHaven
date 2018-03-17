package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests the {@link FormulaEvaluator}.
 * 
 * @author Adam
 */
@SuppressWarnings("null")
public class FormulaEvaluatorTest {

    /**
     * Tests whether single variables are evaluated correctly..
     */
    @Test
    public void testEvaluateSimpleVariable() {
        Map<String, Boolean> values = new HashMap<>();
        
        Variable a = new Variable("A");
        
        assertNull(new FormulaEvaluator(values).visit(a));
        
        values.put("A", true);
        assertTrue(new FormulaEvaluator(values).visit(a));
        
        values.put("A", false);
        assertFalse(new FormulaEvaluator(values).visit(a));
        
        values.remove("A");
        values.put("B", true);
        assertNull(new FormulaEvaluator(values).visit(a));
    }
    
    /**
     * Tests whether constants are evaluated correctly.
     */
    @Test
    public void testEvaluateSimpleConstants() {
        Map<String, Boolean> values = new HashMap<>();

        assertTrue(new FormulaEvaluator(values).visit(True.INSTANCE));
        assertFalse(new FormulaEvaluator(values).visit(False.INSTANCE));
    }
    
    /**
     * Tests whether negations are evaluated correctly.
     */
    @Test
    public void testEvaluateSimpleNegation() {
        Map<String, Boolean> values = new HashMap<>();
        
        Formula f = not("A");
        
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        assertFalse(new FormulaEvaluator(values).visit(f));
    }
    
    /**
     * Tests whether conjunctions are evaluated correctly.
     */
    @Test
    public void testEvaluateSimpleConjunction() {
        Map<String, Boolean> values = new HashMap<>();
        
        Formula f = and("A", "B");
        
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", true);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", false);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", false);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", true);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", null);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", false);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", null);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", null);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", true);
        assertNull(new FormulaEvaluator(values).visit(f));
    }
    
    /**
     * Tests whether disjunctions are evaluated correctly.
     */
    @Test
    public void testEvaluateSimpleDisjunction() {
        Map<String, Boolean> values = new HashMap<>();
        
        Formula f = or("A", "B");
        
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", true);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", false);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", false);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", true);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", null);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", false);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", null);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", null);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", true);
        assertTrue(new FormulaEvaluator(values).visit(f));
    }
    
    /**
     * Tests whether complex formulas are evaluated correctly.
     */
    @Test
    public void testEvaluateComplex() {
        Map<String, Boolean> values = new HashMap<>();
        
        Formula f = and(or("A", not(True.INSTANCE)), not("B"));
        
        values.put("A", true);
        values.put("B", true);
        assertFalse(new FormulaEvaluator(values).visit(f));

        values.put("A", true);
        values.put("B", false);
        assertTrue(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", true);
        assertFalse(new FormulaEvaluator(values).visit(f));

        values.put("A", false);
        values.put("B", false);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", null);
        values.put("B", true);
        assertFalse(new FormulaEvaluator(values).visit(f));
        
        values.put("A", true);
        values.put("B", null);
        assertNull(new FormulaEvaluator(values).visit(f));
        
        values.put("A", false);
        values.put("B", null);
        assertFalse(new FormulaEvaluator(values).visit(f));
    }
    
}
