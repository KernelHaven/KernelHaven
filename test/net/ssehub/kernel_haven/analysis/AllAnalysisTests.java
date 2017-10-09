package net.ssehub.kernel_haven.analysis;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.util.Logger;

/**
 * Tests for analysis package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AnalysisTest.class,
    ConfiguredPipelineAnalysisTest.class,
    PipelineAnalysisTest.class,
    })
public class AllAnalysisTests {

    /**
     * Inits the logger.
     */
    @BeforeClass
    public static void initLogger() {
        Logger.init();
    }
    
}
