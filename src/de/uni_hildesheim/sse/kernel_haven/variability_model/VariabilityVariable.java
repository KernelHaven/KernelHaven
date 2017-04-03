package de.uni_hildesheim.sse.kernel_haven.variability_model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_hildesheim.sse.kernel_haven.util.FormatException;

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
    private String name;

    /**
     * The type of this variable. Never null.
     */
    private String type;

    /**
     * The number, that this variable has in the DIMACS representation of the
     * variability model. 0 if not set / used.
     */
    private int dimacsNumber;

    /**
     * Stores possible source-locations from which the variable might have been
     * derived. <code>null</code> until addLocation() is called.
     */
    private List<SourceLocation> sourceLocations;

    /**
     * Creates a new variable.
     * 
     * @param name
     *            The name of the new variable. Must not be null.
     * @param type
     *            The type of the new variable. Must not be null.
     */
    public VariabilityVariable(String name, String type) {
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
    public VariabilityVariable(String name, String type, int dimacsNumber) {
        this.name = name;
        this.type = type;
        this.dimacsNumber = dimacsNumber;
    }

    /**
     * Returns the name of the variable.
     * 
     * @return The name of the variable. Never null.
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a source location for this variablity variable.
     * 
     * @param location
     *            the location of the source linked to this variable
     */
    public void addLocation(SourceLocation location) {
        if (sourceLocations == null) {
            sourceLocations = new ArrayList<SourceLocation>(1);
        }
        sourceLocations.add(location);
    }

    /**
     * Returns a list of all source locations that correspond to this variable.
     * 
     * @return list of source locations. This is <code>null</code> if no source location was added.
     */
    public List<SourceLocation> getSourceLocations() {
        return sourceLocations;
    }

    /**
     * Returns the type of this variable.
     * 
     * @return The type. Never null.
     */
    public String getType() {
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
    public void getDimacsMapping(Map<Integer, String> mapping) {
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
     * Serializes this as a CSV line.
     * 
     * @return The parts of the CSV line.
     */
    public List<String> serializeCsv() {
        List<String> result = new ArrayList<>(5);

        result.add(name);
        result.add(type);
        result.add("" + dimacsNumber);

        String sourceLocations = "";
        List<SourceLocation> sourceLocationsList = this.getSourceLocations();

        if (sourceLocationsList != null) {
            for (int i = 0; i < sourceLocationsList.size(); i++) {
                sourceLocations += sourceLocationsList.get(i).getSource() + ">"
                        + sourceLocationsList.get(i).getLineNumber();
                if (i < sourceLocationsList.size() - 1) {
                    sourceLocations += "|";
                }
            }
        } else {
            sourceLocations = "null";
        }
        result.add(sourceLocations);
        return result;
    }

    @Override
    public String toString() {
        return "VariabilityVariable [name=" + name + ", type=" + type + ", dimacsNumber=" + dimacsNumber
                + ", sourceLocations="
                + (getSourceLocations() == null ? "null" : getSourceLocations().toString()) + "]";
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode() + new Integer(dimacsNumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof VariabilityVariable) {
            VariabilityVariable other = (VariabilityVariable) obj;
            result = other.name.equals(this.name) && other.type.equals(this.type)
                    && other.dimacsNumber == this.dimacsNumber;
            
            if (this.sourceLocations == null) {
                result &= other.sourceLocations == null;
            } else {
                result &= other.sourceLocations != null;
                if (result) {
                    result &= this.sourceLocations.equals(other.sourceLocations);
                }
            }
        }
        return result;
    }

    /**
     * Creates a {@link VariabilityVariable} from the given CSV.
     * 
     * @param csvParts
     *            The CSV that is converted into a {@link VariabilityVariable}.
     * @return The {@link VariabilityVariable} created by the CSV.
     * 
     * @throws FormatException
     *             If the CSV cannot be read into a variable.
     */
    public static VariabilityVariable createFromCsv(String[] csvParts) throws FormatException {
        if (csvParts.length < 4) {
            throw new FormatException("Invalid CSV");
        }

        try {
            VariabilityVariable varVariable = new VariabilityVariable(csvParts[0], csvParts[1],
                    Integer.parseInt(csvParts[2]));
            String sourceLocationsString = csvParts[3];

            if (!sourceLocationsString.equals("null")) {
                String[] sourceLocationsArray = sourceLocationsString.split("\\|");
                for (String sourceLocation : sourceLocationsArray) {
                    String[] parts = sourceLocation.split(">");
                    if (parts.length != 2) {
                        throw new FormatException("Invalid SourceLocation: " + sourceLocation);
                    }
                    varVariable.addLocation(new SourceLocation(new File(parts[0]), Integer.parseInt(parts[1])));
                }
            }

            return varVariable;
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
    }

}
