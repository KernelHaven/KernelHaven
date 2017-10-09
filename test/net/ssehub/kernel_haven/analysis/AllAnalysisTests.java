package net.ssehub.kernel_haven.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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

}
