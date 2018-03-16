package net.ssehub.kernel_haven.util.cpp.parser.ast;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;

/**
 * A temporary element in the AST while is parsed. Contains a flat list of {@link CppExpression}s.
 *
 * @author Adam
 */
public class ExpressionList extends CppExpression {

    private List<CppExpression> expressions;
    
    /**
     * Creates a new {@link ExpressionList} with no nested expressions.
     */
    public ExpressionList() {
        this.expressions = new ArrayList<>();
    }
    
    /**
     * Returns the number of nested expressions.
     * 
     * @return The number of nested expressions.
     */
    public int getExpressionSize() {
        return expressions.size();
    }
    
    /**
     * Returns the expression at the given index.
     * 
     * @param index The index to get the expression at.
     * 
     * @return The nested expression.
     */
    public CppExpression getExpression(int index) {
        return expressions.get(index);
    }
    
    /**
     * Replaces the expression at the given index.
     * 
     * @param index The index to replace the expression at.
     * @param expression The new expression for that index.
     */
    public void setExpression(int index, CppExpression expression) {
        this.expressions.set(index, expression);
    }
    
    /**
     * Removes the expression at the given index. The following expressions shift done by one.
     * 
     * @param index The index to remove.
     */
    public void removeExpression(int index) {
        this.expressions.remove(index);
    }
    
    /**
     * Adds a nested expression to the end of the list.
     * 
     * @param expression The expression to add.
     */
    public void addExpression(CppExpression expression) {
        expressions.add(expression);
    }
    
    @Override
    public <T> T accept(ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitExpressionList(this);
    }

    @Override
    protected String toString(String indentation) {
        StringBuilder result = new StringBuilder(indentation).append("ExpressionList");
        
        indentation += '\t';
        for (CppExpression expr : expressions) {
            result.append('\n').append(expr.toString(indentation));
        }
        
        return result.toString();
    }
    
}
