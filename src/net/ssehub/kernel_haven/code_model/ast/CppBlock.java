package net.ssehub.kernel_haven.code_model.ast;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A preprocessor block with a variability condition (e.g. an #ifdef block). The nested children in this element
 * are the elements inside the preprocessor block.
 *
 * @author El-Sharkawy
 */
public class CppBlock extends AbstractSyntaxElementWithChildreen implements ICode {
    
    /**
     * The kind of preprocessor block.
     */
    public static enum Type {
        IF, IFDEF, IFNDEF, ELSEIF, ELSE;
    }

    private @Nullable Formula condition;
    
    private @NonNull Type type;
    
    private List<CppBlock> siblings;
    
    /**
     * Creates a {@link CppBlock}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param condition The variability condition of this block.
     * @param type The {@link Type} of preprocessor block that this is.
     */
    public CppBlock(@NonNull Formula presenceCondition, @Nullable Formula condition, @NonNull Type type) {
        super(presenceCondition);
        this.condition = condition;
        this.type = type;
        siblings = new ArrayList<>();
    }
    
    /**
     * Adds another if, elif, else block, which belongs to the same block.
     * @param sibling Another if, elif, else block, which belongs to the this block structure.
     */
    public void addSibling(CppBlock sibling) {
        siblings.add(sibling);
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    /**
     * Returns the {@link Type} of preprocessor block that this is.
     * 
     * @return The {@link Type} of preprocessor block that this is.
     */
    public @NonNull Type getType() {
        return type;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        String result = "#" + type.name();
        
        Formula condition = this.condition;
        if (condition != null) {
            result += " " + condition.toString();
        }
        return result + "\n";
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCppBlock(this);
    }

}
