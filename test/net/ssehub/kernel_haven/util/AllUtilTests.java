package net.ssehub.kernel_haven.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.util.io.AllIoTests;
import net.ssehub.kernel_haven.util.logic.AllLogicTests;

/**
 * Tests for util package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllIoTests.class,
    AllLogicTests.class,
    
    BlockingQueueTest.class,
    ConfigurationTest.class,
    LoggerTest.class,
    PipelineArchiverTest.class,
    UtilTest.class,
    ZipArchiveTest.class,
})
public class AllUtilTests {

}
