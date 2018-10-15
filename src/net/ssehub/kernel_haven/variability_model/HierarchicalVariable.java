package net.ssehub.kernel_haven.variability_model;

import java.util.HashSet;
import java.util.List;
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
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param variable A variable to copy the name, type and DIMACS number from.
     */
    public HierarchicalVariable(@NonNull VariabilityVariable variable) {
        super(variable.getName(), variable.getType(), variable.getDimacsNumber());
        children = new HashSet<>();

        List<@NonNull SourceLocation> locations = variable.getSourceLocations();
        if (locations != null) {
            for (SourceLocation sl : locations) {
                addLocation(sl);
            }
        }
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
     * <p>
     * Sets the parent for this variable, add this variable as a child to the parent variable and set the nesting depth
     * of this to parent+1. If there was a previous parent set for this, then this variable is removed from the
     * previous parents children list. If the new parent is null, then the new nesting depth is 0.
     * </p>
     * <p>
     * This method should only be called by the extractor that creates the variability model.
     * </p>
     * 
     * @param parent The new parent for this variable.
     */
    public void setParent(@Nullable HierarchicalVariable parent) {
        // remove from previous
        HierarchicalVariable previousParent = this.parent;
        if (previousParent != null) {
            previousParent.children.remove(this);
        }
        
        // add to new
        this.parent = parent;
        if (null != parent) {
            parent.children.add(this);
            this.nestingDepth = parent.nestingDepth + 1;
        } else {
            this.nestingDepth = 0;
        }
    }
    
    /**
     * Overrides the nesting depth. This should only be called by the serialization, to ensure consistency.
     * 
     * @param nestingDepth The new nesting depth.
     */
    void setNestingDepth(int nestingDepth) {
        this.nestingDepth = nestingDepth;
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
