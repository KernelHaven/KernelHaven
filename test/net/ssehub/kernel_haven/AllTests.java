package net.ssehub.kernel_haven;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.analysis.AnalysisTest;
import net.ssehub.kernel_haven.build_model.BuildModelCacheTest;
import net.ssehub.kernel_haven.build_model.BuildModelProviderTest;
import net.ssehub.kernel_haven.code_model.CodeModelCacheTest;
import net.ssehub.kernel_haven.code_model.CodeModelProviderTest;
import net.ssehub.kernel_haven.util.BlockingQueueTest;
import net.ssehub.kernel_haven.util.ConfigurationTest;
import net.ssehub.kernel_haven.util.LoggerTest;
import net.ssehub.kernel_haven.util.UtilTest;
import net.ssehub.kernel_haven.util.ZipperTest;
import net.ssehub.kernel_haven.util.logic.AllLogicTests;
import net.ssehub.kernel_haven.variability_model.VariabilityModelCacheTest;
import net.ssehub.kernel_haven.variability_model.VariabilityModelProviderTest;

/**
 * The Class AllTests.
 */
@RunWith(Suite.class)
@SuiteClasses({
    VariabilityModelProviderTest.class,
    LoggerTest.class,
    PipelineConfiguratiorTest.class,
    VariabilityModelProviderTest.class,
    ConfigurationTest.class,
    UtilTest.class,
    AllLogicTests.class,
    VariabilityModelCacheTest.class,
    BuildModelProviderTest.class,
    BuildModelCacheTest.class,
    AnalysisTest.class,
    CodeModelProviderTest.class,
    ZipperTest.class,
    CodeModelCacheTest.class,
    BlockingQueueTest.class,
    })
public class AllTests {
    // runs tests defined in SuiteClasses
}
