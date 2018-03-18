package net.ssehub.kernel_haven.util.cpp.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.kernel_haven.util.cpp.parser.ast.CppExpression;
import net.ssehub.kernel_haven.util.cpp.parser.ast.FunctionCall;
import net.ssehub.kernel_haven.util.cpp.parser.ast.IntegerLiteral;
import net.ssehub.kernel_haven.util.cpp.parser.ast.Operator;
import net.ssehub.kernel_haven.util.cpp.parser.ast.Variable;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * Tests the {@link CppParser}.
 * 
 * @author Adam
 */
public class CppParserTest {

    /**
     * Tests that an empty expression leads to an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testParseEmpty() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("");
    }
    
    /**
     * Tests parsing a single variable.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSingleVariable() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("Variable");
        assertVariable(result, "Variable");
    }
    
    /**
     * Tests parsing a single variable with brackets around it.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSingleVariableWithBrackets() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("(Variable)");
        assertVariable(result, "Variable");
    }
    
    /**
     * Tests that too many closing brackets correctly throw an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testTooManyClosingBrackets() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("((A)))");
    }
    
    /**
     * Tests that too few closing brackets correctly throw an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testTooFeqClosingBrackets() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("((A)");
    }
    
    /**
     * Tests that operators on the same nesting depth are ordered according to their precedence.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testOperatorPrecedence() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("A * B + C");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_ADD);
        assertVariable(op1[1], "C");
        
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.INT_MUL);
        assertVariable(op2[0], "A");
        assertVariable(op2[1], "B");
    }
    
    /**
     * Tests that same-level precedence is evaluated left-to-right.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSameOperatorPrecedence() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("A - B + C");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_ADD);
        assertVariable(op1[1], "C");
        
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.INT_SUB);
        assertVariable(op2[0], "A");
        assertVariable(op2[1], "B");
    }
    
    /**
     * Tests that brackets override precedence.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testBracketsOverridingOperatorPrecedence() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("A * (B + C)");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_MUL);
        assertVariable(op1[0], "A");
        
        CppExpression[] op2 = assertOperator(op1[1], CppOperator.INT_ADD);
        assertVariable(op2[0], "B");
        assertVariable(op2[1], "C");
    }
    
    /**
     * Tests that a missing operator correctly throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testMissingOperator() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("A B");
    }
    
    /**
     * Tests that an unary operator with expressions on both sides correctly throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testUnaryWithBothSides() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("A ! B");
    }
    
    /**
     * Tests that an unary operator with expressions on the wrong side correctly throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testUnaryWithWrongSide() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("A!");
    }
    
    /**
     * Tests that unary ++ and -- can be also on the right side.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testUnaryOnRight() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("A++");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_INC);
        assertVariable(op1[0], "A");
        assertThat(op1[1], nullValue());
        
        result = parser.parse("A--");
        
        op1 = assertOperator(result, CppOperator.INT_DEC);
        assertVariable(op1[0], "A");
        assertThat(op1[1], nullValue());
    }
    
    /**
     * Tests that a binary operator without expressions on both sides correctly throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testBinaryWithNotBothSides() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        parser.parse("B *");
    }
    
    /**
     * Tests that a function call is detected correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testFunctionCall() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("defined(A)");
        
        CppExpression arg = assertFunctionCall(result, "defined");
        assertVariable(arg, "A");
    }
    
    /**
     * Tests that a function call with a space after the function name is detected correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testFunctionCallWithSpace() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("defined (A)");
        
        CppExpression arg = assertFunctionCall(result, "defined");
        assertVariable(arg, "A");
    }
    
    /**
     * Tests that a defined() call without brackets is handled, too.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testDefinedWithoutBrackets() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("defined A");
        
        CppExpression arg = assertFunctionCall(result, "defined");
        assertVariable(arg, "A");
    }
    
    /**
     * Tests that a defined() call without brackets and an invalid parameter throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testDefinedWithoutBracketsInvalidParameter() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        parser.parse("defined ! A");
    }
    
    /**
     * Tests that a function call without parameters is detected correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testFunctionCallWithNoParameters() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("func()");
        
        CppExpression arg = assertFunctionCall(result, "func");
        assertThat(arg, nullValue());
    }
    
    /**
     * Tests that a function call with more than 1 parameter correctly throws an exception.
     * 
     * @throws ExpressionFormatException wanted.
     */
    @Test(expected = ExpressionFormatException.class)
    public void testFunctionCallWithMoreParameters() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        parser.parse("func(a, b)");
    }
    
    /**
     * Tests that operators that can be both, unary and binary, are detected as unary correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testUnaryAtStart() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("-A");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_SUB_UNARY);
        assertVariable(op1[0], "A");
        assertThat(op1[1], nullValue());
    }
    
    /**
     * Tests that operators that can be both, unary and binary, are detected as unary correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testNotUnaryAfterClosingBracket() throws ExpressionFormatException {
        CppParser parser = new CppParser();
        
        CppExpression result = parser.parse("(A + C) - B");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_SUB);
        CppExpression[] op2 = assertOperator(op1[0], CppOperator.INT_ADD);
        assertVariable(op1[1], "B");
        
        assertVariable(op2[0], "A");
        assertVariable(op2[1], "C");
    }
    
    /**
     * Tests that operators that can be both, unary and binary, are detected as unary correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testUnaryAfterOpeningBracket() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("B * (-A)");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_MUL);
        assertVariable(op1[0], "B");
        CppExpression[] op2 = assertOperator(op1[1], CppOperator.INT_SUB_UNARY);
        
        assertVariable(op2[0], "A");
        assertThat(op2[1], nullValue());
    }
    
    /**
     * Tests that operators that can be both, unary and binary, are detected as unary correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testUnaryAfterOperator() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("B * -A");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_MUL);
        assertVariable(op1[0], "B");
        CppExpression[] op2 = assertOperator(op1[1], CppOperator.INT_SUB_UNARY);
        
        assertVariable(op2[0], "A");
        assertThat(op2[1], nullValue());
    }
    
    /**
     * Tests the unary + operator.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testUnaryPlus() throws ExpressionFormatException {
        CppParser parser = new CppParser();

        CppExpression result = parser.parse("+A");
        
        CppExpression[] op1 = assertOperator(result, CppOperator.INT_ADD_UNARY);
        assertVariable(op1[0], "A");
        assertThat(op1[1], nullValue());
    }
    
    /**
     * Asserts that the given expression is a {@link Variable}.
     * 
     * @param expression The expression that should be a {@link Variable}.
     * @param name The expected name of the variable.
     */
    @SuppressWarnings("null")
    public static void assertVariable(CppExpression expression, String  name) {
        assertThat(expression, instanceOf(Variable.class));
        Variable var = (Variable) expression;
        assertThat(var.getName(), is(name));
    }
    
    /**
     * Asserts that the given expression is an {@link Operator}.
     * 
     * @param expression The expression that should be an {@link Operator}.
     * @param operator The expected operator.
     * 
     * @return A 2-element array containing the parameters. For unary operators, the second element is
     *      <code>null</code>.
     */
    @SuppressWarnings("null")
    public static CppExpression[] assertOperator(CppExpression expression, CppOperator operator) {
        assertThat(expression, instanceOf(Operator.class));
        Operator op = (Operator) expression;
        
        assertThat(op.getOperator(), is(operator));
        
        if (operator.isUnary()) {
            assertThat(op.getRightSide(), nullValue());
        }
        
        return new CppExpression[] {op.getLeftSide(), op.getRightSide()};
    }
    
    /**
     * Asserts that the given expression is an {@link FunctionCall}.
     * 
     * @param expression The expression that should be an {@link FunctionCall}.
     * @param functionName The expected name of the called function.
     * 
     * @return The parameter of the function, may be <code>null</code>.
     */
    @SuppressWarnings("null")
    public static CppExpression assertFunctionCall(CppExpression expression, String functionName) {
        assertThat(expression, instanceOf(FunctionCall.class));
        FunctionCall func = (FunctionCall) expression;
        
        assertThat(func.getFunctionName(), is(functionName));
        
        return func.getArgument();
    }
    
    /**
     * Asserts that the given expression is an {@link IntegerLiteral}.
     * 
     * @param expression The expression that should be an {@link IntegerLiteral}.
     * @param value The expected literal value.
     */
    public static void assertLiteral(CppExpression expression, long value) {
        assertThat(expression, instanceOf(IntegerLiteral.class));
        IntegerLiteral literal = (IntegerLiteral) expression;
        
        assertThat(literal.getValue(), is(value));
    }
    
}
