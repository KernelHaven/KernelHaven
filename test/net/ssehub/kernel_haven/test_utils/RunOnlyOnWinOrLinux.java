package net.ssehub.kernel_haven.test_utils;

import org.junit.runners.model.InitializationError;

import net.ssehub.kernel_haven.util.Util.OSType;

/**
 * May be used to specify that a certain test class runs only on <b>Windows</b> or <b>Linux</b> systems.
 * For instance, srcML-Extractor currently runs only on these two systems.
 * Usage: <tt>@RunWith(value = RunOnlyOnWinOrLinux.class)</tt>
 * @author El-Sharkawy
 *
 */
public class RunOnlyOnWinOrLinux extends AbstractOsSpecificTestRunner {
    
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}.
     * @param clazz The test class.
     * @throws InitializationError if the test class is malformed.
     */
    public RunOnlyOnWinOrLinux(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected boolean isSupportedOS(OSType os) {
        return os == OSType.LINUX32 || os == OSType.LINUX64 || os == OSType.WIN32 || os == OSType.WIN64;
    }
}
