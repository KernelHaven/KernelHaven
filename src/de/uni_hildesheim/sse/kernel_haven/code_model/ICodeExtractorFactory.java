package de.uni_hildesheim.sse.kernel_haven.code_model;

import de.uni_hildesheim.sse.kernel_haven.SetUpException;
import de.uni_hildesheim.sse.kernel_haven.config.CodeExtractorConfiguration;

/**
 * A factory for creating code extractors. 
 * 
 * @author Adam
 * @author Alice
 *
 */
public interface ICodeExtractorFactory {

    /**
     * Creates a new code extractor.
     * 
     * @param config The configuration for this extractor run. 
     * @return ICodeModelExtractor for the given configuration.
     * @throws SetUpException If setting up the extractor fails.
     */
    public ICodeModelExtractor create(CodeExtractorConfiguration config) throws SetUpException;
}
