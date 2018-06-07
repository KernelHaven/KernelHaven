package net.ssehub.kernel_haven.util.logic.parser;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;


/**
 * Parses strings based on {@link Grammar}s.
 * 
 * @param <T> The type of formula that this parser will construct. A proper {@link Grammar} needs to be supplied.
 * 
 * @author Adam (from KernelMiner project)
 */
public final class Parser<T> {
    
    private @NonNull Grammar<T> grammar;
    
    /**
     * Creates a new parser for the given {@link Grammar}.
     * 
     * @param grammar The {@link Grammar} that describes the format to parse.
     */
    public Parser(@NonNull Grammar<T> grammar) {
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
    public T parse(@NonNull String expression) throws ExpressionFormatException  {
        Token[] tokens = lex(expression);
        T f = parse(tokens, 0, tokens.length - 1, expression);
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
                result.add(new Bracket(i, false));
                i += 1;
                
            } else if (grammar.isClosingBracketChar(expr, i)) {
                currentIdentifier = null;
                result.add(new Bracket(i, true));
                i += 1;
                
            } else if (op != null) {
                currentIdentifier = null;
                result.add(new OperatorToken(i, op));
                i += op.getSymbol().length();
                
            } else if (grammar.isIdentifierChar(expr, i)) {
                if (currentIdentifier == null) {
                    currentIdentifier = new Identifier(i, "");
                    result.add(currentIdentifier);
                }
                currentIdentifier.setName(currentIdentifier.getName() + expr[i]);
                i++;
                
            } else {
                throw makeException(expression, "Invalid character in expression: '" + expr[i] + "'", i);
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
     * @param expression The expression that is currently parsed. Used for error messages.
     * @return The parsed expression.
     * 
     * @throws ExpressionFormatException If the expression denoted by tokens is malformed.
     */
    // CHECKSTYLE:OFF // TODO: method is too long
    private T parse(Token[] tokens, int min, int max, @NonNull String expression) throws ExpressionFormatException {
    // CHECKSTYLE:ON
        // this method calls itself recursively

        // if we have  no tokens left then something went wrong
        if (max - min < 0) {
            throw makeException(expression, "Expected identifier", getTokenPos(tokens, max, min));
        }
        
        // if we only have one token left, then it must be an identifier
        if (max - min == 0) {
            if (!(tokens[min] instanceof Identifier)) {
                throw makeException(expression, "Expected identifier, got " + tokens[min], getTokenPos(tokens, min));
            }
            
            try {
                return grammar.makeIdentifierFormula(((Identifier) tokens[min]).getName());
            } catch (ExpressionFormatException e) {
                ExpressionFormatException newExc = makeException(expression, notNull(e.getMessage()),
                        getTokenPos(tokens, min));
                newExc.setStackTrace(e.getStackTrace());
                throw newExc;
            }
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
                    throw makeException(expression, "Unbalanced brackets", getTokenPos(tokens, i));
                }
                
            } else if (e instanceof OperatorToken) {
                Operator op = ((OperatorToken) e).getOperator();
                
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
            throw makeException(expression, "Unbalanced brackets", getTokenPos(tokens, min, max));
        }
        
        T result = null;

        result = createResult(tokens, min, max, ho, expression);
        
        return result;
    }

    /**
     * Turns the given highest operator into a result. The grammar handles the conversion.
     * Sub-parts of the operator are recursively passed to parse().
     * 
     * @param tokens The tokens to parse.
     * @param min The lower bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @param max The upper bound of the part of <code>tokens</code> that should be parsed, inclusive.
     * @param ho The highest operator.
     * @param expression The expression that is currently parsed. Used for error messages.
     * 
     * @return The result of the operator, as returned by the grammar.
     * @throws ExpressionFormatException If the grammar cannot parse this.
     */
    private T createResult(Token[] tokens, int min, int max, HighestOperatorData ho, @NonNull String expression)
            throws ExpressionFormatException {
        
        T result;
        // if there is an operator that is not nested in any brackets
        if (ho.highestOperatorLevel == 0) {
            // recursively parse the nested parts based on whether the operator is binary or not
            // and pass the results to the grammer to create the result
            
            if (ho.highestOp.isBinary()) {
                T leftTree = parse(tokens, min, ho.highestOpPos - 1, expression);
                T rightTree = parse(tokens, ho.highestOpPos + 1, max, expression);
                try {
                    result = grammar.makeBinaryFormula(ho.highestOp, leftTree, rightTree);
                } catch (ExpressionFormatException e) {
                    ExpressionFormatException newExc = makeException(expression, notNull(e.getMessage()),
                            ho.highestOpPos);
                    newExc.setStackTrace(e.getStackTrace());
                    throw newExc;
                }
                
            } else {
                if (ho.highestOpPos != min) {
                    throw makeException(expression, "Unary operator is not on the left", ho.highestOpPos);
                }
                
                T childFormula = parse(tokens, min + 1, max, expression);
                try {
                    result = grammar.makeUnaryFormula(ho.highestOp, childFormula);
                } catch (ExpressionFormatException e) {
                    ExpressionFormatException newExc = makeException(expression, notNull(e.getMessage()),
                            ho.highestOpPos);
                    newExc.setStackTrace(e.getStackTrace());
                    throw newExc;
                }
                
            }
        } else {
            // unpack the brackets and recursively call parse()
            
            if (!(tokens[min] instanceof Bracket) || !(tokens[max] instanceof Bracket)) {
                throw makeException(expression, "Couldn't find operator", getTokenPos(tokens, min, max));
            }
            
            Bracket first = (Bracket) tokens[min];
            Bracket last = (Bracket) tokens[max];
            
            if (first.isClosing() || !last.isClosing()) {
                throw makeException(expression, "Unbalanced brackets", getTokenPos(tokens, min, max));
            }
            
            result = parse(tokens, min + 1, max - 1, expression);
        }
        return result;
    }
    
    /**
     * Creates an {@link ExpressionFormatException}.
     * 
     * @param expression The formula that couldn't be parsed.
     * @param message The message describing the exception.
     * @param markers The indices for characters in the expression where markers should be displayed. Must be sorted
     *      ascending.
     * 
     * @return The exception with the proper error message.
     */
    private @NonNull ExpressionFormatException makeException(@NonNull String expression, @NonNull String message,
            int ... markers) {
        
        StringBuilder fullMessage = new StringBuilder(message).append("\nIn formula: ").append(expression);
        if (markers.length > 0) {
            fullMessage.append("\n            ");
            int markersIndex = 0;
            for (int i = 0; markersIndex < markers.length; i++) {
                if (i == markers[markersIndex]) {
                    fullMessage.append('^');
                    markersIndex++;
                    
                } else {
                    fullMessage.append(' ');
                }
            }
        }
        return new ExpressionFormatException(fullMessage.toString());
    }
    
    /**
     * Returns the position of the token in the given array.
     * 
     * @param tokens The list of tokens to get the single token position from.
     * @param index The index of the token in the array.
     * 
     * @return The position of the given token; empty array if index is out of bounds.
     */
    private int[] getTokenPos(Token[] tokens, int index) {
        int pos = -1;
        if (index >= 0 && index < tokens.length) {
            pos = tokens[index].getPos();
        }
        if (index == tokens.length && tokens.length != 0) {
            pos = tokens[index - 1].getPos() + tokens[index - 1].getLength();
        }
        
        int[] result;
        if (pos != -1) {
            result = new int[] {pos};
        } else {
            result = new int[0];
        }
        return result;
    }
    
    /**
     * Returns the position of the two token in the given array.
     * 
     * @param tokens The list of tokens to get the single token position from.
     * @param index1 The index of the first token in the array.
     * @param index2 The index of the second token in the array.
     * 
     * @return The position of the given token; empty array if index is out of bounds.
     */
    private int[] getTokenPos(Token[] tokens, int index1, int index2) {
        int[] pos1 = getTokenPos(tokens, index1);
        int[] pos2 = getTokenPos(tokens, index2);
        
        int[] result;
        if (pos1.length > 0 && pos2.length > 0) {
            result = new int[] {pos1[0], pos2[0]};
            
        } else if (pos1.length > 0) {
            result = new int[] {pos1[0]};
            
        } else if (pos2.length > 0) {
            result = new int[] {pos2[0]};
            
        } else {
            result = new int[0];
        }
        return result;
    }
    
}
