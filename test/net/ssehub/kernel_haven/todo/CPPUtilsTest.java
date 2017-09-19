package net.ssehub.kernel_haven.todo;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link CPPUtils}.
 * @author El-Sharkawy
 *
 */
public class CPPUtilsTest {
    
    /**
     * Tests the {@link CPPUtils#isIfOrElifStatement(String)} method.
     */
    @Test
    public void testIsIfOrElifStatement() {
        String[] validIfs = {"#if (Var > Something)", "#if(Var > Something)", "#elif (Var > Something)",
            "#elif(Var > Something)", "#if (Var > Something) \\"};
        String[] inValidIfs = {"#ifdef Var", "#ifndef Var", "#ifdef(Var)", "#ifndef(Var)", ""};
        
        // Check desired statements
        for (String validIf : validIfs) {
            Assert.assertTrue(validIf + " was not detected as valid #if.", CPPUtils.isIfOrElifStatement(validIf));
        }
        
        // Check undesired statements
        for (String invalidIf : inValidIfs) {
            Assert.assertFalse(invalidIf + " was detected as valid #if, but should not!",
                CPPUtils.isIfOrElifStatement(invalidIf));
        }
    }

}
