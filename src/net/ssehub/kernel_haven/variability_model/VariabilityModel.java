package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Representation of variability models.
 * 
 * @author Adam
 * @author Johannes
 * @author Moritz
 * @author Kevin
 */
public class VariabilityModel {

    private @NonNull VariabilityModelDescriptor descriptor;
    
    /**
     * A representation of the constraints inside the variability model. Never
     * null.
     */
    private @NonNull File constraintModel;

    /**
     * The variables defined by this variability model. Never null.
     * The key is the name of the variable for easier access.
     */
    private @NonNull Map<@NonNull String, VariabilityVariable> variables;

    /**
     * Creates a new variability model.
     * 
     * @param constraintModel
     *            A file containing a representation of the constraints of the
     *            variability model. For boolean models, this could be DIMACS.
     *            Must not be null.
     * @param variables
     *            The variables that are defined in the variability model. Must
     *            not be null.
     */
    public VariabilityModel(@NonNull File constraintModel,
            @NonNull Map<@NonNull String, VariabilityVariable> variables) {
        
        this.constraintModel = constraintModel;
        this.variables = variables;
        this.descriptor = new VariabilityModelDescriptor();
    }
    
    /**
     * Creates a new variability model.
     * 
     * @param constraintModel A file containing a representation of the constraints of the
     *          variability model. For boolean models, this could be DIMACS. Must not be null.
     * @param variables The variables that are defined in the variability model.
     *          Must not be null.
     */
    public VariabilityModel(@NonNull File constraintModel, @NonNull Set<VariabilityVariable> variables) {
        this.constraintModel = constraintModel;
        this.variables = new HashMap<>();
        for (VariabilityVariable var : variables) {
            this.variables.put(var.getName(), var);
        }
        this.descriptor = new VariabilityModelDescriptor();
    }

    /**
     * Returns the representation of the constraints inside the variability
     * model. For boolean models, this is most likely DIMACS.
     * 
     * @return The representation of the constraints inside the variability
     *         model. Never null.
     */
    public @NonNull File getConstraintModel() {
        return constraintModel;
    }

    /**
     * Returns the variables defined by this variability model.
     * 
     * @return The variables defined by this variability model. Never null.
     */
    public @NonNull Set<@NonNull VariabilityVariable> getVariables() {
        @SuppressWarnings("null")
        Set<@NonNull VariabilityVariable> result = new HashSet<>(variables.values());
        return result;
    }

    /**
     * Returns the variables defined by this variability model, as a mapping name -> variable.
     * @return The variables defined by this variability model. Never null.
     */
    public @NonNull Map<@NonNull String, VariabilityVariable> getVariableMap() {
        return variables; 
    }
    
    /**
     * Returns the descriptor for this model.
     * 
     * @return The {@link VariabilityModelDescriptor} for this model.
     */
    public VariabilityModelDescriptor getDescriptor() {
        return descriptor;
    }
    
    /**
     * Overrides the descriptor of this model. This should only be called by the deserializer.
     *  
     * @param descriptor The new descriptor.
     */
    void setDescriptor(@NonNull VariabilityModelDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
}
