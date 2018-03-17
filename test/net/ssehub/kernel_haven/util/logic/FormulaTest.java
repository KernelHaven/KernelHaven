package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;

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
        Formula f = and(
                or("A", not(True.INSTANCE)),
                and(not("B"), False.INSTANCE)
        );
        
        
        Assert.assertEquals("(A || !1) && !B && 0", f.toString());
    }
    
    /**
     * Tests whether equals() works.
     */
    @Test
    public void testEqual() {
        Formula f1 = and(
                or("A", not(True.INSTANCE)),
                and(not("B"), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(new Object()));
        
        Formula f2 = and(
                or("A", not(True.INSTANCE)),
                and(not("B"), False.INSTANCE)
        );
        Assert.assertTrue(f1.equals(f2));
        
        Formula f3 = and(
                or("A", not(True.INSTANCE)),
                and(not("C"), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(f3));
        
        Formula f4 = and(
                or("A", False.INSTANCE),
                and(not("B"), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(f4));
        
        Formula f5 = and(
                and("A", False.INSTANCE),
                and(not("B"), False.INSTANCE)
        );
        Assert.assertFalse(f1.equals(f5));
    }

}
