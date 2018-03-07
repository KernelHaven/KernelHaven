package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.logic.FormulaSimplifier.defaultSimplifier;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the default simplifier of the {@link FormulaSimplifier}.
 *
 * @author Adam
 */
public class FormulaSimplifierTest {

    private static final @NonNull Variable A = new Variable("A");
    private static final @NonNull Variable B = new Variable("B");
    private static final @NonNull Variable C = new Variable("C");
    
    /**
     * Tests that a single variable stays the same.
     */
    @Test
    public void testVariableStaysSame() {
        assertThat(defaultSimplifier(A), is(A));
    }
    
    /**
     * Tests that a simple conjunction stays the same.
     */
    @Test
    public void testSimpleConjunctionStaysSame() {
        assertThat(defaultSimplifier(new Conjunction(A, B)),
                is(new Conjunction(A, B)));
    }
    
    /**
     * Tests that a simple disjunction stays the same.
     */
    @Test
    public void testSimpleDisjunctionStaysSame() {
        assertThat(defaultSimplifier(new Disjunction(A, B)),
                is(new Disjunction(A, B)));
    }
    
    /**
     * Tests that a simple negation stays the same.
     */
    @Test
    public void testSimpleNegationStaysSame() {
        assertThat(defaultSimplifier(new Negation(A)),
                is(new Negation(A)));
    }
    
    /**
     * Tests that a the constants stay the same.
     */
    @Test
    public void testConstantsStaySame() {
        assertThat(defaultSimplifier(True.INSTANCE),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(False.INSTANCE),
                is(False.INSTANCE));
    }
    
    /**
     * Tests whether constants in a conjunction are simplified correctly.
     */
    @Test
    public void testConjunctionWithConstant() {
        assertThat(defaultSimplifier(new Conjunction(A, True.INSTANCE)),
                is(A));
        assertThat(defaultSimplifier(new Conjunction(A, False.INSTANCE)),
                is(False.INSTANCE));
        
        // mirrored
        assertThat(defaultSimplifier(new Conjunction(True.INSTANCE, A)),
                is(A));
        assertThat(defaultSimplifier(new Conjunction(False.INSTANCE, A)),
                is(False.INSTANCE));
    }
    
    /**
     * Tests whether constants in a disjunction are simplified correctly.
     */
    @Test
    public void testDisjunctionWithConstant() {
        assertThat(defaultSimplifier(new Disjunction(A, True.INSTANCE)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Disjunction(A, False.INSTANCE)),
                is(A));
        
        // mirrored
        assertThat(defaultSimplifier(new Disjunction(True.INSTANCE, A)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Disjunction(False.INSTANCE, A)),
                is(A));
    }
    
    /**
     * Tests whether constants in a negation are simplified correctly.
     */
    @Test
    public void testNegationWithConstant() {
        assertThat(defaultSimplifier(new Negation(True.INSTANCE)),
                is(False.INSTANCE));
        assertThat(defaultSimplifier(new Negation(False.INSTANCE)),
                is(True.INSTANCE));
    }
    
    /**
     * Tests whether a conjunction with same arguments is simplified correctly.
     */
    @Test
    public void testConjunctionWithSameSides() {
        Formula part = new Disjunction(A, B);
        assertThat(defaultSimplifier(new Conjunction(part, part)),
                is(part));
    }
    
    /**
     * Tests whether a disjunction with same arguments is simplified correctly.
     */
    @Test
    public void testDisjunctionWithSameSides() {
        Formula part = new Disjunction(A, B);
        assertThat(defaultSimplifier(new Disjunction(part, part)),
                is(part));
    }
    
    /**
     * Tests whether a double negation is simplified correctly.
     */
    @Test
    public void testDoubleNegation() {
        assertThat(defaultSimplifier(new Negation(new Negation(A))),
                is(A));
    }
    
    /**
     * Tests whether doubule constants in a conjunction are simplified correctly.
     */
    @Test
    public void testConjunctionDoubleConstants() {
        assertThat(defaultSimplifier(new Conjunction(True.INSTANCE, True.INSTANCE)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Conjunction(True.INSTANCE, False.INSTANCE)),
                is(False.INSTANCE));
        assertThat(defaultSimplifier(new Conjunction(False.INSTANCE, True.INSTANCE)),
                is(False.INSTANCE));
        assertThat(defaultSimplifier(new Conjunction(False.INSTANCE, False.INSTANCE)),
                is(False.INSTANCE));
    }
    
    /**
     * Tests whether double constants in a disjunction are simplified correctly.
     */
    @Test
    public void testDisjunctionDoubleConstants() {
        assertThat(defaultSimplifier(new Disjunction(True.INSTANCE, True.INSTANCE)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Disjunction(True.INSTANCE, False.INSTANCE)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Disjunction(False.INSTANCE, True.INSTANCE)),
                is(True.INSTANCE));
        assertThat(defaultSimplifier(new Disjunction(False.INSTANCE, False.INSTANCE)),
                is(False.INSTANCE));
    }
    
    /**
     * Tests a more complex formula (e.g. that may appear during a feature effect analysis).
     *  
     * @throws ExpressionFormatException unwanted.
     */
    @Test
    public void testComplex() throws ExpressionFormatException {
        Parser<@NonNull Formula> parser = new Parser<>(new CStyleBooleanGrammar(new VariableCache()));
        Formula in = parser.parse("((0 && (B || C)) || (1 && (B || C))) && (!(0 && (B || C)) || !(1 && (B || C)))");
        
        assertThat(defaultSimplifier(in), is(new Disjunction(B, C)));
    }
    
}
