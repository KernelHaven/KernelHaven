package net.ssehub.kernel_haven.analysis;

import java.io.File;
import java.util.Set;

import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
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
    public void setVariabilityModelProvider(@NonNull VariabilityModelProvider provider);
    
    /**
     * Sets the provider that this analysis should use for extracting the build model.
     * 
     * @param provider The provider to use.
     */
    public void setBuildModelProvider(@NonNull BuildModelProvider provider);
    
    /**
     * Sets the provider that this analysis should use for extracting the code model.
     * 
     * @param provider The provider to use.
     */
    public void setCodeModelProvider(@NonNull CodeModelProvider provider);
    
    /**
     * Sets the directory where the analysis can place it output files.
     * 
     * @param outputDir The directory for output storage. This is a directory where we have write access.
     */
    public void setOutputDir(@NonNull File outputDir);
    
    /**
     * Returns all files that were created by the analysis.
     * 
     * @return All output files of the analysis.
     */
    public @NonNull Set<@NonNull File> getOutputFiles();
    
    /**
     * Executes the analysis.
     */
    public void run();
    
}
