package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Serializes and deserializes a {@link VariabilityVariable} to CSV. Implementing classes should not keep any internal
 * state, because instances may be re-used for many {@link VariabilityModel}s and may be used in parallel. Sub-classes
 * can extend this class and override {@link #serializeImpl(VariabilityVariable)}, {@link #deserializeImpl(String[])}
 * and {@link #checkLength(String[])}.
 * 
 * @author Adam
 */
public class VariabilityVariableSerializer {
    
    /**
     * The number of fields that the default implementation produces for a {@link VariabilityVariable}.
     */
    protected static final int DEFAULT_SIZE = 4;

    /**
     * Serializes the given {@link VariabilityVariable} to CSV line.
     * 
     * @param variable The variability variable to serialize.
     * 
     * @return The variability variable serialized as a CSV line.
     */
    public final @Nullable Object @NonNull [] serialize(@NonNull VariabilityVariable variable) {
        return notNull(serializeImpl(variable).toArray(new @Nullable Object[0]));
    }
    
    /**
     * Serializes the given {@link VariabilityVariable} into CSV. This can be re-used by sub-classes if they want to
     * only extend the existing CSV format.
     * 
     * @param variable The variable to serialize. This can safely be cast into the variable type that this serializer
     *      was registered to in the {@link VariabilityVariableSerializerFactory}.
     *      
     * @return The list of CSV parts.
     */
    protected @NonNull List<@NonNull String> serializeImpl(@NonNull VariabilityVariable variable) {
        List<@NonNull String> result = new ArrayList<>(5);

        result.add(variable.getName());
        result.add(variable.getType());
        result.add(notNull(String.valueOf(variable.getDimacsNumber())));

        String sourceLocations = "";
        List<SourceLocation> sourceLocationsList = variable.getSourceLocations();

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

    /**
     * Deserializes the given CSV line back into a {@link VariabilityVariable}.
     * 
     * @param csv The CSV line to deserialize.
     * 
     * @return The deserialized {@link VariabilityVariable}.
     * 
     * @throws FormatException If the CSV format is invalid.
     */
    public final @NonNull VariabilityVariable deserialize(@NonNull String @NonNull [] csv) throws FormatException {
        checkLength(csv);
        return deserializeImpl(csv);
    }
    
    /**
     * Deserializes the given CSV parts into a {@link VariabilityVariable}. This can be re-used by sub-classes if they
     * want to only extend the existing CSV format.
     * 
     * @param csv The CSV parts.
     * 
     * @return The deserialized {@link VariabilityVariable}.
     * 
     * @throws FormatException If the CSV format is invalid.
     */
    protected @NonNull VariabilityVariable deserializeImpl(@NonNull String @NonNull [] csv) throws FormatException {
        try {
            VariabilityVariable varVariable = new VariabilityVariable(csv[0], csv[1],
                    Integer.parseInt(csv[2]));
            String sourceLocationsString = csv[3];

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
    
    /**
     * Checks the length of the given CSV line. Throws an exception if an invalid length is detected. The default
     * method checks for <code>csv.length != {@link #DEFAULT_SIZE}</code>, sub-classes can override this, if they
     * extend the CSV by more fields.
     *  
     * @param csv The CSV line to check the length for.
     * 
     * @throws FormatException If the CSV line has an unexpected amount of fields.
     */
    protected void checkLength(@NonNull String @NonNull [] csv) throws FormatException {
        if (csv.length != DEFAULT_SIZE) {
            throw new FormatException("Invalid CSV");
        }
    }

}
