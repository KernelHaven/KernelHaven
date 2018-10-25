package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a single variable from the variability model.
 * <p>
 * The type of the variable is represented by a simple string. This allows
 * extractors for specific product lines to create the data types required for
 * the specific product line they are parsing. For example, in Kconfig the
 * possible types would be "bool", "tristate", etc.
 * </p>
 * <p>
 * If some data types require additional data (e.g. compound types), then the
 * extractor can create a new type that inherits from this class. The analysis
 * can then cast this generic class into the specific sub-type, if needed.
 * </p>
 * <p>
 * If this variable is linked to a specific DIMACS representation, then the
 * dimacsNumber attribute is set to a non 0 value. This attribute then is the
 * number that this variable is represented by.
 * </p>
 * <p>
 * <b>Serialization:</b> In order for the serialization mechanism to work, every sub-class needs a constructor
 * with two String parameters (name and type, like {@link #VariabilityVariable(String, String)}). Additionally,
 * sub-classes may override {@link #toJson()} and {@link #setJsonData(JsonObject, Map)} to store
 * additional data during serialization.
 * </p>
 * 
 * @author Adam
 * @author Johannes
 * @author Marvin
 * @author Moritz
 */
public class VariabilityVariable {

    /**
     * The name of the variable. Never null.
     */
    private @NonNull String name;

    /**
     * The type of this variable. Never null.
     */
    private @NonNull String type;

    /**
     * The number, that this variable has in the DIMACS representation of the
     * variability model. 0 if not set / used.
     */
    private int dimacsNumber;

    /**
     * Stores possible source-locations from which the variable might have been
     * derived. <code>null</code> until addLocation() is called.
     */
    private @Nullable List<@NonNull SourceLocation> sourceLocations;
    
    private @Nullable Set<@NonNull VariabilityVariable> variablesUsedInConstraints;
    
    private @Nullable Set<@NonNull VariabilityVariable> usedInConstraintsOfOtherVariables;

    /**
     * Creates a new variable.
     * 
     * @param name
     *            The name of the new variable. Must not be null.
     * @param type
     *            The type of the new variable. Must not be null.
     */
    public VariabilityVariable(@NonNull String name, @NonNull String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Creates a new variable.
     * 
     * @param name
     *            The name of the new variable. Must not be null.
     * @param type
     *            The type of the new variable. Must not be null.
     * @param dimacsNumber
     *            The number that this variable has in the DIMACS representation
     *            of the variability model.
     */
    public VariabilityVariable(@NonNull String name, @NonNull String type, int dimacsNumber) {
        this.name = name;
        this.type = type;
        this.dimacsNumber = dimacsNumber;
    }

    /**
     * Returns the name of the variable.
     * 
     * @return The name of the variable. Never null.
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Adds a source location for this variability variable.
     * 
     * @param location
     *            the location of the source linked to this variable
     */
    public void addLocation(@NonNull SourceLocation location) {
        List<@NonNull SourceLocation> sourceLocations = this.sourceLocations;
        if (sourceLocations == null) {
            sourceLocations = new ArrayList<@NonNull SourceLocation>(1);
            this.sourceLocations = sourceLocations;
        }
        sourceLocations.add(location);
    }

    /**
     * Returns a list of all source locations that correspond to this variable.
     * 
     * @return list of source locations. This is <code>null</code> if no source location was added.
     */
    public @Nullable List<@NonNull SourceLocation> getSourceLocations() {
        return sourceLocations;
    }

    /**
     * Returns the type of this variable.
     * 
     * @return The type. Never null.
     */
    public @NonNull String getType() {
        return type;
    }

    /**
     * Returns the number that this variable has in the DIMACS representation of
     * the variability model. This is 0, if this variable is not associated with
     * a specific DIMACS representation.
     * 
     * @return The DIMACS number.
     */
    public int getDimacsNumber() {
        return dimacsNumber;
    }

    /**
     * Adds the DIMACS number variable name mapping to the given mapping map.
     * More specialized {@link VariabilityVariable}s (like tristates) may
     * overwrite this method if they are represented by more than one DIMACS
     * number.
     * 
     * @param mapping
     *            The mapping to add to.
     */
    public void getDimacsMapping(@NonNull Map<Integer, String> mapping) {
        mapping.put(getDimacsNumber(), getName());
    }

    /**
     * Sets the number that this variable has in the DIMACS representation of
     * the variability model. Use 0, if this variable is not associated with a
     * specific DIMACS representation.
     * 
     * @param dimacsNumber
     *            The DIMACS number.
     */
    public void setDimacsNumber(int dimacsNumber) {
        this.dimacsNumber = dimacsNumber;
    }

    /**
     * Sets which other variability variables are used in the constraints of this variable. Should be called by the
     * extractor that creates this variable.
     * 
     * @param variablesUsedInConstraints The other variables that are used.
     */
    public void setVariablesUsedInConstraints(@NonNull Set<@NonNull VariabilityVariable> variablesUsedInConstraints) {
        this.variablesUsedInConstraints = variablesUsedInConstraints;
    }
    
    /**
     * Returns which other variables this variable uses in its constraints. <code>null</code> if the extractor did not
     * provide this information.
     * 
     * @return The other variables used in the constraints.
     */
    public @Nullable Set<@NonNull VariabilityVariable> getVariablesUsedInConstraints() {
        return variablesUsedInConstraints;
    }
    
    /**
     * Sets which other variability variables have conditions that use this variable. Should be called by the
     * extractor that creates this variable.
     * 
     * @param usedInConstraintsOfOtherVariables Other variables that have conditions that use this variable.
     */
    public void setUsedInConstraintsOfOtherVariables(
            @NonNull Set<@NonNull VariabilityVariable> usedInConstraintsOfOtherVariables) {
        
        this.usedInConstraintsOfOtherVariables = usedInConstraintsOfOtherVariables;
    }
    
    /**
     * Returns which other variability variables have conditions that use this variable. <code>null</code> if the
     * extractor did not provide this information.
     * 
     * @return Other variables that have conditions that use this variable.
     */
    public @Nullable Set<@NonNull VariabilityVariable> getUsedInConstraintsOfOtherVariables() {
        return usedInConstraintsOfOtherVariables;
    }
    
    /**
     * Converts this variable into a JSON object. Sub-classes may overwrite this method to add their own data, but
     * should always call the super method.
     * 
     * @return A JSON representation of this object.
     */
    protected @NonNull JsonObject toJson() {
        JsonObject result = new JsonObject();
        
        result.putElement("name", new JsonString(name));
        result.putElement("type", new JsonString(type));
        result.putElement("dimacsNumber", new JsonNumber(dimacsNumber));
        
        List<@NonNull SourceLocation> sourceLocations = this.sourceLocations;
        if (sourceLocations != null) {
            JsonList sls = new JsonList();
            for (SourceLocation sl : sourceLocations) {
                sls.addElement(sl.toJson());
            }
            result.putElement("sourceLocations", sls);
        }
        
        Set<@NonNull VariabilityVariable> variablesUsedInConstraints = this.variablesUsedInConstraints;
        if (variablesUsedInConstraints != null) {
            JsonList vars = new JsonList();
            variablesUsedInConstraints.stream()
                    .map((var) -> var.getName())
                    .sorted()
                    .forEach((var) -> vars.addElement(new JsonString(var)));
            result.putElement("references", vars);
        }
        
        Set<@NonNull VariabilityVariable> usedInConstraintsOfOtherVariables = this.usedInConstraintsOfOtherVariables;
        if (usedInConstraintsOfOtherVariables != null) {
            JsonList vars = new JsonList();
            usedInConstraintsOfOtherVariables.stream()
                    .map((var) -> var.getName())
                    .sorted()
                    .forEach((var) -> vars.addElement(new JsonString(var)));
            result.putElement("referenced-by", vars);
        }
        
        return result;
    }
    
    /**
     * Sets the JSON data during de-serialization. This is called after all variables are instantiated, so that
     * variable name reference can be resolved. Sub-classes may overwrite this method, but should always call the
     * super method.
     * 
     * @param data The JSON data to read.
     * @param vars All variables of the variability model. Maps variable names to variables.
     * 
     * @throws FormatException If the JSON data does not have the correct structure.
     */
    protected void setJsonData(@NonNull JsonObject data, Map<@NonNull String, VariabilityVariable> vars)
            throws FormatException {
        
        // name and type are already set in constructor
        
        this.dimacsNumber = data.getInt("dimacsNumber");
        
        if (data.getElement("sourceLocations") != null) {
            for (JsonElement element : data.getList("sourceLocations")) {
                if (!(element instanceof JsonObject)) {
                    throw new FormatException("Expected JsonObject, but got " + element.getClass().getSimpleName());
                }
                
                addLocation(SourceLocation.fromJson((JsonObject) element));
            }
        }
        
        if (data.getElement("references") != null) {
            Set<@NonNull VariabilityVariable> result = new HashSet<>();
            for (JsonElement element : data.getList("references")) {
                if (!(element instanceof JsonString)) {
                    throw new FormatException("Expected JsonString, but got " + element.getClass().getSimpleName());
                }
                
                String varName = ((JsonString) element).getValue();
                VariabilityVariable var = vars.get(varName);
                if (var == null) {
                    throw new FormatException("Unknown variable " + varName);
                }
                result.add(var);
            }
            setVariablesUsedInConstraints(result);
        }
        
        if (data.getElement("referenced-by") != null) {
            Set<@NonNull VariabilityVariable> result = new HashSet<>();
            for (JsonElement element : data.getList("referenced-by")) {
                if (!(element instanceof JsonString)) {
                    throw new FormatException("Expected JsonString, but got " + element.getClass().getSimpleName());
                }

                String varName = ((JsonString) element).getValue();
                VariabilityVariable var = vars.get(varName);
                if (var == null) {
                    throw new FormatException("Unknown variable " + varName);
                }
                result.add(var);
            }
            setUsedInConstraintsOfOtherVariables(result);
        }
    }
    
    /**
     * Sets the (extra) data that has previously been serialized with {@link #getSerializationData()}. Overriding
     * methods should read and <b>remove</b> their data from the beginning of the list, and then call the super method. 
     * 
     * @param data The data to deserialize.
     * @param variables All variability variables of the final model. Do not add to this map.
     *      (These may not yet be fully initialized.)
     * 
     * @throws FormatException If the deserialization data is faulty.
     */
    @Deprecated
    protected void setSerializationData(@NonNull List<@NonNull String> data,
            @NonNull Map<@NonNull String, VariabilityVariable> variables) throws FormatException {
        
        readSerializedSourceLocations(data);
        readSerializedVariablesUsedInConstraints(data, variables);
        readSerializedUsedInConstraintsOfOtherVariables(data, variables);
    }
    
    /**
     * Reads the serialized {@link SourceLocation}s from the beginning of the given list.
     * 
     * @param data The data list.
     * 
     * @throws FormatException If the data is malformed.
     */
    private void readSerializedSourceLocations(List<@NonNull String> data) throws FormatException {
        
        if (data.isEmpty()) {
            throw new FormatException("Expected at least 1 more element");
        }
        
        String sizeStr = notNull(data.remove(0));
        if (!sizeStr.equals("null")) {
            
            int size = Integer.parseInt(sizeStr);
            if (data.size() < size) {
                throw new FormatException("Expected at least " + size + " more elemnts");
            }
            
            for (int i = 0; i < size; i++) {
                String sl = notNull(data.remove(0));
                String[] parts = sl.split(":");
                if (parts.length != 2) {
                    throw new FormatException("Invalid code location: " + sl);
                }
                
                int line;
                try {
                    line = Integer.parseInt(parts[1]);
                    
                } catch (NumberFormatException e) {
                    throw new FormatException(e);
                }
                
                addLocation(new SourceLocation(new File(parts[0]), line));
            }
        }
    }

    /**
     * Reads the serialized list of variables used in constraints from the beginning of the given list.
     * 
     * @param data The data list.
     * @param variables The map of all existing variables.
     * 
     * @throws FormatException If the data is malformed.
     */
    private void readSerializedVariablesUsedInConstraints(List<@NonNull String> data,
            Map<@NonNull String, VariabilityVariable> variables) throws FormatException {
        
        if (data.isEmpty()) {
            throw new FormatException("Expected at least 1 more element");
        }
        
        String sizeStr = notNull(data.remove(0));
        if (!sizeStr.equals("null")) {
            
            int size = Integer.parseInt(sizeStr);
            if (data.size() < size) {
                throw new FormatException("Expected at least " + size + " more elemnts");
            }
            
            Set<@NonNull VariabilityVariable> result = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                String varStr = notNull(data.remove(0));
                VariabilityVariable var = variables.get(varStr);
                
                if (var == null) {
                    throw new FormatException("Unknown variable name " + varStr);
                }
                
                result.add(var);
            }
            
            setVariablesUsedInConstraints(result);
        }
    }
    
    /**
     * Reads the serialized list of used in constraints of other variables from the beginning of the given list.
     * 
     * @param data The data list.
     * @param variables The map of all existing variables.
     * 
     * @throws FormatException If the data is malformed.
     */
    private void readSerializedUsedInConstraintsOfOtherVariables(List<@NonNull String> data,
            Map<@NonNull String, VariabilityVariable> variables) throws FormatException {
        if (data.isEmpty()) {
            throw new FormatException("Expected at least 1 more element");
        }
        String sizeStr = notNull(data.remove(0));
        if (!sizeStr.equals("null")) {
            
            int size = Integer.parseInt(sizeStr);
            if (data.size() < size) {
                throw new FormatException("Expected at least " + size + " more elemnts");
            }
            
            Set<@NonNull VariabilityVariable> result = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                String varStr = notNull(data.remove(0));
                VariabilityVariable var = variables.get(varStr);
                
                if (var == null) {
                    throw new FormatException("Unknown variable name " + varStr);
                }
                
                result.add(var);
            }
            
            setUsedInConstraintsOfOtherVariables(result);
        }
    }

    /**
     * Creates a list of (extra) data that needs to be serialized. On deserialization, this will be passed to
     * {@link #setSerializationDat(List)}. Overriding methods should always call the super method, and <b>prepend</b>
     * their data to the returned list.
     * 
     * @return A list of extra data to serialize.
     */
    @Deprecated
    protected @NonNull List<@NonNull String> getSerializationData() {
        
        List<@NonNull String> result = new LinkedList<>();
        
        // sourceLocations
        List<@NonNull SourceLocation> sourceLocations = this.sourceLocations;
        if (sourceLocations != null) {
            result.add(notNull(String.valueOf(sourceLocations.size())));
            
            for (SourceLocation sl : sourceLocations) {
                result.add(sl.getSource().getPath() + ":" + sl.getLineNumber());
            }
            
        } else {
            result.add("null");
        }
        
        // variablesUsedInConstraints
        Set<@NonNull VariabilityVariable> variablesUsedInConstraints = this.variablesUsedInConstraints;
        if (variablesUsedInConstraints != null) {
            result.add(notNull(String.valueOf(variablesUsedInConstraints.size())));
            
            for (VariabilityVariable var : variablesUsedInConstraints) {
                result.add(var.getName());
            }
            
        } else {
            result.add("null");
        }
        
        // usedInConstraintsOfOtherVariables
        Set<@NonNull VariabilityVariable> usedInConstraintsOfOtherVariables = this.usedInConstraintsOfOtherVariables;
        if (usedInConstraintsOfOtherVariables != null) {
            result.add(notNull(String.valueOf(usedInConstraintsOfOtherVariables.size())));
            
            for (VariabilityVariable var : usedInConstraintsOfOtherVariables) {
                result.add(var.getName());
            }
            
        } else {
            result.add("null");
        }
        
        return result;
    }
    
    @Override
    public @NonNull String toString() {
        List<@NonNull SourceLocation> sourceLocations = this.sourceLocations;
        return "VariabilityVariable [name=" + name + ", type=" + type + ", dimacsNumber=" + dimacsNumber
                + ", sourceLocations="
                + (sourceLocations == null ? "null" : sourceLocations.toString()) + "]";
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode() + new Integer(dimacsNumber).hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;
        if (obj instanceof VariabilityVariable) {
            VariabilityVariable other = (VariabilityVariable) obj;
            result = other.name.equals(this.name) && other.type.equals(this.type)
                    && other.dimacsNumber == this.dimacsNumber;
            
            List<@NonNull SourceLocation> sourceLocations = this.sourceLocations;
            if (sourceLocations == null) {
                result &= other.sourceLocations == null;
            } else {
                result &= other.sourceLocations != null;
                if (result) {
                    result &= sourceLocations.equals(other.sourceLocations);
                }
            }
        }
        return result;
    }

}
