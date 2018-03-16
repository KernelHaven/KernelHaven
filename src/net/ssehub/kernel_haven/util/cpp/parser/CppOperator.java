package net.ssehub.kernel_haven.util.cpp.parser;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Operators used inside C preprocessor (CPP) expressions.
 *
 * @author Adam
 */
public enum CppOperator {

    BOOL_AND("&&", 2, false),
    BOOL_OR("||", 1, false),
    BOOL_NOT("!", 11, true),
    
    INT_ADD("+", 9, false),
    INT_ADD_UNARY("+", 11, true),
    INT_SUB("-", 9, false),
    INT_SUB_UNARY("-", 11, true),
    INT_MUL("*", 10, false),
    INT_DIV("/", 10, false),
    INT_MOD("&", 10, false),
    INT_INC("++", 11, true),
    INT_DEC("--", 11, true),
    
    CMP_EQ("==", 6, false),
    CMP_NE("!=", 6, false),
    CMP_LT("<", 7, false),
    CMP_LE("<=", 7, false),
    CMP_GT(">", 7, false),
    CMP_GE(">=", 7, false),
    
    BIN_AND("&", 5, false),
    BIN_OR("|", 3, false),
    BIN_XOR("^", 4, false),
    BIN_SHR(">>", 8, false),
    BIN_SHL("<<", 8, false),
    BIN_INV("~", 11, true);
    
    private @NonNull String symbol;
    
    private int precedence;
    
    private boolean unary;
    
    /**
     * Creates a new {@link CppOperator}.
     * 
     * @param symbol The string representation inside source code.
     * @param precedence The precedence of this operator. Higher precedence means that this operator is evaluated first.
     * @param unary Whether this operator is unary (<code>true</code>) or binary (<code>false</code>).
     */
    private CppOperator(@NonNull String symbol, int precedence, boolean unary) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.unary = unary;
    }

    /**
     * Returns the string representation of this operator in source code.
     * 
     * @return The string representation of this operator.
     */
    public @NonNull String getSymbol() {
        return symbol;
    }
    
    /**
     * Returns the precedence of this operator. Higher precedence means that this operator is evaluated first.
     * 
     * @return The precedence of this operator.
     */
    public int getPrecedence() {
        return precedence;
    }
    
    /**
     * Whether this is an unary operator.
     * 
     * @return Whether this is an unary operator.
     */
    public boolean isUnary() {
        return unary;
    }
    
    /**
     * Whether this is a binary operator.
     * 
     * @return Whether this is a binary operator.
     */
    public boolean isBinary() {
        return !unary;
    }
    
    @Override
    public String toString() {
        return symbol + (unary ? " (unary)" : " (binary)");
    }
    
}
