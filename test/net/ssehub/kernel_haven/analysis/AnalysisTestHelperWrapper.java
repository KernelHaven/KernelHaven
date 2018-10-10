package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.TestAnalysisComponentProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A helper class to make {@link TestAnalysisComponentProvider} an internal helper component.
 * 
 * @author Adam
 *
 * @param <T> The output type of this {@link AnalysisComponent}.
 */
public abstract class AnalysisTestHelperWrapper<T> extends AnalysisComponent<T> {

    /**
     * Creates this {@link AnalysisComponent} with the given configuration.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisTestHelperWrapper(@NonNull Configuration config) {
        super(config);
    }

    @Override
    boolean isInternalHelperComponent() {
        return true;
    }
    
}
