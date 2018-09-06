package net.ssehub.kernel_haven.test_utils;

import org.junit.runners.model.InitializationError;

import net.ssehub.kernel_haven.util.Util.OSType;

/**
 * May be used to specify that a certain test class runs only on <b>Linux</b> systems.
 * Usage: <tt>@RunWith(value = RunOnlyOnLinux.class)</tt>
 * @author El-Sharkawy
 *
 */
public class RunOnlyOnLinux extends AbstractOsSpecificTestRunner {
    
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}.
     * @param clazz The test class.
     * @throws InitializationError if the test class is malformed.
     */
    public RunOnlyOnLinux(Class<?> clazz) throws InitializationError {
        super(clazz);
    }


    @Override
    protected boolean isSupportedOS(OSType os) {
        return os == OSType.LINUX64 || os == OSType.LINUX32;
    }
}
