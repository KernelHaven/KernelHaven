package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.util.Set;

import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.variability_model.VariabilityModelProvider;

/**
 * An analysis that is started by the pipeline configurator.
 * 
 * @author Adam
 */
public interface IAnalysis {

    /**
     * Sets the provider that this analysis should use for extracting the variability model.
     * 
     * @param provider  The provider to use.
     */
    public void setVariabilityModelProvider(VariabilityModelProvider provider);
    
    /**
     * Sets the provider that this analysis should use for extracting the build model.
     * 
     * @param provider The provider to use.
     */
    public void setBuildModelProvider(BuildModelProvider provider);
    
    /**
     * Sets the provider that this analysis should use for extracting the code model.
     * 
     * @param provider The provider to use.
     */
    public void setCodeModelProvider(CodeModelProvider provider);
    
    /**
     * Sets the directory where the analysis can place it output files.
     * 
     * @param outputDir The directory for output storage. This is a directory where we have write access.
     */
    public void setOutputDir(File outputDir);
    
    /**
     * Returns all files that were created by the analysis.
     * 
     * @return All output files of the analysis.
     */
    public Set<File> getOutputFiles();
    
    /**
     * Executes the analysis.
     */
    public void run();
    
}
