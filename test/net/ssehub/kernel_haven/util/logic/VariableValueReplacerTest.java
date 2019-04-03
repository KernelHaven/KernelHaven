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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests for VariableValueReplacer.
 * 
 * @author bargfeldt
 *
 */
@RunWith(Parameterized.class)
public class VariableValueReplacerTest {

    private @NonNull Formula formula;
    private @NonNull String variable;
    private boolean value;
    private boolean exactMatch;
    
    private @NonNull Formula expected;
    
    
    /**
     * Constructor for VariableValueReplacerTest.
     * 
     * @param formula Input formula
     * @param variable Input variable
     * @param value Input value
     * @param exactMatch Input exactMatch
     * @param expected Expected output
     */
    public VariableValueReplacerTest(@NonNull Formula formula, @NonNull String variable, boolean value,
        boolean exactMatch, @NonNull Formula expected) {
        this.formula = formula;
        this.variable = variable;
        this.value = value;
        this.exactMatch = exactMatch;
        this.expected = expected;
    }
    
    
    /**
     * Test parameters.
     * 
     * @return Collection of parameters
     */
    @Parameters(name = "{0} -> {4}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
            {True.INSTANCE, "A", false, true, True.INSTANCE},
            {False.INSTANCE, "A", true, true, False.INSTANCE},
            
            {new Variable("A"), "A", false, true, False.INSTANCE},
            {new Variable("A"), "A", true, true, True.INSTANCE},
            {new Variable("X"), "A", true, true, new Variable("X")},
            {new Variable("Foo"), "F", false, false, False.INSTANCE},
            {new Variable("Foo"), "X", false, false, new Variable("Foo")},
            
            {not(new Variable("Foo")), "F", false, false, not(False.INSTANCE)},
            {not(not(new Variable("Bar"))), "Bar", true, true, not(not(True.INSTANCE))},
            
            {or("Foo", False.INSTANCE), "Foo", true, true, or(True.INSTANCE, False.INSTANCE)},
            {or(True.INSTANCE, "Bar"), "B", false, false, or(True.INSTANCE, False.INSTANCE)},
            {or(True.INSTANCE, "Bar"), "B", false, true, or(True.INSTANCE, "Bar")},
            
            {and("Foo", False.INSTANCE), "Foo", true, true, and(True.INSTANCE, False.INSTANCE)},
            {and(True.INSTANCE, "Bar"), "B", false, false, and(True.INSTANCE, False.INSTANCE)},
            {and(True.INSTANCE, "Bar"), "B", false, true, and(True.INSTANCE, "Bar")},
            
            {or(and("A", not("B")), not(or(True.INSTANCE, and("C", "B")))), "B", false, true,
                or(and("A", not(False.INSTANCE)), not(or(True.INSTANCE, and("C", False.INSTANCE))))},
        });
    }
    
    /**
     * Run the tests.
     */
    @Test
    public void test() {
        assertThat(new VariableValueReplacer(variable, value, exactMatch).visit(formula), is(expected));
    }

}
