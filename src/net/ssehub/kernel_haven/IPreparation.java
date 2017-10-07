package net.ssehub.kernel_haven;

import net.ssehub.kernel_haven.config.Configuration;

/**
 * A preparation that is executed before the analysis or extractors start. TODO: this will change in the future.
 *
 * @author Adam
 */
public interface IPreparation {

    /**
     * Executes this configuration.
     * 
     * @param config The global pipeline configuration.
     * 
     * @throws SetUpException If the preparation fails.
     */
    public void run(Configuration config) throws SetUpException;
    
}
