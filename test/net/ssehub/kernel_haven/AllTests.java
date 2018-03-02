package net.ssehub.kernel_haven;

import java.io.File;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.analysis.AllAnalysisTests;
import net.ssehub.kernel_haven.build_model.AllBuildModelTests;
import net.ssehub.kernel_haven.code_model.AllCodeModelTests;
import net.ssehub.kernel_haven.config.AllConfigurationTests;
import net.ssehub.kernel_haven.util.AllUtilTests;
import net.ssehub.kernel_haven.variability_model.AllVariabilityModelTests;

/**
 * The Class AllTests.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllAnalysisTests.class,
    AllBuildModelTests.class,
    AllCodeModelTests.class,
    AllConfigurationTests.class,
    AllUtilTests.class,
    AllVariabilityModelTests.class,
    
    PipelineConfiguratiorTest.class,
    RunTest.class,
    })
public class AllTests {
    
    public static final File TESTDATA = new File("testdata");
    
}
