package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents an <tt>else</tt> or an <tt>else if</tt> block of an {@link IfStructure}.
 * @author El-Sharkawy
 *
 */
public class ElseStatement extends SyntaxElementWithChildreen {
    
    public static enum ElseType {
        ELSE, ELSE_IF;
    }

    private SyntaxElement elseIfCondition;
    private ElseType type;
    
    /**
     * 
     * @param presenceCondition
     * @param sourceFile
     * @param elseIfCondition The condition of the <tt>else if</tt> block, maybe <tt>null</tt> in case of an
     *     <tt>else</tt> block (in this case {@link ElseType#ELSE} must be passed as <tt>type</tt>). 
     * @param type
     */
    public ElseStatement(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement elseIfCondition,
        @NonNull ElseType type) {
        
        super(presenceCondition, sourceFile);
        this.elseIfCondition = elseIfCondition;
        this.type = type;
    }
    
    public SyntaxElement getElseIfCondition() {
        return elseIfCondition;
    }

    @Override
    protected String elementToString() {
        return type.name() + " " + (elseIfCondition == null ? "" : elseIfCondition.toString("")); // TODO
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitElseStatement(this);
    }

}
