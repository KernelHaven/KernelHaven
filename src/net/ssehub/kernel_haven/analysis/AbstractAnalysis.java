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
    protected VariabilityModelProvider vmProvider;
    
    /**
     * The provider for the build model.
     */
    protected BuildModelProvider bmProvider;
    
    /**
     * The provider for the code model.
     */
    protected CodeModelProvider cmProvider;
    
    /**
     * The global configuration.
     */
    protected Configuration config;
    
    private File outputDir;
    
    private Set<File> outputFiles;

    /**
     * Creates a new abstract analysis.
     * 
     * @param config The configuration passed to us by the pipeline. Must not be <code>null</code>.
     */
    public AbstractAnalysis(Configuration config) {
        this.config = config;
        outputFiles = new HashSet<>();
    }
    
    @Override
    public void setVariabilityModelProvider(VariabilityModelProvider provider) {
        this.vmProvider = provider;
    }
    
    @Override
    public void setBuildModelProvider(BuildModelProvider provider) {
        this.bmProvider = provider;
    }
    
    @Override
    public void setCodeModelProvider(CodeModelProvider provider) {
        this.cmProvider = provider;
    }
    
    @Override
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }
    
    @Override
    public Set<File> getOutputFiles() {
        return outputFiles;
    }
    
    /**
     * Creates a new output stream into a result file.
     * 
     * @param filename The name of the file to write into. Never null.
     * @return The output stream; never null.
     */
    protected PrintStream createResultStream(String filename) {
        File outputFile = new File(outputDir, filename);
        outputFiles.add(outputFile);
        
        PrintStream result = null;
        
        try {
            result = new PrintStream(outputFile);
        } catch (FileNotFoundException e) {
            // can't happen, because we already checked that outputDir is writable
        }
        
        return result;
    }

}
