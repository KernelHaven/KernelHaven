package net.ssehub.kernel_haven.variability_model;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;

/**
 * A factory for creating variability extractors. 
 * 
 * @author Adam
 * @author Alice
 *
 */
public interface IVariabilityExtractorFactory {

    /**
     * Creates a new variability extractor.
     * 
     * @param config The configuration for this extractor run. 
     * @return IVariabilityModelExtractor for the given configuration.
     * @throws SetUpException If setting up the extractor fails.
     */
    public IVariabilityModelExtractor create(VariabilityExtractorConfiguration config) throws SetUpException;
}
