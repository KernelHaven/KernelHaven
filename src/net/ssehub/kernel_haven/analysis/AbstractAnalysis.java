package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.VariabilityModelProvider;

/**
 * An abstract analysis that implements the setters for the provider.
 * 
 * @author Adam
 * @author Kevin
 */
public abstract class AbstractAnalysis implements IAnalysis {

    protected static final Logger LOGGER = Logger.get();
    
    /**
     * The provider for the variability model.
     */
    protected @NonNull VariabilityModelProvider vmProvider;
    
    /**
     * The provider for the build model.
     */
    protected @NonNull BuildModelProvider bmProvider;
    
    /**
     * The provider for the code model.
     */
    protected @NonNull CodeModelProvider cmProvider;
    
    /**
     * The global configuration.
     */
    protected @NonNull Configuration config;
    
    private @Nullable File outputDir;
    
    private @NonNull Set<@NonNull File> outputFiles;

    /**
     * Creates a new abstract analysis.
     * 
     * @param config The configuration passed to us by the pipeline. Must not be <code>null</code>.
     */
    @SuppressWarnings("null") // doesn't set values for the providers; but we are sure that they will be set once run()
                              // is called, so its safe to mark the providers as @NonNull
    public AbstractAnalysis(@NonNull Configuration config) {
        this.config = config;
        outputFiles = new HashSet<>();
    }
    
    @Override
    public void setVariabilityModelProvider(@NonNull VariabilityModelProvider provider) {
        this.vmProvider = provider;
    }
    
    @Override
    public void setBuildModelProvider(@NonNull BuildModelProvider provider) {
        this.bmProvider = provider;
    }
    
    @Override
    public void setCodeModelProvider(@NonNull CodeModelProvider provider) {
        this.cmProvider = provider;
    }
    
    @Override
    public void setOutputDir(@NonNull File outputDir) {
        this.outputDir = outputDir;
    }
    
    /**
     * Returns the target destination for analysis results.
     * @return A directory in which analysis results shall be written to.
     */
    protected @Nullable File getOutputDir() {
        return outputDir;
    }
    
    @Override
    public @NonNull Set<@NonNull File> getOutputFiles() {
        return outputFiles;
    }
    
    /**
     * Marks a given file as an output file of the analysis. This should be used for all files that are not created
     * via {@link #createResultStream(String)}.
     * 
     * @param file The file to mark as an output file of this analysis.
     */
    protected void addOutputFile(@NonNull File file) {
        outputFiles.add(file);
    }
    
    /**
     * Creates a new output stream into a result file.
     * 
     * @param filename The name of the file to write into. Never null.
     * @return The output stream; never null.
     */
    protected @NonNull PrintStream createResultStream(@NonNull String filename) {
        File outputFile = new File(outputDir, filename);
        outputFiles.add(outputFile);
        
        PrintStream result = null;
        
        try {
            result = new PrintStream(outputFile);
        } catch (FileNotFoundException e) {
            // can't happen, because we already checked that outputDir is writable
            throw new RuntimeException(e);
        }
        
        return result;
    }

}
