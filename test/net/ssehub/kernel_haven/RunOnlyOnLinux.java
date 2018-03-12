package net.ssehub.kernel_haven;

import org.junit.runners.model.InitializationError;

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
    protected boolean isSupportedOS(String os) {
        // Test that OS is liNUX.
        return os.indexOf("nux") >= 0;
    }
}
