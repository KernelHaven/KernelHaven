package net.ssehub.kernel_haven.util.logic;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;

/**
 * Tests the parser with the {@link CStyleBooleanGrammar}.
 * 
 * @author Adam (from KernelMiner project)
 */
public class ParserTest {

    /**
     * Tests parsing a single variable.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleVariable() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "A";
        
        Formula f = parser.parse(str);
        
        assertVariable(f, "A");
        Assert.assertEquals(1, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of negations.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleNegation() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "!A";
        
        Formula f = parser.parse(str);
        
        assertVariable(assertNegation(f), "A");
        Assert.assertEquals(1, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of conjunctions.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleConjunction() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "A && B";
        
        Formula f = parser.parse(str);
        
        Formula[] t = assertConjunction(f);
        assertVariable(t[0], "A");
        assertVariable(t[1], "B");
        Assert.assertEquals(2, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of disjunctions.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleDisjunction() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "A||B";
        
        Formula f = parser.parse(str);
        
        Formula[] t = assertDisjunction(f);
        assertVariable(t[0], "A");
        assertVariable(t[1], "B");
        Assert.assertEquals(2, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of two operators considering precedence.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testPrecedence() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "!A && B";
        
        Formula f = parser.parse(str);
        
        Formula[] t = assertConjunction(f);
        assertVariable(assertNegation(t[0]), "A");
        assertVariable(t[1], "B");
        Assert.assertEquals(2, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of paranthesis.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleParenthesis1() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "A || (!B && C)";
        
        Formula f = parser.parse(str);
        
        Formula[] t = assertDisjunction(f);
        assertVariable(t[0], "A");
        Formula[] t2 = assertConjunction(t[1]);
        assertVariable(assertNegation(t2[0]), "B");
        assertVariable(t2[1], "C");
        Assert.assertEquals(3, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of paranthesis.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testSimpleParenthesis2() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "(!(A) && ((B) || (C)))";
        
        Formula f = parser.parse(str);
        
        Formula[] t = assertConjunction(f);
        assertVariable(assertNegation(t[0]), "A");
        Formula[] t2 = assertDisjunction(t[1]);
        assertVariable(t2[0], "B");
        assertVariable(t2[1], "C");
        Assert.assertEquals(3, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of complex formula.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testComplex1() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "(A && B && (!A || B))";
        
        Formula f = parser.parse(str);
        
        Formula[] t1 = assertConjunction(f);
        assertVariable(t1[0], "A");
        Formula[] t2 = assertConjunction(t1[1]);
        assertVariable(t2[0], "B");
        Formula[] t3 = assertDisjunction(t2[1]);
        assertVariable(assertNegation(t3[0]), "A");
        assertVariable(t3[1], "B");
        Assert.assertEquals(2, cache.getNumVariables());
    }
    
    /**
     * Tests parsing of malformed brackets.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testMalformedBrackets() {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        
        try {
            parser.parse("((A)");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("(A))");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
    }
    
    /**
     * Tests parsing of malformed identifiers.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testMalformedMissingIdentifier() {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        
        try {
            parser.parse("");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("()");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("||");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("(||)");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
    }
    
    /**
     * Tests parsing of malformed identifiers.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testMalformedOperator() {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        
        try {
            parser.parse("A &&");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("A!");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("A B");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("A & B");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
        
        try {
            parser.parse("A | B");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
    }
    
    /**
     * Tests parsing of malformed characters.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testMalformedCharacter() {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        
        try {
            parser.parse("A && BÜ");
            Assert.fail("Expected exception");
        } catch (ExpressionFormatException e) {
        }
    }
    
    /**
     * Tests whether true and false (1 and 0) are parsed correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testConstants() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        

        Formula f = parser.parse("1 && 0");
        
        Formula[] a = assertConjunction(f);
        assertThat(a[0], is(True.INSTANCE));
        assertThat(a[1], is(False.INSTANCE));
    }
    
    /**
     * Tests whether additional whitespace characters (e.g. \n) are treated correctly.
     * 
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testWhitespace() throws ExpressionFormatException {
        VariableCache cache = new VariableCache();
        Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        String str = "(A      &&\n B && \r\n (!A \t\t || B))";
        
        Formula f = parser.parse(str);
        
        Formula[] t1 = assertConjunction(f);
        assertVariable(t1[0], "A");
        Formula[] t2 = assertConjunction(t1[1]);
        assertVariable(t2[0], "B");
        Formula[] t3 = assertDisjunction(t2[1]);
        assertVariable(assertNegation(t3[0]), "A");
        assertVariable(t3[1], "B");
        Assert.assertEquals(2, cache.getNumVariables());
    }
    
    /**
     * Checks if the given formula is a variable with the given name.
     * 
     * @param formula The formula that must be a variable.
     * @param expectedName The name that the variable should have.
     */
    private static void assertVariable(Formula formula, String expectedName) {
        assertTrue(formula instanceof Variable);
        assertEquals(expectedName, ((Variable) formula).getName());
    }
    
    /**
     * Checks that the given formula is a negation.
     * 
     * @param formula The formula that must be a negation.
     * @return The nested formula inside the negation.
     */
    private static Formula assertNegation(Formula formula) {
        assertTrue(formula instanceof Negation);
        return ((Negation) formula).getFormula();
    }
    
    /**
     * Checks that the given formula is a conjunction.
     * 
     * @param formula The formula that must be a conjunction.
     * @return The nested formulas.
     */
    private static Formula[] assertConjunction(Formula formula) {
        assertTrue(formula instanceof Conjunction);
        Conjunction c = (Conjunction) formula;
        return new Formula[] {c.getLeft(), c.getRight()};
    }
    
    /**
     * Checks that the given formula is a disjunction.
     * 
     * @param formula The formula that must be a disjunction.
     * @return The nested formulas.
     */
    private static Formula[] assertDisjunction(Formula formula) {
        assertTrue(formula instanceof Disjunction);
        Disjunction c = (Disjunction) formula;
        return new Formula[] {c.getLeft(), c.getRight()};
    }
    
}
