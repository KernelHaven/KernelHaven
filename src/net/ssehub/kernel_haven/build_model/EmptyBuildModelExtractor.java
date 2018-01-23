package net.ssehub.kernel_haven.build_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A simple extractor that returns an empty build model.
 *
 * @author Adam
 */
public class EmptyBuildModelExtractor extends AbstractBuildModelExtractor {

    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
    }

    @Override
    protected @NonNull BuildModel runOnFile(@NonNull File target) throws ExtractorException {
        return new BuildModel();
    }

    @Override
    protected @NonNull String getName() {
        return "EmptyBuildModelExtractor";
    }

}
