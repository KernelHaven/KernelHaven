package net.ssehub.kernel_haven.util;

import java.net.URL;
import java.net.URLClassLoader;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link ClassLoader} to be used by KernelHaven. Starting with Java 9, it is required that this is set as the
 * <code>java.system.class.loader</code> in order for the plugin system to work.
 *
 * @author Adam
 */
public class KernelHavenClassLoader extends URLClassLoader {

    /**
     * Creates this class loader. This will be called by the JVM if <code>java.system.class.loader</code> is set to
     * this class.
     * 
     * @param parent The parent class loader to delegate to.
     */
    public KernelHavenClassLoader(@NonNull ClassLoader parent) {
        super(new URL[0], parent);
    }

    // make this method public
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
}
