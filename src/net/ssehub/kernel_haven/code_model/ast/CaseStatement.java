package net.ssehub.kernel_haven.code_model.ast;

import java.io.File;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a <tt>case</tt> or a <tt>default</tt> block of a switch statement.
 * @author El-Sharkawy
 *
 */
public class CaseStatement extends SyntaxElementWithChildreen {
    
    public static enum CaseType {
        CASE, DEFAULT;
    }

    private SyntaxElement caseCondition;
    private CaseType type;
    
    public CaseStatement(@NonNull Formula presenceCondition, File sourceFile, SyntaxElement caseCondition,
        CaseType type) {
        
        super(presenceCondition, sourceFile);
        this.caseCondition = caseCondition;
        this.type = type;
    }
    
    public SyntaxElement getCaseCondition() {
        return caseCondition;
    }

    @Override
    protected String elementToString() {
        return type.name() + " " + (caseCondition == null ? "" : caseCondition.toString("")); // TODO
    }

    @Override
    public void accept(ISyntaxElementVisitor visitor) {
        visitor.visitCaseStatement(this);
    }

}
