package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.config.Configuration;

/**
 * A component to join multiple pipelines created by a {@link SplitComponent}. This must be the main analysis component.
 * 
 * @author Adam
 */
public class JoinComponent extends AnalysisComponent<Void> {

    private AnalysisComponent<?>[] inputs;
    
    /**
     * Creates a {@link JoinComponent} for the given input components.
     * 
     * @param config The global configuration.
     * @param inputs The input components.
     */
    public JoinComponent(Configuration config, AnalysisComponent<?>... inputs) {
        super(config);
        this.inputs = inputs;
    }
    
    /**
     * Returns the input components to join.
     * 
     * @return The input components.
     */
    AnalysisComponent<?>[] getInputs() {
        return inputs;
    }

    @Override
    protected void execute() {
    }

    @Override
    public String getResultName() {
        return "PseudoJoinComponent";
    }

}
