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

import static org.junit.Assert.assertThat;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.*;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.util.null_checks.NonNull;


/**
 * Tests for FormulaLiteralCounter.
 * @author bargfeldt
 *
 */
@RunWith(Parameterized.class)
public class FormulaLiteralCounterTest {

    private @NonNull Formula input;
    private int expected;
    
    /**
     * Constructor for FormulaLiteralCounterTest.
     * @param input Test input
     * @param expected Expected output
     */
    public FormulaLiteralCounterTest(@NonNull Formula input, int expected) {
        this.input = input;
        this.expected = expected;
    }
    
    
    /**
     * Test parameters.
     * @return Collection of parameters
     */
    @Parameters(name = "{0} has {1} literals")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
            {not("A"), 1},
            {and("A", "B"), 2},
            {or("A", not("B")), 2},
            {new Variable("A"), 1},

            {True.INSTANCE, 0},
            {False.INSTANCE, 0},
            {not(True.INSTANCE), 0},
            
            {and("A", "A"), 2},
            {or(and("A", not("B")), not(or(True.INSTANCE, and("C", "B")))), 4},
        });
    }
    
    
    /**
     * Run the tests.
     */
    @Test
    public void test() {
        FormulaLiteralCounter counter = new FormulaLiteralCounter();
        int output = counter.visit(input);
        assertThat(output, is(expected));
    }

}
