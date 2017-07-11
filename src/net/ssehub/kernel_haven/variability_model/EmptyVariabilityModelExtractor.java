package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;

/**
 * A simple variability model extractor which returns an empty variability model.
 *
 * @author Adam
 */
public class EmptyVariabilityModelExtractor extends AbstractVariabilityModelExtractor {

    @Override
    protected void init(VariabilityExtractorConfiguration config) throws SetUpException {
    }

    @Override
    protected VariabilityModel runOnFile(File target) throws ExtractorException {
        try {
            File constraintFile = File.createTempFile("empty_constraint_model", ".txt");
            return new VariabilityModel(constraintFile, new HashMap<>());
        } catch (IOException e) {
            throw new ExtractorException(e);
        }
    }

    @Override
    protected String getName() {
        return "EmptyVariabilityModelExtractor";
    }

}
