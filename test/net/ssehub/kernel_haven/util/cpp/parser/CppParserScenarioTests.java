package net.ssehub.kernel_haven.util.cpp.parser;

import static net.ssehub.kernel_haven.util.cpp.parser.CppParserTest.assertFunctionCall;
import static net.ssehub.kernel_haven.util.cpp.parser.CppParserTest.assertLiteral;
import static net.ssehub.kernel_haven.util.cpp.parser.CppParserTest.assertOperator;
import static net.ssehub.kernel_haven.util.cpp.parser.CppParserTest.assertVariable;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.kernel_haven.util.cpp.parser.ast.CppExpression;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * "Real world" test cases for the {@link CppParser}.
 * 
 * @author Adam
 */
public class CppParserScenarioTests {

    /**
     * A test case from a non-boolean example.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void nonBool1() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("((VAR & 2) > 0)");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.CMP_GT);
        assertLiteral(op1[1], 0L);
        
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.BIN_AND);
        assertVariable(op2[0], "VAR");
        assertLiteral(op2[1], 2L);
    }
    
    /**
     * A test case from a non-boolean example.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void nonBool2() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("((VAR1 == VAR2) && (VAR3 == 2))");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.BOOL_AND);
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.CMP_EQ);
        CppExpression[] op3 = assertOperator(op1[1], CppOperator.CMP_EQ);
        
        assertVariable(op2[0], "VAR1");
        assertVariable(op2[1], "VAR2");
        
        assertVariable(op3[0], "VAR3");
        assertLiteral(op3[1], 2L);
    }
    
    /**
     * A test case from the linux kernel.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void linux1() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("HZ <= MSEC_PER_SEC && !(MSEC_PER_SEC % HZ)");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.BOOL_AND);
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.CMP_LE);
        CppExpression[] op3 = assertOperator(op1[1], CppOperator.BOOL_NOT);
        CppExpression[] op4 = assertOperator(op3[0], CppOperator.INT_MOD);
        
        assertVariable(op2[0], "HZ");
        assertVariable(op2[1], "MSEC_PER_SEC");
        
        assertVariable(op4[0], "MSEC_PER_SEC");
        assertVariable(op4[1], "HZ");
    }
    
    /**
     * A test case from the linux kernel.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void linux2() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("(TICK_NSEC % (NSEC_PER_SEC / USER_HZ)) == 0");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.CMP_EQ);
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.INT_MOD);
        assertLiteral(op1[1], 0L);
        CppExpression[] op3 = assertOperator(op2[1], CppOperator.INT_DIV);
        
        assertVariable(op2[0], "TICK_NSEC");
        
        assertVariable(op3[0], "NSEC_PER_SEC");
        assertVariable(op3[1], "USER_HZ");
    }
    
    /**
     * A test case from the linux kernel.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void linux3() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("defined(CONFIG_RCU_BOOST) && !defined(CONFIG_HOTPLUG_CPU)");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.BOOL_AND);
        CppExpression arg1 = assertFunctionCall(op1[0], "defined");
        CppExpression[] op2 = assertOperator(op1[1], CppOperator.BOOL_NOT);
        
        assertVariable(arg1, "CONFIG_RCU_BOOST");
        
        CppExpression arg2 = assertFunctionCall(op2[0], "defined");
        assertThat(op2[1], nullValue());
        
        assertVariable(arg2, "CONFIG_HOTPLUG_CPU");
    }
    
}
