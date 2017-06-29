package net.ssehub.kernel_haven.build_model;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.BuildExtractorConfiguration;

/**
 * A factory for creating build extractors. 
 * 
 * @author Adam
 * @author Alice
 *
 */
public interface IBuildExtractorFactory {

    /**
     * Creates a new build extractor.
     * 
     * @param config The configuration for this extractor run. 
     * @return IBuildModelExtractor for the given configuration.
     * @throws SetUpException If setting up the extractor fails.
     */
    public IBuildModelExtractor create(BuildExtractorConfiguration config) throws SetUpException;
}
