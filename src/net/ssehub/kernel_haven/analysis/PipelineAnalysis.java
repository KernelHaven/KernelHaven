package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Timestamp;
import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.io.TableCollectionWriterFactory;
import net.ssehub.kernel_haven.util.io.csv.CsvFileCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * An analysis that is a pipeline consisting of {@link AnalysisComponent}s.
 * 
 * @author Adam
 */
public abstract class PipelineAnalysis extends AbstractAnalysis {

    private static PipelineAnalysis instance;
    
    private ITableCollection resultCollection;
    
    private ExtractorDataDuplicator<VariabilityModel> vmStarter;
    
    private ExtractorDataDuplicator<BuildModel> bmStarter;
    
    private ExtractorDataDuplicator<SourceFile<?>> cmStarter;
    
    /**
     * Creates a new {@link PipelineAnalysis}.
     * 
     * @param config The global configuration.
     */
    public PipelineAnalysis(@NonNull Configuration config) {
        super(config);
    }
    
    /**
     * The {@link PipelineAnalysis} that is the current main analysis in this execution. May be null if no
     * {@link PipelineAnalysis} is the main analysis component.
     *
     * @return The current {@link PipelineAnalysis} instance.
     */
    static PipelineAnalysis getInstance() {
        return instance;
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the variability model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the variability model.
     */
    protected @NonNull AnalysisComponent<VariabilityModel> getVmComponent() {
        return vmStarter.createNewStartingComponent(config);
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the build model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the build model.
     */
    protected @NonNull AnalysisComponent<BuildModel> getBmComponent() {
        return bmStarter.createNewStartingComponent(config);
    }
    
    /**
     * Returns the {@link AnalysisComponent} that provides the code model from the extractors.
     * 
     * @return The {@link AnalysisComponent} that provides the code model.
     */
    protected @NonNull AnalysisComponent<SourceFile<?>> getCmComponent() {
        return cmStarter.createNewStartingComponent(config);
    }
    
    /**
     * The collection that {@link AnalysisComponent}s should write their intermediate output to.
     *  
     * @return The {@link ITableCollection} to write output to.
     */
    ITableCollection getResultCollection() {
        return resultCollection;
    }
    
    /**
     * Creates the result collection from the user settings.
     * 
     * @return The result collection to store files in.
     * 
     * @throws SetUpException If creating the result collection fails.
     */
    private ITableCollection createResultCollection() throws SetUpException {
        String outputSuffix = config.getValue(DefaultSettings.ANALYSIS_RESULT);
        File outputFile = new File(getOutputDir(), Timestamp.INSTANCE.getFilename(
                config.getValue(DefaultSettings.ANALYSIS_RESULT_NAME), outputSuffix));
        
        try {
            return TableCollectionWriterFactory.INSTANCE.createCollection(outputFile);
        } catch (IOException e) {
            throw new SetUpException("Can't create output for suffix " + outputSuffix, e);
        }
    }
    
    /**
     * Creates the pipeline.
     * 
     * @return The "main" (i.e. the last) component of the pipeline.
     * 
     * @throws SetUpException If setting up the pipeline fails.
     */
    protected abstract @NonNull AnalysisComponent<?> createPipeline() throws SetUpException;

    @Override
    public void run() {
        Thread.currentThread().setName("AnalysisPipelineController");
        try {
            vmStarter = new ExtractorDataDuplicator<>(vmProvider, false, "VM");
            bmStarter = new ExtractorDataDuplicator<>(bmProvider, false, "BM");
            cmStarter = new ExtractorDataDuplicator<>(cmProvider, true, "CM");
            
            try {
                resultCollection = createResultCollection();
            } catch (SetUpException e) {
                LOGGER.logException("Couldn't create output collection based on user configuration; "
                        + "falling back to CSV", e);
                
                resultCollection = new CsvFileCollection(new File(getOutputDir(), 
                        "Analysis_" + Timestamp.INSTANCE.getFileTimestamp()));
            }
            
            instance = this;
        
            AnalysisComponent<?> mainComponent = createPipeline();
            
            if (config.getValue(DefaultSettings.ANALYSIS_PIPELINE_START_EXTRACTORS)) {
                // start all extractors; this is needed here because the analysis components will most likely poll them
                // in order, which means that the extractors would not run in parallel
                vmStarter.start();
                bmStarter.start();
                cmStarter.start();
            }
            
            if (mainComponent instanceof JoinComponent) {
                joinSplitComponentFull((JoinComponent) mainComponent);
                
            } else {
                pollAndWriteOutput(mainComponent);
            }
            
            LOGGER.logDebug("Analysis components done");
            
            try {
                LOGGER.logDebug("Closing result collection");
                resultCollection.close();
                
                for (File file : resultCollection.getFiles()) {
                    addOutputFile(file);
                }
            } catch (IOException e) {
                LOGGER.logException("Exception while closing output file", e);
            }
            
        } catch (SetUpException e) {
            LOGGER.logException("Exception while setting up", e);
        }
    }

    /**
     * Part of {@link #run()} to handle {@link JoinComponent}s. This method joins all components in parallel.
     * 
     * @param mainComponent The analysis, which is joining results of multiple other components.
     */
    private void joinSplitComponentFull(@NonNull JoinComponent mainComponent) {
        List<Thread> threads = new ArrayList<>(mainComponent.getInputs().length);
        
        for (AnalysisComponent<?> component : mainComponent.getInputs()) {
            Thread th = new Thread(() -> {
                pollAndWriteOutput(component);
            }, "AnalysisPipelineControllerOutputThread");
            threads.add(th);
            th.setDaemon(true);
            th.start();
        }
        
        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    /**
     * Polls all output from the given component and writes it to the output file.
     * 
     * @param component The component to read the output from.
     */
    private void pollAndWriteOutput(@NonNull AnalysisComponent<?> component) {
        LOGGER.logDebug2("Starting and polling output of analysis component (", component.getClass().getSimpleName(),
            ")...");
        
        try (ITableWriter writer = resultCollection.getWriter(component.getResultName())) {
            Object result;
            while ((result = component.getNextResult()) != null) {
                LOGGER.logDebug2("Got analysis result: ", result.toString());
                
                writer.writeObject(result);
            }
        } catch (IOException e) {
            LOGGER.logException("Exception while writing output file", e);
        }
    }
    
    /**
     * A class for duplicating the extractor data. This way, multiple analysis components can have the same models
     * as their input data.
     * 
     * @param <T> The type of model to duplicate.
     */
    private static class ExtractorDataDuplicator<T> implements Runnable {
        
        private @NonNull AbstractProvider<T> provider;
        
        private boolean multiple;
        
        private @NonNull List<@NonNull StartingComponent<T>> startingComponents;
        
        private boolean started;
        
        private @NonNull String type;
        
        /**
         * Creates a new ExtractorDataDuplicator.
         * 
         * @param provider The provider to get the data from.
         * @param multiple Whether the provider should be polled multiple times or just once.
         * @param type The type of duplicator component ("CM", "BM" or "VM").
         */
        public ExtractorDataDuplicator(@NonNull AbstractProvider<T> provider, boolean multiple, @NonNull String type) {
            this.provider = provider;
            this.multiple = multiple;
            this.type = type;
            startingComponents = new LinkedList<>();
        }
        
        /**
         * Creates a new starting component that will get its own copy of the data from us.
         * 
         * @param config The configuration to create the component with.
         * 
         * @return The starting component that can be used as input data for other analysis components.
         */
        public @NonNull StartingComponent<T> createNewStartingComponent(@NonNull Configuration config) {
            
            StartingComponent<T> component = new StartingComponent<>(config, this, type);
            startingComponents.add(component);
            return component;
        }
        
        /**
         * Adds the given data element to all starting components.
         * 
         * @param data The data to add.
         */
        private void addToAllComponents(@NonNull T data) {
            for (StartingComponent<T> component : startingComponents) {
                component.addResult(data);
            }
        }
        
        /**
         * Starts a new thread that copies the extractor data to all stating components created up until now.
         * This method ensures that this thread is only started once, no matter how often this method is called.
         */
        public void start() {
            synchronized (this) {
                if (!started) {
                    new Thread(this, "ExtractorDataDuplicator").start();
                    started = true;
                }
            }
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
        
        private @NonNull ExtractorDataDuplicator<T> duplicator;
        
        private @NonNull String name;
        
        /**
         * Creates a new starting component.
         * 
         * @param config The global configuration.
         * @param duplicator The {@link ExtractorDataDuplicator} to start when this component is started
         *      (start on demand).
         * @param type The type of starting component ("CM", "BM" or "VM").
         */
        public StartingComponent(@NonNull Configuration config, @NonNull ExtractorDataDuplicator<T> duplicator,
                @NonNull String type) {
            super(config);
            this.duplicator = duplicator;
            this.name = type + " StartingComponent";
        }

        @Override
        protected void execute() {
            duplicator.start();
            
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

        @Override
        public String getResultName() {
            return name;
        }
        
        @Override
        boolean isInternalHelperComponent() {
            return true;
        }
        
    }

}
