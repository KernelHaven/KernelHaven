/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;

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
    private static final Formula SUB_FORMULA = and(VARIABLE_A, VARIABLE_B);
    
    
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
        assertContainment(and(and("E", "F"), or("C", not(SUB_FORMULA))), true);
    }
    
    /**
     * Tests that a very similar with a different semantics does not contain the sub formula.
     */
    @Test
    public void testSameVariablesWithDifferentSemanticsDoesNotContain() {
        assertContainment(and("A", not("B")), false);
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
