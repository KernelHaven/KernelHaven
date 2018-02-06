package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents an <tt>if</tt>, <tt>else</tt> or an <tt>else if</tt> block.
 * @author El-Sharkawy
 *
 */
public class BranchStatement extends SyntaxElementWithChildreen {
    
    public static enum Type {
        IF, ELSE, ELSE_IF;
    }

    private @NonNull Type type;
    
    private @Nullable SyntaxElement ifCondition;
    
    private @NonNull List<@NonNull BranchStatement> siblings;
    
    /**
     * 
     * @param presenceCondition
     * @param sourceFile
     * @param elseIfCondition The condition of the <tt>else if</tt> block, maybe <tt>null</tt> in case of an
     *     <tt>else</tt> block (in this case {@link Type#ELSE} must be passed as <tt>type</tt>). 
     * @param type
     */
    public BranchStatement(@NonNull Formula presenceCondition, @NonNull Type type,
            @Nullable SyntaxElement ifCondition) {
        
        super(presenceCondition);
        this.ifCondition = ifCondition;
        this.type = type;
        siblings = new LinkedList<>();
    }
    
    public void addSibling(@NonNull BranchStatement sibling) {
        siblings.add(sibling);
    }
    
    public int getSiblingCount() {
        return siblings.size();
    }
    
    public @NonNull BranchStatement getSibling(int index) throws IndexOutOfBoundsException {
        return notNull(siblings.get(index));
    }
    
    public @Nullable SyntaxElement getIfCondition() {
        return ifCondition;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        String result = type.name() + " (" + getSiblingCount() + " siblings)\n";
        if (ifCondition != null) {
            result += ifCondition.toString(indentation + "\t");
        }
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitBranchStatement(this);
    }

}
