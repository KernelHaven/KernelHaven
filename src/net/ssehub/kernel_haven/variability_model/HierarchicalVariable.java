package net.ssehub.kernel_haven.variability_model;

import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link VariabilityVariable} that has additional information about the hierarchy of variables.
 * 
 * @author Adam
 */
public class HierarchicalVariable extends VariabilityVariable {

    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param name The name of the variable.
     * @param type The type of the variable.
     */
    public HierarchicalVariable(@NonNull String name, @NonNull String type) {
        super(name, type);
    }

    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param name The name of the variable.
     * @param type The type of the variable.
     * @param dimacsNumer The number in the dimacs mapping of this variable.
     */
    public HierarchicalVariable(@NonNull String name, @NonNull String type, int dimacsNumer) {
        super(name, type, dimacsNumer);
    }
    
    /**
     * Returns the parent of this variable.
     * 
     * @return The parent variable. <code>null</code> if this variable has no parent.
     */
    public @Nullable HierarchicalVariable getParent() {
        return null;
    }
    
    /**
     * Returns the children of this variable. May be empty, if this has no children.
     * 
     * @return The children of this variable.
     */
    public @NonNull Set<@NonNull HierarchicalVariable> getChildren() {
        return new HashSet<>();
    }
    
    /**
     * Returns the nesting depth of this variable. This is the number of parents above this variable. 0 means top level.
     * 
     * @return The nesting depth of this variable.
     */
    public int getNestingDepth() {
        return 0;
    }
    
}
