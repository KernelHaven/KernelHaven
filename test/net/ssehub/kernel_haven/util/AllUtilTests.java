package net.ssehub.kernel_haven.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.util.cpp.AllCppTests;
import net.ssehub.kernel_haven.util.io.AllIoTests;
import net.ssehub.kernel_haven.util.logic.AllLogicTests;

/**
 * Tests for util package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllIoTests.class,
    AllLogicTests.class,
    AllCppTests.class,
    
    BlockingQueueTest.class,
    FormulaCacheTest.class,
    LoggerTest.class,
    OrderPreservingParallelizerTest.class,
    PipelineArchiverTest.class,
    StaticClassLoaderTest.class,
    UtilTest.class,
    ZipArchiveTest.class,
    })
public class AllUtilTests {

}
