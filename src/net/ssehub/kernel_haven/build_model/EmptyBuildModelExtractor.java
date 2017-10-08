package net.ssehub.kernel_haven.build_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;

/**
 * A simple extractor that returns an empty build model.
 *
 * @author Adam
 */
public class EmptyBuildModelExtractor extends AbstractBuildModelExtractor {

    @Override
    protected void init(Configuration config) throws SetUpException {
    }

    @Override
    protected BuildModel runOnFile(File target) throws ExtractorException {
        return new BuildModel();
    }

    @Override
    protected String getName() {
        return "EmptyBuildModelExtractor";
    }

}
