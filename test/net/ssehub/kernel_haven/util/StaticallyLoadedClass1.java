package net.ssehub.kernel_haven.util;

/**
 * A class that will be loaded by the {@link StaticClassLoaderTest}.
 *
 * @author Adam
 */
public class StaticallyLoadedClass1 {

    /**
     * Initialization method called by the KernelHaven infrastructure on startup.
     */
    public static void initialize() {
        StaticClassLoaderTest.testClassLoaded(StaticallyLoadedClass1.class.getName());
    }
    
}
