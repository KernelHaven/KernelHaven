package net.ssehub.kernel_haven.util.logic.parser;

import java.util.LinkedList;
import java.util.List;


/**
 * Parses strings based on {@link Grammar}s.
 * 
 * @param <T> The type of formula that this parser will construct. A proper {@link Grammar} needs to be supplied.
 * 
 * @author Adam (from KernelMiner project)
 */
public class Parser<T> {
    
    private Grammar<T> grammar;
    
    /**
     * Creates a new parser for the given {@link Grammar}.
     * 
     * @param grammar The {@link Grammar} that describes the format to parse.
     */
    public Parser(Grammar<T> grammar) {
        this.grammar = grammar;
    }
    
    /**
     * Parses the given string based on the {@link Grammar} this parser was
     * created for.
     * 
     * @param expression The expression to parse.
     * @return The parsed expression, as created by the make* methods in the given {@link Grammar}.
     * 
     * @throws ExpressionFormatException If the supplied string is not a valid expression for the given {@link Grammar}.
     */
    public T parse(String expression) throws ExpressionFormatException  {
        Token[] tokens = lex(expression);
        T f = parse(tokens, 0, tokens.length - 1);
        return f;
    }

    /**
     * Lexes the given expression, based on the {@link Grammar} this parser was created for.
     * 
     * @param expression The expression to lex.
     * @return A flat array of {@link Token}s found in the expression.
     * 
     * @throws ExpressionFormatException If the expression contains characters not allowed by the {@link Grammar}.
     */
    private Token[] lex(String expression) throws ExpressionFormatException {
        List<Token> result = new LinkedList<>();
        
        Identifier currentIdentifier = null;
        
        char[] expr = expression.toCharArray();
        
        // iterate over the string; i is incremented based on which token was identified
        for (int i = 0; i < expr.length;) {
            Operator op = grammar.getOperator(expr, i);
            
            if (grammar.isWhitespaceChar(expr, i)) {
                // whitespaces are ignored
                currentIdentifier = null;
                i++;
                
            } else  if (grammar.isOpeningBracketChar(expr, i)) {
                currentIdentifier = null;
                result.add(new Bracket(false));
                i += 1;
                
            } else if (grammar.isClosingBracketChar(expr, i)) {
                currentIdentifier = null;
                result.add(new Bracket(true));
                i += 1;
                
            } else if (op != null) {
                currentIdentifier = null;
                result.add(op);
                i += op.getSymbol().length();
                
            } else if (grammar.isIdentifierChar(expr, i)) {
                if (currentIdentifier == null) {
                    currentIdentifier = new Identifier("");
                    result.add(currentIdentifier);
                }
                currentIdentifier.setName(currentIdentifier.getName() + expr[i]);
                i++;
                
            } else {
                throw new ExpressionFormatException("Invalid character in expression: " + expr[i]);
                
            }
        }
        
        return result.toArray(new Token[0]);
    }
    
    /**
     * Stores the necessary parts to identify the highest operator.
     */
    private static final class HighestOperatorData {
        private int highestOperatorLevel = -1;
        private int highestOpPos = -1;
        private Operator highestOp = null;
    }
    
    /**
     * Parses the flat array of tokens that the lexer found, based on the {@link Grammar}
     * this parser was created for.
     * <p>
     * For performance purposes, there is only instance of the <code>tokens</code>
     * array. Two indices, <code>min</code> and <code>max</code> are provided,
     * to indicate which part should be parsed by this method.
     * </p>
     * 
     * @param tokens The flat array of tokens; the output of {@link #lex(String)}.
     * @param min The lower bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @param max The upper bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @return The parsed expression.
     * 
     * @throws ExpressionFormatException If the expression denoted by tokens is malformed.
     */
    // CHECKSTYLE:OFF
    private T parse(Token[] tokens, int min, int max) throws ExpressionFormatException {
        // this method calls itself recursively

        // if we have  no tokens left then something went wrong
        if (max - min < 0) {
            throw new ExpressionFormatException("Expected identifier");
        }
        
        // if we only have one token left, then it must be an identifier
        if (max - min == 0) {
            if (!(tokens[min] instanceof Identifier)) {
                throw new ExpressionFormatException("Expected identifier, got " + tokens[min]);
            }
            
            return grammar.makeIdentifierFormula(((Identifier) tokens[min]).getName());
        }
        
        // find the "highest" operator in the bracket tree
        
        HighestOperatorData ho = new HighestOperatorData();
        
        int bracketDepth = 0;
        
        for (int i = min; i <= max; i++) {
            Token e = tokens[i];
            if (e instanceof Bracket) {
                if (((Bracket) e).isClosing()) {
                    bracketDepth--;
                } else {
                    bracketDepth++;
                }
                
                if (bracketDepth < 0) {
                    throw new ExpressionFormatException("Unbalanced brackets");
                }
                
            } else if (e instanceof Operator) {
                Operator op = (Operator) e;
                
                // if ...
                if (
                        ho.highestOp == null // .. we haven't found any operator yet
                        || bracketDepth < ho.highestOperatorLevel // ... the current operator is
                                                               // "higher" in the bracket structure
                        || (
                                ho.highestOperatorLevel == bracketDepth
                                && op.getPrecedence() > ho.highestOp.getPrecedence()
                        ) // ... the current operator has the same level as the previously found one, but
                          // it has a higher precedence
                ) {
                    ho.highestOpPos = i;
                    ho.highestOp = op;
                    ho.highestOperatorLevel = bracketDepth;
                }
            }
        }
        
        if (bracketDepth != 0) {
            throw new ExpressionFormatException("Unbalanced brackets");
        }
        
        T result = null;

        result = createResult(tokens, min, max, ho);
        
        return result;
    }
    // CHECKSTYLE:ON

    /**
     * Turns the given highest operator into a result. The grammar handles the conversion.
     * Sub-parts of the operator are recursively passed to parse().
     * 
     * @param tokens The tokens to parse.
     * @param min The lower bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @param max The upper bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @param ho The highest operator.
     * @return The result of the operator, as returned by the grammar.
     * @throws ExpressionFormatException If the grammar cannot parse this.
     */
    private T createResult(Token[] tokens, int min, int max, HighestOperatorData ho) throws ExpressionFormatException {
        T result;
        // if there is an operator that is not nested in any brackets
        if (ho.highestOperatorLevel == 0) {
            // recursively parse the nested parts based on whether the operator is binary or not
            // and pass the results to the grammer to create the result
            
            if (ho.highestOp.isBinary()) {
                T leftTree = parse(tokens, min, ho.highestOpPos - 1);
                T rightTree = parse(tokens, ho.highestOpPos + 1, max);
                result = grammar.makeBinaryFormula(ho.highestOp, leftTree, rightTree);
                
            } else {
                if (ho.highestOpPos != min) {
                    throw new ExpressionFormatException("Unary operator is not on the left");
                }
                
                T childFormula = parse(tokens, min + 1, max);
                result = grammar.makeUnaryFormula(ho.highestOp, childFormula);
                
            }
        } else {
            // unpack the brackets and recursively call parse()
            
            if (!(tokens[min] instanceof Bracket) || !(tokens[max] instanceof Bracket)) {
                throw new ExpressionFormatException("Couldn't find operator");
            }
            
            Bracket first = (Bracket) tokens[min];
            Bracket last = (Bracket) tokens[max];
            
            if (first.isClosing() || !last.isClosing()) {
                throw new ExpressionFormatException("Unbalanced brackets");
            }
            
            result = parse(tokens, min + 1, max - 1);
        }
        return result;
    }
    
}
