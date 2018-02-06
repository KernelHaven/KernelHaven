package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A list containing {@link ICode} elements. For example, this class is used to represent that some parts of an
 * unparsed code string have no immediate variability conditions, while other parts are wrapped inside a
 * {@link CppBlock}. The nested children are only {@link ICode} elements.
 *
 * @author Adam
 */
public class CodeList extends AbstractSyntaxElementWithChildreen implements ICode {

    /**
     * Creates a {@link CodeList}.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public CodeList(@NonNull Formula presenceCondition) {
        super(presenceCondition);
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "CodeList\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCodeList(this);
    }
    
}
