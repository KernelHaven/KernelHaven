package net.ssehub.kernel_haven.util.cpp;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link NumberUtils}.
 * @author El-Sharkawy
 *
 */
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

}
