package net.ssehub.kernel_haven.util;

/**
 * A class that will be loaded by the {@link StaticClassLoaderTest}.
 *
 * <b>The static block of this class is relevant for the test case. Do not load this class anywhere!</b>
 *
 * @author Adam
 */
public class StaticallyLoadedClass3 {

    static {
        StaticClassLoaderTest.testClassLoaded(StaticallyLoadedClass3.class.getName());
    }
    
}
