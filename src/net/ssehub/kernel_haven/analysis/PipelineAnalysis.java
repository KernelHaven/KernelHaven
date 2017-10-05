package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * An analysis that is a pipeline consisting of {@link AnalysisComponent}s.
 * 
 * @author Adam
 */
public abstract class PipelineAnalysis extends AbstractAnalysis {

    private StartingComponent<VariabilityModel> vmStarter;
    
    private StartingComponent<BuildModel> bmStarter;
    
    private StartingComponent<SourceFile> cmStarter;
    
    /**
     * Creates a new {@link PipelineAnalysis}.
     * 
     * @param config The global configuration.
     */
    public PipelineAnalysis(Configuration config) {
        super(config);
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the variability model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the variability model.
     */
    protected AnalysisComponent<VariabilityModel> getVmComponent() {
        return vmStarter;
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the build model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the build model.
     */
    protected AnalysisComponent<BuildModel> getBmComponent() {
        return bmStarter;
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the code model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the code model.
     */
    protected AnalysisComponent<SourceFile> getCmComponent() {
        return cmStarter;
    }
    
    /**
     * Creates the pipeline.
     * 
     * @return The "main" (i.e. the last) component of the pipeline.
     * 
     * @throws SetUpException If setting up the pipeline fails.
     */
    protected abstract AnalysisComponent<?> createPipeline() throws SetUpException;

    @Override
    public void run() {
        try {
            vmProvider.start();
            bmProvider.start();
            cmProvider.start();
            
            this.vmStarter = new StartingComponent<>(config, vmProvider, false);
            this.bmStarter = new StartingComponent<>(config, bmProvider, false);
            this.cmStarter = new StartingComponent<>(config, cmProvider, true);
        
            AnalysisComponent<?> mainComponent = createPipeline();
            
            Object result;
            while ((result = mainComponent.getNextResult()) != null) {
                // TODO: log result to file
                LOGGER.logInfo("Got analysis result: " + result.toString());
            }
            
        } catch (SetUpException e) {
            LOGGER.logException("Exception while setting up", e);
        }
    }
    
    /**
     * A starting component for the analysis pipeline. These get their data from the extractors.
     *  
     * @param <T> The type of result data that this produces.
     * 
     * @author Adam
     */
    private static class StartingComponent<T> extends AnalysisComponent<T> {

        private AbstractProvider<T, ?> provider;
        
        private boolean multiple;
        
        /**
         * Creates a new starting component.
         * 
         * @param config The global configuration.
         * @param provider The provider to get the data from.
         * @param multiple Whether the provider should be polled multiple times or just onece.
         */
        public StartingComponent(Configuration config, AbstractProvider<T, ?> provider, boolean multiple) {
            super(config);
            this.provider = provider;
            this.multiple = multiple;
        }

        @Override
        protected void execute() {
            if (multiple) {
                T data;
                while ((data = provider.getNextResult()) != null) {
                    addResult(data);
                }
                
                ExtractorException exc;
                while ((exc = provider.getNextException()) != null) {
                    LOGGER.logException("Got extractor exception", exc);
                }
            } else {
                T data = provider.getResult();
                if (data != null) {
                    addResult(data);
                }
                
                ExtractorException exc = provider.getException();
                if (exc != null) {
                    LOGGER.logException("Got extractor exception", exc);
                }
            }
            
        }
        
    }

}
