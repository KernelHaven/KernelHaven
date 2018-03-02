package net.ssehub.kernel_haven.variability_model;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Describes which information / features a {@link VariabilityModel} provides.
 * 
 * @author Adam
 */
public class VariabilityModelDescriptor {
    
    /**
     * Describes what {@link VariabilityVariable}s in the model represent.
     */
    public static enum VariableType {
        
        /**
         * {@link VariabilityVariable}s are simple booleans (or tristates).
         */
        BOOLEAN,
        
        /**
         * {@link VariabilityVariable}s are FiniteIntegerVariables from a NonBoolean preparation.
         */
        FINITE_INTEGER,
    }
    
    /**
     * Describes which format the constraint model file has. 
     */
    public static enum ConstraintFileType {
        
        /**
         * No specific format.
         */
        UNSPECIFIED,
        
        /**
         * Constraint model file has the DIMACS format. It can be used with CnfUtils.
         */
        DIMACS,
        
    }
    
    private @NonNull VariableType variableType;
    
    private @NonNull ConstraintFileType constraintFileType;
    
    private boolean hasSourceLoactions;
    
    private boolean hasConstraintUsage;
    
    /**
     * Creates a descriptor with default values.
     */
    VariabilityModelDescriptor() {
        variableType = VariableType.BOOLEAN;
        constraintFileType = ConstraintFileType.UNSPECIFIED;
        hasSourceLoactions = false;
    }
    
    /**
     * Returns which {@link ConstraintFileType} the {@link VariabilityModel} has.
     * 
     * @return The {@link ConstraintFileType}.
     */
    public @NonNull ConstraintFileType getConstraintFileType() {
        return constraintFileType;
    }
    
    /**
     * Sets the {@link ConstraintFileType} of the {@link VariabilityModel}. This should only be called by the creator
     * of the {@link VariabilityModel}.
     * 
     * @param constraintFileType The new {@link ConstraintFileType}.
     */
    public void setConstraintFileType(@NonNull ConstraintFileType constraintFileType) {
        this.constraintFileType = constraintFileType;
    }
    
    /**
     * Returns the {@link VariableType} of the {@link VariabilityVariable}s in the {@link VariabilityModel}.
     * 
     * @return The {@link VariableType} of the model.
     */
    public @NonNull VariableType getVariableType() {
        return variableType;
    }
    
    /**
     * Sets the {@link VariableType} of the {@link VariabilityVariable}s in the {@link VariabilityModel}. This should
     * only be called by the creator of the {@link VariabilityModel}.
     * 
     * @param variableType The {@link VariableType} of the model.
     */
    public void setVariableType(@NonNull VariableType variableType) {
        this.variableType = variableType;
    }
    
    /**
     * Returns whether the {@link VariabilityVariable}s have {@link SourceLocation}s attached to them or not.
     * 
     * @return Whether the model provides {@link SourceLocation}s.
     * 
     * @see VariabilityVariable#getSourceLocations()
     */
    public boolean hasSourceLoactions() {
        return hasSourceLoactions;
    }
    
    /**
     * Sets whether the {@link VariabilityVariable}s have {@link SourceLocation}s attached to them or not. This should
     * only be called by the creator of the {@link VariabilityModel}.
     * 
     * @param hasSourceLoactions Whether the model provides {@link SourceLocation}s.
     */
    public void setHasSourceLoactions(boolean hasSourceLoactions) {
        this.hasSourceLoactions = hasSourceLoactions;
    }
    
    /**
     * Returns whether usage of {@link VariabilityVariable}s in their constraints is provided. 
     * 
     * @return Whether variable usage in constraints are provided.
     * 
     * @see VariabilityVariable#getVariablesUsedInConstraints()
     * @see VariabilityVariable#getUsedInConstraintsOfOtherVariables()
     */
    public boolean hasConstraintUsage() {
        return hasConstraintUsage;
    }
    
    /**
     * Sets whether usage of {@link VariabilityVariable}s in their constraints is provided. This should
     * only be called by the creator of the {@link VariabilityModel}.
     * 
     * @param hasConstraintUsage Whether variable usage in constraints are provided.
     */
    public void setHasConstraintUsage(boolean hasConstraintUsage) {
        this.hasConstraintUsage = hasConstraintUsage;
    }

}
