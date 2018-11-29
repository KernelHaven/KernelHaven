package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a <tt>case</tt> or a <tt>default</tt> block of a switch statement. The children nested inside this are
 * the content of the case.
 * 
 * TODO SE: should content following a case: statement really be nested inside of it? 
 * 
 * @author El-Sharkawy
 */
public class CaseStatement extends AbstractSyntaxElementWithNesting {
    
    /**
     * The type of case or default statement.
     */
    public static enum CaseType {
        CASE, DEFAULT;
    }

    private @Nullable ICode caseCondition;
    
    private @NonNull CaseType type;
    
    private @NonNull SwitchStatement switchStatement;
    
    /**
     * Creates a {@link CaseStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param caseCondition The value of a case statement; <code>null</code> if this is a default statement.
     * @param type The {@link CaseType} of statement that this is.
     * @param switchStatement The {@link SwitchStatement} to which this case belongs to.
     */
    public CaseStatement(@NonNull Formula presenceCondition, @Nullable ICode caseCondition,
            @NonNull CaseType type, @NonNull SwitchStatement switchStatement) {
        
        super(presenceCondition);
        this.caseCondition = caseCondition;
        this.type = type;
        this.switchStatement = switchStatement;
    }
    
    /**
     * Returns the value of a case statement; <code>null</code> if this is a default statement.
     * 
     * @return The value of a case statement.
     */
    public @Nullable ICode getCaseCondition() {
        return caseCondition;
    }
    
    /**
     * Returns the {@link CaseType} of statement that this is.
     * 
     * @return The {@link CaseType} of statement that this is.
     */
    public @NonNull CaseType getType() {
        return type;
    }
    
    /**
     * Returns the {@link SwitchStatement} that this case belongs to.
     * 
     * @return The {@link SwitchStatement} that this {@link CaseStatement} belongs to.
     */
    public @NonNull SwitchStatement getSwitchStatement() {
        return switchStatement;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        ICode caseCondition = this.caseCondition;
        
        String result = type.name() + "\n";
        if (caseCondition != null) {
            result += caseCondition.toString(indentation + "\t");
        }
        
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCaseStatement(this);
    }

}
