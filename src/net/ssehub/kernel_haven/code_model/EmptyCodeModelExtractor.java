package net.ssehub.kernel_haven.code_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;

/**
 * A simple code model extractor that returns empty source files for each target to run on.
 *
 * @author Adam
 */
public class EmptyCodeModelExtractor extends AbstractCodeModelExtractor {

    @Override
    protected void init(CodeExtractorConfiguration config) throws SetUpException {
    }

    @Override
    protected SourceFile runOnFile(File target) throws ExtractorException {
        return new SourceFile(target);
    }

    @Override
    protected String getName() {
        return "EmptyCodeModelExtractor";
    }

}
