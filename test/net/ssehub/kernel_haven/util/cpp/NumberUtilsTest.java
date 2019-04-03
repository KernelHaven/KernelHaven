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
package net.ssehub.kernel_haven.util.cpp;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link NumberUtils}.
 * 
 * @author El-Sharkawy
 * @author Adam
 */
@SuppressWarnings("null")
public class NumberUtilsTest {

    /**
     * Tests correct parsing of integers.
     */
    @Test
    public void testParsingIntegers() {
        String[] validIntegers = {"10", "100.0", "0x3e8", "0x44C", "0xFF", String.valueOf(Integer.MAX_VALUE) + "0"};
        long[] expectedValues = {10, 100, 1000, 1100, 255, Integer.MAX_VALUE * 10L };
        
        for (int i = 0; i < validIntegers.length; i++) {
            Number parseResult = NumberUtils.convertToNumber(validIntegers[i]);
            
            Assert.assertNotNull("Parsing of " + validIntegers[i] + " failed.", parseResult);
            assertThat(parseResult, instanceOf(Long.class));
            Assert.assertEquals(expectedValues[i], parseResult);
        }
    }
    
    /**
     * Tests correct parsing of doubles.
     */
    @Test
    public void testParsingDoubles() {
        String[] validDoubles = {"10.2", "-100.1"};
        double[] expectedValues = {10.2, -100.1};
        
        for (int i = 0; i < validDoubles.length; i++) {
            Number parseResult = NumberUtils.convertToNumber(validDoubles[i]);
            
            Assert.assertNotNull("Parsing of " + validDoubles[i] + " failed.", parseResult);
            assertThat(parseResult, instanceOf(Double.class));
            Assert.assertEquals(expectedValues[i], parseResult);
        }
    }
    
    /**
     * Tests parsing invalid numbers.
     */
    @Test
    public void testParsingInvalid() {
        String[] invalids = {"abc", "12-32", "434.343.3", "0xg", "0xFFFFFFFFFFFFFFFF", "999999999999999999999999"};
        
        for (String invalid : invalids) {
            Number parseResult = NumberUtils.convertToNumber(invalid);
            Assert.assertNull("Parsing of " + parseResult + " should fail", parseResult);
        }
    }
    
    /**
     * Tests that isNumber() correctly detects numbers.
     */
    @Test
    public void testIsNumber() {
        String[] numbers = {"1", "-1", "524", "12345678901"};
        
        for (String number : numbers) {
            assertThat(number + " should be detected as a number", NumberUtils.isInteger(number, 10), is(true));
        }
    }

    /**
     * Tests that isNumber() correctly detects strings that are not numbers.
     */
    @Test
    public void testIsntNumber() {
        String[] nonNumbers = {"", "-", "5 24", "1234567890A1"};
        
        for (String number : nonNumbers) {
            assertThat(number + " shouldn't be detected as a number", NumberUtils.isInteger(number, 10), is(false));
        }
    }
    
}
