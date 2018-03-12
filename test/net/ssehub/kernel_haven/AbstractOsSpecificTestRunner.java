package net.ssehub.kernel_haven;

import static org.junit.Assume.assumeTrue;

import java.util.Locale;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

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
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        boolean isSupported = isSupportedOS(os);
        assumeTrue(this.getTestClass().getName() + " skipped because of wrong OS used: " + os, isSupported);
        
        super.run(notifier);            
    }
    
    /**
     * Specification whether the given operating system is supported by the executed test.
     * @param os The name of the Operating system (<tt>System.getProperty("os.name")</tt>).
     * @return <tt>true</tt> if the test can be executed on the OS, <tt>false</tt> if the test shall be skipped.
     */
    protected abstract boolean isSupportedOS(String os);
}
