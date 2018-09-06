package net.ssehub.kernel_haven.test_utils;

import static org.junit.Assume.assumeTrue;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.Util.OSType;

/**
 * May be used to specify that a certain test class runs only on specific operating systems.
 * Usage: <tt>@RunWith(value = AbstractOsSpecificTestRunner.class)</tt>
 * @author El-Sharkawy
 *
 */
abstract class AbstractOsSpecificTestRunner extends BlockJUnit4ClassRunner {
    
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}.
     * @param clazz The test class.
     * @throws InitializationError if the test class is malformed.
     */
    public AbstractOsSpecificTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        OSType os = Util.determineOS();
        boolean isSupported = isSupportedOS(os);
        assumeTrue(this.getTestClass().getName() + " skipped because of wrong OS used: " + os, isSupported);
        
        super.run(notifier);            
    }
    
    /**
     * Specification whether the given operating system is supported by the executed test.
     * 
     * @param os The operating system as determined by {@link Util#determineOS()}.
     * @return <tt>true</tt> if the test can be executed on the OS, <tt>false</tt> if the test shall be skipped.
     */
    protected abstract boolean isSupportedOS(OSType os);
}
