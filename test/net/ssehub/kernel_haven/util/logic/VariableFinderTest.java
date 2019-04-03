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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * Tests the {@link VariableFinder} class.
 * 
 * @author Adam
 */
@SuppressWarnings("null")
public class VariableFinderTest {

    /**
     * Tests a simple variable.
     */
    @Test
    public void testSimple() {
        Variable a = new Variable("A");
        
        VariableFinder finder = new VariableFinder();
        finder.visit(a);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a))));
    }
    
    /**
     * Tests a complex formula.
     */
    @Test
    public void tesComplex() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Variable c = new Variable("C");
        
        Formula f = and(or("A", not(and("B", False.INSTANCE))), and(not(True.INSTANCE), and("A", "C")));
        
        VariableFinder finder = new VariableFinder();
        finder.visit(f);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A", "B", "C")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a, b, c))));
    }
    
    /**
     * Tests the clear method.
     */
    @Test
    public void testClear() {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        
        VariableFinder finder = new VariableFinder();
        finder.visit(a);
        
        assertThat(finder.getVariableNames(), is(Arrays.asList("A")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a))));
        
        finder.clear();
        finder.visit(b);

        assertThat(finder.getVariableNames(), is(Arrays.asList("B")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(b))));
        

        finder.visit(a);
        assertThat(finder.getVariableNames(), is(Arrays.asList("A", "B")));
        assertThat(finder.getVariables(), is(new HashSet<>(Arrays.asList(a, b))));
    }
    
}
