package net.ssehub.kernel_haven.analysis;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Timestamp;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * An analysis that is a pipeline consisting of {@link AnalysisComponent}s.
 * 
 * @author Adam
 */
public abstract class PipelineAnalysis extends AbstractAnalysis {

    private ExtractorDataDuplicator<VariabilityModel> vmStarter;
    
    private ExtractorDataDuplicator<BuildModel> bmStarter;
    
    private ExtractorDataDuplicator<SourceFile> cmStarter;
    
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
        return vmStarter.createNewStartingComponent(config);
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the build model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the build model.
     */
    protected AnalysisComponent<BuildModel> getBmComponent() {
        return bmStarter.createNewStartingComponent(config);
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the code model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the code model.
     */
    protected AnalysisComponent<SourceFile> getCmComponent() {
        return cmStarter.createNewStartingComponent(config);
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
            vmStarter = new ExtractorDataDuplicator<>(vmProvider, false);
            bmStarter = new ExtractorDataDuplicator<>(bmProvider, false);
            cmStarter = new ExtractorDataDuplicator<>(cmProvider, true);
        
            AnalysisComponent<?> mainComponent = createPipeline();
            
            vmProvider.start();
            bmProvider.start();
            cmProvider.start();
            
            vmStarter.start();
            bmStarter.start();
            cmStarter.start();
            
            PrintStream out = createResultStream(
                    Timestamp.INSTANCE.getFilename(mainComponent.getClass().getSimpleName() + "_result", "txt"));
            
            Object result;
            while ((result = mainComponent.getNextResult()) != null) {
                LOGGER.logInfo("Got analysis result: " + result.toString());
                // TODO: log result to file
                out.println(result.toString());
                out.flush();
            }
            
            out.close();
            
        } catch (SetUpException e) {
            LOGGER.logException("Exception while setting up", e);
        }
    }
    
    /**
     * A class for duplicating the extractor data. This way, multiple analysis components can have the same models
     * as their input data.
     * 
     * @param <T> The type of model to duplicate.
     */
    private static class ExtractorDataDuplicator<T> implements Runnable {
        
        private AbstractProvider<T, ?> provider;
        
        private boolean multiple;
        
        private List<StartingComponent<T>> startingComponents;
        
        /**
         * Creates a new ExtractorDataDuplicator.
         * 
         * @param provider The provider to get the data from.
         * @param multiple Whether the provider should be polled multiple times or just onece.
         */
        public ExtractorDataDuplicator(AbstractProvider<T, ?> provider, boolean multiple) {
            this.provider = provider;
            this.multiple = multiple;
            startingComponents = new LinkedList<>();
        }
        
        /**
         * Creates a new starting component that will get its own copy of the data from us.
         * 
         * @param config The configuration to create the component with.
         * 
         * @return The starting component that can be used as input data for other analysis components.
         */
        public StartingComponent<T> createNewStartingComponent(Configuration config) {
            StartingComponent<T> component = new StartingComponent<>(config);
            startingComponents.add(component);
            return component;
        }
        
        /**
         * Adds the given data element to all starting components.
         * 
         * @param data The data to add.
         */
        private void addToAllComponents(T data) {
            for (StartingComponent<T> component : startingComponents) {
                component.addResult(data);
            }
        }
        
        /**
         * Starts a new thread that copies the extractor data to all stating components created up until now.
         */
        public void start() {
            new Thread(this, "ExtractorDataDuplicator").start();
        }

        @Override
        public void run() {
            if (multiple) {
                T data;
                while ((data = provider.getNextResult()) != null) {
                    addToAllComponents(data);
                }
                
                ExtractorException exc;
                while ((exc = provider.getNextException()) != null) {
                    LOGGER.logException("Got extractor exception", exc);
                }
            } else {
                T data = provider.getResult();
                if (data != null) {
                    addToAllComponents(data);
                }
                
                ExtractorException exc = provider.getException();
                if (exc != null) {
                    LOGGER.logException("Got extractor exception", exc);
                }
            }
            
            for (StartingComponent<T> component : startingComponents) {
                synchronized (component) {
                    component.done = true;
                    component.notifyAll();
                }
            }
        }
        
    }
    
    /**
     * A starting component for the analysis pipeline. This is used to pass the extractor data to the analysis
     * components. This class does nothing; it is only used by {@link ExtractorDataDuplicator}.
     *  
     * @param <T> The type of result data that this produces.
     */
    private static class StartingComponent<T> extends AnalysisComponent<T> {

        private boolean done = false;
        
        /**
         * Creates a new starting component.
         * 
         * @param config The global configuration.
         */
        public StartingComponent(Configuration config) {
            super(config);
        }

        @Override
        protected void execute() {
            // wait until the duplicator tells us that we are done
            synchronized (this) {
                while (!done) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        
    }

}
