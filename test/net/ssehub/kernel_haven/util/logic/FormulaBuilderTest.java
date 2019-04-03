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

import org.junit.Test;

/**
 * Tests the {@link FormulaBuilder} methods.
 *
 * @author Adam
 */
public class FormulaBuilderTest {

    /**
     * Tests the utility methods for creating conjunctions.
     */
    @Test
    public void testConjunctions() {
        assertThat(and(True.INSTANCE, False.INSTANCE),
                is(new Conjunction(True.INSTANCE, False.INSTANCE)));
        
        assertThat(and("A", False.INSTANCE),
                is(new Conjunction(new Variable("A"), False.INSTANCE)));
        
        assertThat(and(True.INSTANCE, "B"),
                is(new Conjunction(True.INSTANCE, new Variable("B"))));
        
        assertThat(and("A", "B"),
                is(new Conjunction(new Variable("A"), new Variable("B"))));
    }
    
    /**
     * Tests the utility methods for creating disjunctions.
     */
    @Test
    public void testDisunctions() {
        assertThat(or(True.INSTANCE, False.INSTANCE),
                is(new Disjunction(True.INSTANCE, False.INSTANCE)));
        
        assertThat(or("A", False.INSTANCE),
                is(new Disjunction(new Variable("A"), False.INSTANCE)));
        
        assertThat(or(True.INSTANCE, "B"),
                is(new Disjunction(True.INSTANCE, new Variable("B"))));
        
        assertThat(or("A", "B"),
                is(new Disjunction(new Variable("A"), new Variable("B"))));
    }
    
    /**
     * Tests the utility methods for creating negations.
     */
    @Test
    public void testNegations() {
        assertThat(not(True.INSTANCE),
                is(new Negation(True.INSTANCE)));
        
        assertThat(not("A"),
                is(new Negation(new Variable("A"))));
    }
    
    /**
     * Test all methods combined.
     */
    @Test
    public void testCombined() {
        assertThat(or("A", and(not("B"), "C")),
                is(new Disjunction(new Variable("A"),
                        new Conjunction(new Negation(new Variable("B")), new Variable("C")))));
    }
    
}
