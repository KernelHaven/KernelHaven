package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

public class Function extends AbstractSyntaxElementWithChildreen {

    private @NonNull String name;
    
    private @NonNull ICode header;
    
    public Function(@NonNull Formula presenceCondition, @NonNull String name, @NonNull ICode header) {
        super(presenceCondition);
        this.header = header;
        this.name = name;
    }
    
    public @NonNull ICode getHeader() {
        return header;
    }
    
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Function " + name + "\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitFunction(this);
    }

}
