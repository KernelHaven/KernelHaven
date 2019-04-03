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
