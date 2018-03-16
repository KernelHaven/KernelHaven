package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

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
    public T visitExpressionList(ExpressionList expressionList) throws ExpressionFormatException;
    
    /**
     * Visits a {@link FunctionCall}.
     * 
     * @param call The function call to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitFunctionCall(FunctionCall call) throws ExpressionFormatException;
    
    /**
     * Visits a {@link Variable}.
     * 
     * @param variable The variable to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitVariable(Variable variable) throws ExpressionFormatException;
    
    /**
     * Visits an {@link Operator}.
     * 
     * @param operator The operator to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitOperator(Operator operator) throws ExpressionFormatException;
    
    /**
     * Visits an {@link IntegerLiteral}.
     * 
     * @param literal The literal to visit.
     * 
     * @return Something.
     * 
     * @throws ExpressionFormatException If the expression is malformed.
     */
    public T visitLiteral(IntegerLiteral literal) throws ExpressionFormatException;
    
}
