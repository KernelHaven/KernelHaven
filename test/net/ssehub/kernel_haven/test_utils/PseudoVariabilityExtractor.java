package net.ssehub.kernel_haven.test_utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.Attribute;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * A VariabilityModelExtractor for testing.
 * @author El-Sharkawy
 *
 */
@SuppressWarnings("null")
public class PseudoVariabilityExtractor extends AbstractVariabilityModelExtractor {

    private static VariabilityModel model;

    /**
     * Must be called before the Pipeline is instantiated.
     * @param dimacsFile A file containing a representation of the constraints of the
     *          variability model.
     * @param variables The variables, which are needed for the test.
     */
    public static void configure(File dimacsFile, VariabilityVariable... variables) {
        Set<VariabilityVariable> vars = new HashSet<VariabilityVariable>();
        for (VariabilityVariable var : variables) {
            vars.add(var);
        }
        model = new VariabilityModel(dimacsFile, vars);
    }
    
    /**
     * Adds the specified attributes to the descriptor of the variability model.
     * Must be called after {@link #configure(File, VariabilityVariable...)} as this method resets all attributes.
     * @param attributes The attributes to set for the variability model.
     */
    public static void setAttributes(@NonNull Attribute... attributes) {
        if (null != model) {
            VariabilityModelDescriptor descriptor = model.getDescriptor();
            for (Attribute attribute : attributes) {
                descriptor.addAttribute(attribute);
            }
        }
    }
    
    @Override
    protected void init(Configuration config) throws SetUpException {
    }

    @Override
    protected VariabilityModel runOnFile(File target) throws ExtractorException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return model;
    }

    @Override
    protected String getName() {
        return "PseudoVariabilityExtractor";
    }

}
