package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * Represents an <tt>if</tt>, <tt>else</tt> or an <tt>else if</tt> block. The children nested inside this element
 * are the branch statement body.
 * </p>
 * <p>
 * It contains a list of siblings: each {@link BranchStatement} of an if-elseif-else construct has references to all
 * siblings in the same construct (including itself). The ordering of these is always the same as in the original
 * source code.
 * </p>
 *
 * @author Adam
 * @author El-Sharkawy
 */
public class BranchStatement extends AbstractSyntaxElementWithNesting {
    
    /**
     * The type of {@link BranchStatement}. 
     */
    public static enum Type {
        IF, ELSE, ELSE_IF;
    }

    private @NonNull Type type;
    
    private @Nullable ICode ifCondition;
    
    private @NonNull List<@NonNull BranchStatement> siblings;
    
    /**
     * Creates a {@link BranchStatement}.
     * 
     * @param presenceCondition The presence condition for this element.
     * @param type Which {@link Type} of branching statement this is.
     * @param ifCondition The condition of an <tt>if</tt> or <tt>else if</tt> block; <tt>null</tt> if this is an
     *     <tt>else</tt> block (in this case {@link Type#ELSE} must be passed as <tt>type</tt>). 
     */
    public BranchStatement(@NonNull Formula presenceCondition, @NonNull Type type,
            @Nullable ICode ifCondition) {
        
        super(presenceCondition);
        this.ifCondition = ifCondition;
        this.type = type;
        siblings = new LinkedList<>();
    }
    
    /**
     * Adds another sibling to this {@link BranchStatement}. This should only be called by the extractors that
     * creates the AST. It should be ensured that all siblings have a complete list of all siblings in a given
     * if-elseif-else construct (including themselves).
     * 
     * @param sibling The sibling to add.
     */
    public void addSibling(@NonNull BranchStatement sibling) {
        siblings.add(sibling);
    }
    
    /**
     * Returns the number of siblings this element has. This is at lest one (this object itself).
     * 
     * @return The number of siblings.
     */
    public int getSiblingCount() {
        return siblings.size();
    }
    
    /**
     * Returns the sibling at the given index.
     * 
     * @param index The index to get the sibling for.
     * @return The sibling at the given index.
     * 
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    public @NonNull BranchStatement getSibling(int index) throws IndexOutOfBoundsException {
        return notNull(siblings.get(index));
    }
    
    /**
     * Returns the condition of this {@link BranchStatement}. <code>null</code> if this is an else block.
     * 
     * @return The condition of this {@link BranchStatement}.
     */
    public @Nullable ICode getIfCondition() {
        return ifCondition;
    }
    
    /**
     * Returns the type of this {@link BranchStatement}.
     * 
     * @return The type of branching statement.
     */
    public @NonNull Type getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
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
