package de.uni_hildesheim.sse.kernel_haven;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_hildesheim.sse.kernel_haven.analysis.AnalysisTest;
import de.uni_hildesheim.sse.kernel_haven.build_model.BuildModelCacheTest;
import de.uni_hildesheim.sse.kernel_haven.build_model.BuildModelProviderTest;
import de.uni_hildesheim.sse.kernel_haven.code_model.CodeModelCacheTest;
import de.uni_hildesheim.sse.kernel_haven.code_model.CodeModelProviderTest;
import de.uni_hildesheim.sse.kernel_haven.util.BlockingQueueTest;
import de.uni_hildesheim.sse.kernel_haven.util.ConfigurationTest;
import de.uni_hildesheim.sse.kernel_haven.util.LoggerTest;
import de.uni_hildesheim.sse.kernel_haven.util.UtilTest;
import de.uni_hildesheim.sse.kernel_haven.util.ZipperTest;
import de.uni_hildesheim.sse.kernel_haven.util.logic.FormulaTest;
import de.uni_hildesheim.sse.kernel_haven.util.logic.ParserTest;
import de.uni_hildesheim.sse.kernel_haven.variability_model.VariabilityModelCacheTest;
import de.uni_hildesheim.sse.kernel_haven.variability_model.VariabilityModelProviderTest;

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
    FormulaTest.class,
    ParserTest.class,
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
