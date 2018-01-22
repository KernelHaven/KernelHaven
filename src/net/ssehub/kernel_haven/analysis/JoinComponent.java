package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A component to join multiple pipelines created by a {@link SplitComponent}. This must be the main analysis component.
 * 
 * @author Adam
 */
public class JoinComponent extends AnalysisComponent<Void> {

    private @NonNull AnalysisComponent<?> @NonNull [] inputs;
    
    /**
     * Creates a {@link JoinComponent} for the given input components.
     * 
     * @param config The global configuration.
     * @param inputs The input components.
     */
    public JoinComponent(@NonNull Configuration config, @NonNull AnalysisComponent<?> @NonNull ... inputs) {
        super(config);
        this.inputs = inputs;
    }
    
    /**
     * Returns the input components to join.
     * 
     * @return The input components.
     */
    @NonNull AnalysisComponent<?> @NonNull [] getInputs() {
        return inputs;
    }

    @Override
    protected void execute() {
    }

    @Override
    public @NonNull String getResultName() {
        return "PseudoJoinComponent";
    }

}
