package net.ssehub.kernel_haven.variability_model;

import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link VariabilityVariable} that has additional information about the hierarchy of variables.
 * 
 * TODO: serialization
 * 
 * @author Adam
 */
public class HierarchicalVariable extends VariabilityVariable {

    private @Nullable HierarchicalVariable parent;
    
    private @NonNull Set<@NonNull HierarchicalVariable> children;
    
    private int nestingDepth;
    
    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param name The name of the variable.
     * @param type The type of the variable.
     */
    public HierarchicalVariable(@NonNull String name, @NonNull String type) {
        super(name, type);
        children = new HashSet<>();
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
        children = new HashSet<>();
    }
    
    /**
     * Returns the parent of this variable.
     * 
     * @return The parent variable. <code>null</code> if this variable has no parent.
     */
    public @Nullable HierarchicalVariable getParent() {
        return parent;
    }
    
    /**
     * Sets the parent for this variable, add this variable as a child to the parent variable and set the nesting depth
     * of this to parent+1. This method should only be called by the extractor that creates the variability model.
     * 
     * @param parent The new parent for this variable.
     */
    public void setParent(@Nullable HierarchicalVariable parent) {
        this.parent = parent;
        if (null != parent) {
            parent.children.add(this);
            this.nestingDepth = parent.nestingDepth + 1;
        }
    }
    
    /**
     * Returns the children of this variable. May be empty, if this has no children.
     * 
     * @return The children of this variable.
     */
    public @NonNull Set<@NonNull HierarchicalVariable> getChildren() {
        return children;
    }
    
    /**
     * Returns the nesting depth of this variable. This is the number of parents above this variable. 0 means top level.
     * 
     * @return The nesting depth of this variable.
     */
    public int getNestingDepth() {
        return nestingDepth;
    }
    
}
