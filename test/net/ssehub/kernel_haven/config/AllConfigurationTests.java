package net.ssehub.kernel_haven.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests this package.
 * @author El-Sharkawy
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    ConfigurationTest.class,
    OldConfigurationTest.class,
    })
public class AllConfigurationTests {

}
