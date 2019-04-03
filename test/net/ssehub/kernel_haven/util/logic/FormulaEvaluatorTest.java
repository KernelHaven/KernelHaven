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
