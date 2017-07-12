package net.ssehub.kernel_haven.util.logic;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the formula structure.
 * 
 * @author Adam (from KernelMiner project)
 */
public class FormulaTest {

    /**
     * Tests whether toString() works on complex formulas.
     */
    @Test
    public void testToStringComplex() {
        Formula f = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(new True())),
                new Conjunction(new Negation(new Variable("B")), new False())
        );
        
        
        Assert.assertEquals("(A || !1) && !B && 0", f.toString());
    }
    
    /**
     * Tests whether evaluate() works on a single variable.
     */
    @Test
    public void testEvaluateSimpleVariable() {
        Variable a = new Variable("A");
        
        a.setValue(true);
        Assert.assertTrue(a.evaluate());
        
        a.setValue(false);
        Assert.assertFalse(a.evaluate());
    }
    
    /**
     * Tests whether evaluate() works on constants.
     */
    @Test
    public void testEvaluateSimpleConstants() {
        Assert.assertTrue(new True().evaluate());
        Assert.assertFalse(new False().evaluate());
    }
    
    /**
     * Tests whether evaluate() works on negations.
     */
    @Test
    public void testEvaluateSimpleNegation() {
        Variable a = new Variable("A");
        Formula f = new Negation(a);
        
        a.setValue(true);
        Assert.assertFalse(f.evaluate());
        
        a.setValue(false);
        Assert.assertTrue(f.evaluate());
    }
    
    /**
     * Tests whether evaluate() works on conjunctions.
     */
    @Test
    public void testEvaluateSimpleConjunction() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Formula f = new Conjunction(a, b);
        
        a.setValue(true);
        b.setValue(true);
        Assert.assertTrue(f.evaluate());
        
        a.setValue(true);
        b.setValue(false);
        Assert.assertFalse(f.evaluate());
        
        a.setValue(false);
        b.setValue(true);
        Assert.assertFalse(f.evaluate());
        
        a.setValue(false);
        b.setValue(false);
        Assert.assertFalse(f.evaluate());
    }
    
    /**
     * Tests whether evaluate() works on disjunctions.
     */
    @Test
    public void testEvaluateSimpleDisjunction() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Formula f = new Disjunction(a, b);
        
        a.setValue(true);
        b.setValue(true);
        Assert.assertTrue(f.evaluate());
        
        a.setValue(true);
        b.setValue(false);
        Assert.assertTrue(f.evaluate());
        
        a.setValue(false);
        b.setValue(true);
        Assert.assertTrue(f.evaluate());
        
        a.setValue(false);
        b.setValue(false);
        Assert.assertFalse(f.evaluate());
    }
    
    /**
     * Tests whether evaluate() works on complex formulas.
     */
    @Test
    public void testEvaluateComplex() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Formula f = new Conjunction(
                new Disjunction(a, new Negation(new True())),
                new Negation(b)
        );
        
        a.setValue(true);
        b.setValue(true);
        Assert.assertFalse(f.evaluate());
        
        a.setValue(true);
        b.setValue(false);
        Assert.assertTrue(f.evaluate());
        
        a.setValue(false);
        b.setValue(true);
        Assert.assertFalse(f.evaluate());
        
        a.setValue(false);
        b.setValue(false);
        Assert.assertFalse(f.evaluate());
    }
    
    /**
     * Tests whether equals() works.
     */
    @Test
    public void testEqual() {
        Formula f1 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(new True())),
                new Conjunction(new Negation(new Variable("B")), new False())
        );
        Assert.assertFalse(f1.equals(new Object()));
        
        Formula f2 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(new True())),
                new Conjunction(new Negation(new Variable("B")), new False())
        );
        Assert.assertTrue(f1.equals(f2));
        
        Formula f3 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(new True())),
                new Conjunction(new Negation(new Variable("C")), new False())
        );
        Assert.assertFalse(f1.equals(f3));
        
        Formula f4 = new Conjunction(
                new Disjunction(new Variable("A"), new False()),
                new Conjunction(new Negation(new Variable("B")), new False())
                );
        Assert.assertFalse(f1.equals(f4));
        
        Formula f5 = new Conjunction(
                new Conjunction(new Variable("A"), new False()),
                new Conjunction(new Negation(new Variable("B")), new False())
                );
        Assert.assertFalse(f1.equals(f5));
    }

}
