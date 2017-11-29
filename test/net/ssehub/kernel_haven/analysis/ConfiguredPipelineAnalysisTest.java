package net.ssehub.kernel_haven.analysis;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Tests the {@link ConfiguredPipelineAnalysis} class.
 * 
 * @author Adam
 */
public class ConfiguredPipelineAnalysisTest {
    
    /**
     * A dummy analysis component for test cases.
     */
    private static class DummyAnalysisComponent1 extends AnalysisComponent<String> {

        /**
         * Creates this dummy component.
         * 
         * @param config The configuration.
         */
        public DummyAnalysisComponent1(Configuration config) {
            super(config);
        }

        @Override
        protected void execute() {
        }

        @Override
        public String getResultName() {
            return "Dummy1";
        }
        
    }
    
    /**
     * A dummy analysis component for test cases.
     */
    private static class DummyAnalysisComponent2 extends AnalysisComponent<String> {
        
        private AnalysisComponent<String> component1;
        
        /**
         * Creates this dummy component.
         * 
         * @param config The configuration.
         * @param component1 The nested component.
         */
        public DummyAnalysisComponent2(Configuration config, AnalysisComponent<String> component1) {
            super(config);
            this.component1 = component1;
        }
        
        @Override
        protected void execute() {
        }

        @Override
        public String getResultName() {
            return "Dummy2";
        }
        
    }
    
    /**
     * A dummy analysis component for test cases.
     */
    private static class DummyAnalysisComponent3 extends AnalysisComponent<String> {
        
        private AnalysisComponent<String> component1;
        
        private AnalysisComponent<String> component2;
        
        /**
         * Creates this dummy component.
         * 
         * @param config The configuration.
         * @param component1 The nested component.
         * @param component2 The second nested component.
         */
        public DummyAnalysisComponent3(Configuration config,
                AnalysisComponent<String> component1, AnalysisComponent<String> component2) {
            super(config);
            this.component1 = component1;
            this.component2 = component2;
        }
        
        @Override
        protected void execute() {
        }

        @Override
        public String getResultName() {
            return "Dummy3";
        }
        
    }

    /**
     * Tests whether the configuration string is parsed correctly.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testValidConfigurationString() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent3("
                    + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent2(" 
                        + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1()"
                    + "), "
                    + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent3("
                        + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1(), "
                        + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1()"
                    + ")"
                + ")"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        AnalysisComponent<?> mainComponent = pipeline.createPipeline();
        
        assertThat(mainComponent, instanceOf(DummyAnalysisComponent3.class));
        DummyAnalysisComponent3 comp3 = (DummyAnalysisComponent3) mainComponent;
        
        assertThat(comp3.component1, instanceOf(DummyAnalysisComponent2.class));
        DummyAnalysisComponent2 comp2 = (DummyAnalysisComponent2) comp3.component1;
        assertThat(comp2.component1, instanceOf(DummyAnalysisComponent1.class));
        
        assertThat(comp3.component2, instanceOf(DummyAnalysisComponent3.class));
        comp3 = (DummyAnalysisComponent3) comp3.component2;
        

        assertThat(comp3.component1, instanceOf(DummyAnalysisComponent1.class));
        assertThat(comp3.component2, instanceOf(DummyAnalysisComponent1.class));
    }
    
    /**
     * Tests whether the configuration string is handled correctly if a wrong number of arguments is passed t
     * the constructor.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testWrongNumberOfArguments() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1("
                    + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1()" 
                + ")"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        pipeline.createPipeline();
    }
    
    /**
     * Tests whether the configuration string is handled correctly if an invalid class name is specified.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testUnkownClass() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DoesntExist()"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        pipeline.createPipeline();
    }
    
    /**
     * Tests whether the configuration string is handled correctly if the brackets are malformed.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testMalformed1() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1("
                        + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1()" 
                        + ") somethingElse"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        pipeline.createPipeline();
    }
    
    /**
     * Tests whether the configuration string is handled correctly if the brackets are malformed.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testMalformed2() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1("
                        + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1()" 
                        + ""
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        pipeline.createPipeline();
    }
    
    /**
     * Tests whether the configuration string is handled correctly if the brackets are malformed.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testMalformed3() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent1"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new ConfiguredPipelineAnalysis(config);
        
        pipeline.createPipeline();
    }
    
    /**
     * A test class to serve as a dummy for components that provide extractor data. 
     * 
     * @param <T> The type.
     */
    private class DummyExtractorComponent<T> extends AnalysisComponent<T> {

        private String which;
        
        /**
         * Creates this dummy class.
         * 
         * @param config The configuration.
         * @param which A string to represent which extractor this dummies for.
         */
        public DummyExtractorComponent(Configuration config, String which) {
            super(config);
            this.which = which;
        }

        @Override
        protected void execute() {
        }

        @Override
        public String getResultName() {
            return "DummyExtractor";
        }
    }
    
    /**
     * Overwrites the get{Vm, Bm, Cm}Component() methods for testing purposes.
     */
    private class TestsConfiguredPipelineAnalysis extends ConfiguredPipelineAnalysis {

        /**
         * Creates this test class.
         * 
         * @param config The configuration.
         */
        public TestsConfiguredPipelineAnalysis(Configuration config) {
            super(config);
        }
        
        @Override
        protected AnalysisComponent<BuildModel> getBmComponent() {
            return new DummyExtractorComponent<>(config, "build");
        }
        
        @Override
        protected AnalysisComponent<SourceFile> getCmComponent() {
            return new DummyExtractorComponent<>(config, "code");
        }
        
        @Override
        protected AnalysisComponent<VariabilityModel> getVmComponent() {
            return new DummyExtractorComponent<>(config, "variability");
        }
    }
    
    /**
     * Tests whether the extractor components are correctly used as parameters.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testExtractorComponents() throws SetUpException {
        Properties props = new Properties();
        props.put("analysis.pipeline",
                "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent3("
                    + "cmComponent(), "
                    + "net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysisTest$DummyAnalysisComponent3("
                        + "bmComponent(), "
                        + "vmComponent()"
                    + ")"
                + ")"
        );
        TestConfiguration config = new TestConfiguration(props);
        
        ConfiguredPipelineAnalysis pipeline = new TestsConfiguredPipelineAnalysis(config);
        
        AnalysisComponent<?> mainComponent = pipeline.createPipeline();
        
        assertThat(mainComponent, instanceOf(DummyAnalysisComponent3.class));
        DummyAnalysisComponent3 comp3 = (DummyAnalysisComponent3) mainComponent;
        
        assertThat(comp3.component1, instanceOf(DummyExtractorComponent.class));
        assertThat(((DummyExtractorComponent<?>) comp3.component1).which, is("code"));
        
        assertThat(comp3.component2, instanceOf(DummyAnalysisComponent3.class));
        comp3 = (DummyAnalysisComponent3) comp3.component2;
        
        assertThat(comp3.component1, instanceOf(DummyExtractorComponent.class));
        assertThat(((DummyExtractorComponent<?>) comp3.component1).which, is("build"));
        assertThat(comp3.component2, instanceOf(DummyExtractorComponent.class));
        assertThat(((DummyExtractorComponent<?>) comp3.component2).which, is("variability"));
    }
    
}
