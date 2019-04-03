/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    
    /**
     * Other attributes that define the capabilities of the {@link VariabilityModel}.
     */
    public static enum Attribute {
        
        /**
         * {@link VariabilityVariable}s have {@link SourceLocation}s attached to them.
         * 
         * @see VariabilityVariable#getSourceLocations()
         */
        SOURCE_LOCATIONS,
        
        /**
         * {@link VariabilityVariable}s have information about variables used in their conditions attached to them.
         * 
         * @see VariabilityVariable#getVariablesUsedInConstraints()
         * @see VariabilityVariable#getUsedInConstraintsOfOtherVariables()
         */
        CONSTRAINT_USAGE,
        
        /**
         * {@link VariabilityVariable}s are instances of {@link HierarchicalVariable}.
         */
        HIERARCHICAL,
        
    }
    
    private @NonNull VariableType variableType;
    
    private @NonNull ConstraintFileType constraintFileType;
    
    private @NonNull Set<@NonNull Attribute> attributes;
    
    /**
     * Creates a descriptor with default values.
     */
    VariabilityModelDescriptor() {
        variableType = VariableType.BOOLEAN;
        constraintFileType = ConstraintFileType.UNSPECIFIED;
        attributes = new HashSet<>();
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
     * Checks whether the {@link VariabilityModel} has the given attribute.
     * 
     * @param attribute The attribute to be checked.
     * 
     * @return Whether the {@link VariabilityModel} has the given attribute.
     */
    public boolean hasAttribute(@NonNull Attribute attribute) {
        return attributes.contains(attribute);
    }
    
    /**
     * Sets that the {@link VariabilityModel} has the given attribute. This should only be called by the creator of the
     * {@link VariabilityModel}.
     * 
     * @param attribute The attribute to be set for the {@link VariabilityModel}.
     */
    public void addAttribute(@NonNull Attribute attribute) {
        this.attributes.add(attribute);
    }
    
    /**
     * Removes the given attribute from the {@link VariabilityModel}. This should only be called by the creator of the
     * {@link VariabilityModel}.
     * 
     * @param attribute The attribute to be set for the {@link VariabilityModel}.
     */
    public void removeAttribute(@NonNull Attribute attribute) {
        this.attributes.remove(attribute);
    }
    
    /**
     * Returns the set of all {@link Attribute}s.
     * 
     * @return All {@link Attribute}s of the {@link VariabilityModel}.
     */
    public @NonNull Set<@NonNull Attribute> getAttributes() {
        return notNull(Collections.unmodifiableSet(attributes));
    }
    
}
