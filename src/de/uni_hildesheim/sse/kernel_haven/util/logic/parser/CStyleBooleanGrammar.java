package de.uni_hildesheim.sse.kernel_haven.util.logic.parser;

import de.uni_hildesheim.sse.kernel_haven.util.logic.Conjunction;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Disjunction;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Formula;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Negation;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Variable;

/**
 * A {@link Grammar} for C-style boolean expressions.
 * 
 * <p>
 * Examples:
 * <ul>
 * <li><code>A && B</code></li>
 * <li><code>AaA_bcd || D && !E</code></li>
 * <li><code>((A && B) || !(C || B)) && !E</code></li>
 * </ul>
 * </p>
 * 
 * @author Adam (from KernelMiner project)
 */
public class CStyleBooleanGrammar extends Grammar<Formula> {

    protected static final Operator AND = new Operator("&&", true, 2);

    protected static final Operator OR = new Operator("||", true, 2);

    protected static final Operator NOT = new Operator("!", false, 1);

    private VariableCache cache;

    /**
     * Creates this grammar with the given variable cache. The cache is used to
     * create every single {@link Variable}, to ensure that no two different
     * {@link Variable} objects with the same variable name exist.
     * 
     * @param cache
     *            The cache to use, or <code>null</code>.
     */
    public CStyleBooleanGrammar(VariableCache cache) {
        this.cache = cache;
    }

    @Override
    public Operator getOperator(char[] str, int it) {
        Operator result = null;

        if (str[it] == '!') {
            result = NOT;
        }

        if (str[it] == '&' && str[it + 1] == '&') {
            result = AND;
        }

        if (str[it] == '|' && str[it + 1] == '|') {
            result = OR;
        }

        return result;
    }

    @Override
    public boolean isWhitespaceChar(char[] str, int it) {
        return str[it] == ' ';
    }

    @Override
    public boolean isOpeningBracketChar(char[] str, int it) {
        return str[it] == '(';
    }

    @Override
    public boolean isClosingBracketChar(char[] str, int it) {
        return str[it] == ')';
    }

    @Override
    public boolean isIdentifierChar(char[] str, int it) {
        // CHECKSTYLE:OFF
        // checkstyle thinks that this boolean formula is too complex;
        // but we need it this way because every other option is not as
        // performant, and this
        // is a rather performance critical code path.
        return (str[it] >= 'a' && str[it] <= 'z') || (str[it] >= 'A' && str[it] <= 'Z')
                || (str[it] >= '0' && str[it] <= '9') || (str[it] == '_');
        // CHECKSTYLE:ON
    }

    @Override
    public Formula makeUnaryFormula(Operator operator, Formula child) throws ExpressionFormatException {
        if (operator.equals(NOT)) {
            return new Negation(child);
        } else {
            throw new ExpressionFormatException("Unkown operator: " + operator);
        }
    }

    @Override
    public Formula makeBinaryFormula(Operator operator, Formula left, Formula right) throws ExpressionFormatException {
        Formula result = null;

        if (operator.equals(AND)) {
            result = new Conjunction(left, right);
        } else if (operator.equals(OR)) {
            result = new Disjunction(left, right);
        } else {
            throw new ExpressionFormatException("Unkown operator: " + operator);
        }

        return result;
    }

    @Override
    public Formula makeIdentifierFormula(String identifier) throws ExpressionFormatException {
        Formula result = null;

        if (this.cache != null) {
            result = this.cache.getVariable(identifier);
        } else {
            result = new Variable(identifier);
        }

        return result;
    }

}
