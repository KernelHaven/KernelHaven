package net.ssehub.kernel_haven;

import net.ssehub.kernel_haven.config.Configuration;

/**
 * A preparation that is executed before the analysis or extractors start. This preparation is created after the
 * providers and extractors are set up, but before the analysis is instantiated. This preparation may start extractors.
 * 
 * <p>TODO: this will change in the future.</p>
 *
 * @author Adam
 */
public interface IPreparation {

    /**
     * Executes this preparation.
     * 
     * @param config The global pipeline configuration.
     * 
     * @throws SetUpException If the preparation fails.
     */
    public void run(Configuration config) throws SetUpException;
    
}
