package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Represents a switch statement. The nested children inside this element are the switch body.
 *
 * @author Adam
 */
public class SwitchStatement extends AbstractSyntaxElementWithChildreen {

    private @NonNull ICode header;
    
    // TODO: add a reference to the CaseStatements
    
    /**
     * Creates a {@link SwitchStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param header The header of this switch.
     */
    public SwitchStatement(@NonNull Formula presenceCondition, @NonNull ICode header) {
        
        super(presenceCondition);
        this.header = header;
    }
    
    /**
     * Returns the header of this switch.
     * 
     * @return The header of this switch.
     */
    public @NonNull ICode getHeader() {
        return header;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Switch\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

}
