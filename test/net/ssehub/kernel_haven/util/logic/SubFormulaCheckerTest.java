package net.ssehub.kernel_haven.util.logic;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.parser.SubFormulaChecker;

/**
 * Tests the {@link SubFormulaChecker}.
 * @author El-Sharkawy
 *
 */
@SuppressWarnings("null")
public class SubFormulaCheckerTest {
    
    private static final Formula VARIABLE_A = new Variable("A");
    private static final Formula VARIABLE_B = new Variable("B");
    private static final Formula SUB_FORMULA = new Conjunction(VARIABLE_A, VARIABLE_B);
    
    
    /**
     * Tests that elements contained in the sub formula are not marked as containing elements.
     */
    @Test
    public void testSubsubelementsNotContained() {
        assertContainment(VARIABLE_A, false);
    }
    
    /**
     * Tests that a formula contains itself.
     */
    @Test
    public void testSelfContained() {
        assertContainment(SUB_FORMULA, true);
    }
    
    /**
     * Tests if {@link SubFormulaChecker} detects a sub formula correctly in a more complex formula.
     */
    @Test
    public void testContainedDetection() {
        Formula formula = new Disjunction(new Variable("C"), new Negation(SUB_FORMULA));
        Formula secondFormula = new Conjunction(new Variable("E"), new Variable("F"));
        formula = new Conjunction(secondFormula, formula);
        
        assertContainment(formula, true);
    }
    
    /**
     * Tests that a very similar with a different semantics does not contain the sub formula.
     */
    @Test
    public void testSameVariablesWithDifferentSemanticsDoesNotContain() {
        Formula formula = new Conjunction(VARIABLE_A, new Negation(VARIABLE_B));
        
        assertContainment(formula, false);
    }
    

    /**
     * Checks if {@link #SUB_FORMULA} is contained in the given formula.
     * @param formula The containing formula to test.
     * @param containsSubformula <tt>true</tt> {@link #SUB_FORMULA} is nested and should be found,
     * <tt>false</tt> {@link #SUB_FORMULA} is not contained and must not be found.
     */
    private void assertContainment(Formula formula, boolean containsSubformula) {
        SubFormulaChecker checker = new SubFormulaChecker(SUB_FORMULA);
        formula.accept(checker);
        String strFormula = formula.toString().replace("&&", "\u2227").replace("||", "\u2228");
        String strSubFormula = SUB_FORMULA.toString().replace("&&", "\u2227").replace("||", "\u2228");
        
        if (containsSubformula) {
            Assert.assertTrue("\"" + strFormula + "\" contains \"" + strSubFormula + "\","
                + " but the visitor did not recognized that.", checker.isNested());
        } else {
            Assert.assertFalse("\"" + strFormula + "\" contains not \"" + strSubFormula + "\","
                + " but the visitor reported that.", checker.isNested());
        }
    }

}
