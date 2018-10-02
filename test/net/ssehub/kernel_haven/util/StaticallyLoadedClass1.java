package net.ssehub.kernel_haven.util;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A class that will be loaded by the {@link StaticClassLoaderTest}.
 *
 * @author Adam
 */
public class StaticallyLoadedClass1 {

    /**
     * Initialization method called by the KernelHaven infrastructure on startup.
     * 
     * @param config The configuration.
     */
    public static void initialize(@NonNull Configuration config) {
        StaticClassLoaderTest.testClassLoaded(StaticallyLoadedClass1.class.getName());
    }
    
}
