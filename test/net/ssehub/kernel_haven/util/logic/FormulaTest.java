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
                new Disjunction(new Variable("A"), new Negation(True.INSTANCE)),
                new Conjunction(new Negation(new Variable("B")), False.INSTANCE)
        );
        
        
        Assert.assertEquals("(A || !1) && !B && 0", f.toString());
    }
    
    /**
     * Tests whether equals() works.
     */
    @Test
    public void testEqual() {
        Formula f1 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(True.INSTANCE)),
                new Conjunction(new Negation(new Variable("B")), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(new Object()));
        
        Formula f2 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(True.INSTANCE)),
                new Conjunction(new Negation(new Variable("B")), False.INSTANCE)
        );
        Assert.assertTrue(f1.equals(f2));
        
        Formula f3 = new Conjunction(
                new Disjunction(new Variable("A"), new Negation(True.INSTANCE)),
                new Conjunction(new Negation(new Variable("C")), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(f3));
        
        Formula f4 = new Conjunction(
                new Disjunction(new Variable("A"), False.INSTANCE),
                new Conjunction(new Negation(new Variable("B")), False.INSTANCE)
                );
        Assert.assertFalse(f1.equals(f4));
        
        Formula f5 = new Conjunction(
                new Conjunction(new Variable("A"), False.INSTANCE),
                new Conjunction(new Negation(new Variable("B")), False.INSTANCE)
                );
        Assert.assertFalse(f1.equals(f5));
    }

}
