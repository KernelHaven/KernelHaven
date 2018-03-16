package net.ssehub.kernel_haven.util.cpp.parser.ast;

import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * An (integer) literal.
 *
 * @author Adam
 */
public class IntegerLiteral extends CppExpression {

    private long value;
    
    /**
     * Creates a new integer literal.
     * 
     * @param value The literal value.
     */
    public IntegerLiteral(long value) {
        this.value = value;
    }
    
    /**
     * Returns the literal value. This is saved as a long, since CPP doesn't know data types and a normal integer may
     * overflow.
     * 
     * @return The literal value.
     */
    public long getValue() {
        return value;
    }

    @Override
    public <T> T accept(@NonNull ICppExressionVisitor<T> visitor) throws ExpressionFormatException {
        return visitor.visitLiteral(this);
    }
    
    @Override
    protected @NonNull String toString(@NonNull String indentation) {
        return indentation + "Literal " + value;
    }
    
}
