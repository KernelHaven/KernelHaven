package net.ssehub.kernel_haven.test_utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * A VariabilityModelExtractor for testing.
 * @author El-Sharkawy
 *
 */
public class PseudoVariabilityExtractor extends AbstractVariabilityModelExtractor {

    private static File dimacsFile;
    private static Set<VariabilityVariable> vars;
    
    /**
     * Must be called before the Pipeline is instantiated.
     * @param dimacsFile A file containing a representation of the constraints of the
     *          variability model.
     * @param variables The variables, which are needed for the test.
     */
    public static void configure(File dimacsFile, VariabilityVariable... variables) {
        PseudoVariabilityExtractor.dimacsFile = dimacsFile;
        vars = new HashSet<VariabilityVariable>();
        for (VariabilityVariable var : variables) {
            vars.add(var);
        }
    }
    
    @Override
    protected void init(VariabilityExtractorConfiguration config) throws SetUpException {
    }

    @Override
    protected VariabilityModel runOnFile(File target) throws ExtractorException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        VariabilityModel vm = new VariabilityModel(dimacsFile, vars);
        
        return vm;
    }

    @Override
    protected String getName() {
        return "PseudoVariabilityExtractor";
    }

}
