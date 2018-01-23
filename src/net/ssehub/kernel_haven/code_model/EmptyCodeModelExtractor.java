package net.ssehub.kernel_haven.code_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A simple code model extractor that returns empty source files for each target to run on.
 *
 * @author Adam
 */
public class EmptyCodeModelExtractor extends AbstractCodeModelExtractor {

    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
    }

    @Override
    protected @Nullable SourceFile runOnFile(@NonNull File target) throws ExtractorException {
        return new SourceFile(target);
    }

    @Override
    protected @NonNull String getName() {
        return "EmptyCodeModelExtractor";
    }

}
