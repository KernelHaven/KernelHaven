package net.ssehub.kernel_haven.analysis;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;

/**
 * An empty analysis which does nothing but start all extrators.
 *
 * @author Adam
 */
public class EmptyAnalysis extends AbstractAnalysis {

    /**
     * Creates a new empty analysis.
     * 
     * @param config The configuration.
     */
    public EmptyAnalysis(Configuration config) {
        super(config);
    }
    
    @Override
    public void run() {
        try {
            cmProvider.start();
            bmProvider.start();
            vmProvider.start();
            
            // wait for result
            vmProvider.getResult();
            bmProvider.getResult();
            SourceFile result;
            do {
                result = cmProvider.getNextResult();
            } while (result != null);
            
        } catch (SetUpException e) {
            LOGGER.logException("Exception while starting extractors", e);
        }
    }

}
