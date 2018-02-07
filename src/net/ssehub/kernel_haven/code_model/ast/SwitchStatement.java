package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * Represents a switch statement. The nested children inside this element are the switch body. Statements after a
 * {@link CaseStatement} are nested inside that {@link CaseStatement}.
 * </p>
 * <p>
 * This class a list of all the {@link CaseStatement} that belong to this switch.
 * </p>
 *
 * @author Adam
 */
public class SwitchStatement extends AbstractSyntaxElementWithChildreen {

    private @NonNull ICode header;
    
    private @NonNull List<@NonNull CaseStatement> cases;
    
    /**
     * Creates a {@link SwitchStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param header The header of this switch.
     */
    public SwitchStatement(@NonNull Formula presenceCondition, @NonNull ICode header) {
        
        super(presenceCondition);
        this.header = header;
        this.cases = new LinkedList<>();
    }
    
    /**
     * Returns the header of this switch.
     * 
     * @return The header of this switch.
     */
    public @NonNull ICode getHeader() {
        return header;
    }
    
    /**
     * Returns the {@link CaseStatement} at the given index.
     * 
     * @param index The index of the {@link CaseStatement} to get.
     * @return The {@link CaseStatement} at the given index.
     * 
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    public @NonNull CaseStatement getCase(int index) throws IndexOutOfBoundsException {
        return notNull(cases.get(index));
    }
    
    /**
     * Adds another {@link CaseStatement} to this switch. This should only be called by the extractors that
     * creates the AST. The {@link CaseStatement} should be added in the order they appear in the source file.
     * Each of the {@link CaseStatement}s added here should have a reference to this {@link SwitchStatement}.
     * 
     * @param caseStatement The {@link CaseStatement} to add.
     */
    public void addCase(@NonNull CaseStatement caseStatement) {
        cases.add(caseStatement);
    }
    
    /**
     * Returns the number of {@link CaseStatement}s that this switch has.
     * 
     * @return The number of {@link CaseStatement}s.
     */
    public int getCasesCount() {
        return cases.size();
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Switch (" + getCasesCount() + " cases)\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

}
