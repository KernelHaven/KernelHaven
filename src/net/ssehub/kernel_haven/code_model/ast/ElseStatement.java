package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

// TODO: Why not use CppBlock with type = else or elseif?
/**
 * Represents an <tt>else</tt> or an <tt>else if</tt> block of an {@link IfStructure}.
 * @author El-Sharkawy
 *
 */
public class ElseStatement extends SyntaxElementWithChildreen {
    
    public static enum ElseType {
        ELSE, ELSE_IF;
    }

    private @Nullable SyntaxElement elseIfCondition;
    
    private @NonNull ElseType type;
    
    /**
     * 
     * @param presenceCondition
     * @param sourceFile
     * @param elseIfCondition The condition of the <tt>else if</tt> block, maybe <tt>null</tt> in case of an
     *     <tt>else</tt> block (in this case {@link ElseType#ELSE} must be passed as <tt>type</tt>). 
     * @param type
     */
    public ElseStatement(@NonNull Formula presenceCondition, @NonNull File sourceFile,
            @Nullable SyntaxElement elseIfCondition, @NonNull ElseType type) {
        
        super(presenceCondition, sourceFile);
        this.elseIfCondition = elseIfCondition;
        this.type = type;
    }
    
    public @Nullable SyntaxElement getElseIfCondition() {
        return elseIfCondition;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        String result = notNull(type.name());
        if (elseIfCondition != null) {
            result += "\n" + elseIfCondition.toString(indentation + "\t");
        }
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitElseStatement(this);
    }

}
