package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Representation of variability models.
 * 
 * @author Adam
 * @author Johannes
 * @author Moritz
 * @author Kevin
 */
public class VariabilityModel {

    /**
     * A representation of the constraints inside the variability model. Never
     * null.
     */
    private File constraintModel;

    /**
     * The variables defined by this variability model. Never null.
     * The key is the name of the variable for easier access.
     */
    private Map<String, VariabilityVariable> variables;

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
    public VariabilityModel(File constraintModel, Map<String, VariabilityVariable> variables) {
        this.constraintModel = constraintModel;
        this.variables = variables;
    }
    
    /**
     * Creates a new variability model.
     * 
     * @param constraintModel A file containing a representation of the constraints of the
     *          variability model. For boolean models, this could be DIMACS. Must not be null.
     * @param variables The variables that are defined in the variability model.
     *          Must not be null.
     */
    public VariabilityModel(File constraintModel, Set<VariabilityVariable> variables) {
        this.constraintModel = constraintModel;
        this.variables = new HashMap<>();
        for (VariabilityVariable var : variables) {
            this.variables.put(var.getName(), var);
        }
    }

    /**
     * Returns the representation of the constraints inside the variability
     * model. For boolean models, this is most likely DIMACS.
     * 
     * @return The representation of the constraints inside the variability
     *         model. Never null.
     */
    public File getConstraintModel() {
        return constraintModel;
    }

    /**
     * Returns the variables defined by this variability model.
     * 
     * @return The variables defined by this variability model. Never null.
     */
    public Set<VariabilityVariable> getVariables() {
        return new HashSet<>(variables.values());
    }

    /**
     * Returns the variables defined by this variability model, as a mapping name -> variable.
     * @return The variables defined by this variability model. Never null.
     */
    public Map<String, VariabilityVariable> getVariableMap() {
        return variables; 
    }
    
}
