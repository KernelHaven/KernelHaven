package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A visitor for the {@link CppExpression} AST.
 *
 * @param <T> The return type of this visitor.
 *
 * @author Adam
 */
public interface ICppExressionVisitor<T> {

    /**
     * Visits an {@link ExpressionList}. These kind of nodes only appear during parsing.
     * 
     * @param expressionList The expression list to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public default T visitExpressionList(@NonNull ExpressionList expressionList) throws ExpressionFormatException {
        throw new AssertionError("There shouldn't be an ExpressionList left after parsing");
    }
    
    /**
     * Visits a {@link FunctionCall}.
     * 
     * @param call The function call to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitFunctionCall(@NonNull FunctionCall call) throws ExpressionFormatException;
    
    /**
     * Visits a {@link Variable}.
     * 
     * @param variable The variable to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitVariable(@NonNull Variable variable) throws ExpressionFormatException;
    
    /**
     * Visits an {@link Operator}.
     * 
     * @param operator The operator to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitOperator(@NonNull Operator operator) throws ExpressionFormatException;
    
    /**
     * Visits an {@link IntegerLiteral}.
     * 
     * @param literal The literal to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitLiteral(@NonNull IntegerLiteral literal) throws ExpressionFormatException;
    
}
