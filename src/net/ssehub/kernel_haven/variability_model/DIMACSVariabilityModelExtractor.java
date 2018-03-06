package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.NullHelpers;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.ConstraintFileType;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.VariableType;

/**
 * {@link VariabilityModel} extractor, which operators only on a single
 * <a href"http://www.satcompetition.org/2009/format-benchmarks2009.html">DIMAC</a> file.
 * @author El-Sharkawy
 *
 */
public class DIMACSVariabilityModelExtractor extends AbstractVariabilityModelExtractor {

    public static final @NonNull String UNKNOWN_VARIABE_TYPE = "unknown";
    
    private File dimacsfile;
    
    @SuppressWarnings("unused")
    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
        dimacsfile = config.getValue(DefaultSettings.VARIABILITY_INPUT_FILE);
        if (dimacsfile == null) {
            throw new SetUpException(DefaultSettings.VARIABILITY_INPUT_FILE.getKey() + " was not specified, it must "
                + "point to input DIMACS file.");
        }
        if (!dimacsfile.exists()) {
            throw new SetUpException(DefaultSettings.VARIABILITY_INPUT_FILE.getKey() + " = "
                + dimacsfile.getAbsolutePath() + " does not exist.");
        }
    }

    @Override
    protected @Nullable VariabilityModel runOnFile(File target) throws ExtractorException {
        Map<String, VariabilityVariable> variables = new HashMap<>();
        try {
            Files.lines(dimacsfile.toPath())
                .filter(l -> l != null && l.startsWith("c "))           // Only comment lines
                .map(l -> l.split("\\s"))                               // Create array, split whitespace characters
                .map(a -> parseLine(a))                                 // Parse lines
                .filter(Objects::nonNull)                               // Only parsed variables
                .forEachOrdered(v -> variables.put(v.getName(), v));    // Store results
        } catch (IOException e) {
            throw new ExtractorException("Could not parse " + dimacsfile.getAbsolutePath());
        }
        
        // Unfortunately, the map cannot be annotate, otherwise Jacoco will crash.
        @SuppressWarnings("null")
        VariabilityModel result = new VariabilityModel(NullHelpers.notNull(dimacsfile), variables);
        VariabilityModelDescriptor descriptor = result.getDescriptor();
        descriptor.setVariableType(VariableType.BOOLEAN);
        descriptor.setConstraintFileType(ConstraintFileType.DIMACS);
        
        return result;
    }
    
    /**
     * Converts a single comment line of the DIMACS file into a newly created {@link VariabilityVariable}.
     * The extractor ensures, that only non <tt>null</tt> comments will be passed to this method.
     * @param dimacsCommentLine The elements of the comment line, already white space separated, first element is
     *     the comment character <tt>c</tt>.
     * @return The parsed variable.
     */
    protected @Nullable VariabilityVariable parseLine(String[] dimacsCommentLine) {
        VariabilityVariable parsedVariable =  null;
        if (null != dimacsCommentLine && dimacsCommentLine.length > 2) {
            
            // DIMACS ID / Mapping
            int varID = -1;
            try {
                varID = Integer.parseInt(dimacsCommentLine[1]);
            } catch (NumberFormatException exc) {
                String line = String.join(", ", dimacsCommentLine);
                Logger.get().logWarning("Could not parse " + line + ", expected an Integer at index 1");
            }
            
            // Variable name
            String varName = NullHelpers.notNull(dimacsCommentLine[2]);
            
            if (dimacsCommentLine.length > 3) {
                String type = NullHelpers.notNull(dimacsCommentLine[3]);
                parsedVariable = new VariabilityVariable(varName, type, varID);
            } else {
                parsedVariable = new VariabilityVariable(varName, UNKNOWN_VARIABE_TYPE, varID);
            }
        }
//        
        return parsedVariable;
    }

    @Override
    protected @NonNull String getName() {
        return NullHelpers.notNull(this.getClass().getSimpleName());
    }


}
