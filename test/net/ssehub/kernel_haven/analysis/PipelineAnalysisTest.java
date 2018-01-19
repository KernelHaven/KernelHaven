package net.ssehub.kernel_haven.analysis;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.build_model.EmptyBuildModelExtractor;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.code_model.EmptyCodeModelExtractor;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.FileContentsAssertion;
import net.ssehub.kernel_haven.test_utils.PseudoVariabilityExtractor;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.io.AbstractTableWriter;
import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityModelProvider;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Tests the {@link PipelineAnalysis}, {@link SplitComponent}, {@link JoinComponent} and {@link ListCollectorComponent}
 * classes.
 *
 * @author Adam
 */
public class PipelineAnalysisTest {
    
    private File tempOutputDir = new File("testdata/analysis_tmp");
    
    /**
     * Inits the logger.
     */
    @BeforeClass
    public static void initLogger() {
        Logger.init();
    }
    
    /**
     * Creates the empty {@link #tempOutputDir}.
     * 
     * @throws IOException unwanted.
     */
    @Before
    public void setUp() throws IOException {
        if (tempOutputDir.exists()) {
            Util.deleteFolder(tempOutputDir);
        }
        tempOutputDir.mkdir();
        
        assertThat(tempOutputDir.isDirectory(), is(true));
    }
    
    /**
     * Deletes the {@link #tempOutputDir}.
     * 
     * @throws IOException unwanted.
     */
    @After
    public void tearDown() throws IOException {
        TestOutputWriter.TABLES.clear();
        
        if (tempOutputDir.exists()) {
            Util.deleteFolder(tempOutputDir);
        }
    }
    
    /**
     * Creates a {@link PipelineAnalysis}.
     * 
     * @param config The configuration to pass to the {@link PipelineAnalysis}.
     * @param supplier The supplier for the main {@link AnalysisComponent} of the {@link PipelineAnalysis}.
     * 
     * @return The {@link PipelineAnalysis}.
     * 
     * @throws SetUpException unwanted.
     */
    private PipelineAnalysis createAnalysis(@NonNull Configuration config,
            Function<PipelineAnalysis, AnalysisComponent<?>> supplier) throws SetUpException {
        
        PipelineAnalysis analysis = new PipelineAnalysis(config) {
            
            @Override
            protected AnalysisComponent<?> createPipeline() throws SetUpException {
                return supplier.apply(this);
            }
        };
        analysis.setOutputDir(tempOutputDir);
        
        
        VariabilityModelProvider varProvider = new VariabilityModelProvider();
        PseudoVariabilityExtractor.configure(new File(""), new VariabilityVariable("A", "bool"));
        varProvider.setExtractor(new PseudoVariabilityExtractor());
        varProvider.setConfig(config);
        analysis.setVariabilityModelProvider(varProvider);
        
        BuildModelProvider buildProvider = new BuildModelProvider();
        buildProvider.setExtractor(new EmptyBuildModelExtractor());
        buildProvider.setConfig(config);
        analysis.setBuildModelProvider(buildProvider);
        
        CodeModelProvider codeProvider = new CodeModelProvider();
        codeProvider.setExtractor(new EmptyCodeModelExtractor());
        codeProvider.setConfig(config);
        analysis.setCodeModelProvider(codeProvider);
        
        return analysis;
    }
    
    /**
     * A simple analysis component that produces a specified number of strings as result.
     */
    private static class SimpleAnalysisComponent extends AnalysisComponent<String> {

        private String[] results;
        
        /**
         * Creates this {@link SimpleAnalysisComponent}.
         * 
         * @param config The configuration.
         * @param results The result strings that this component should produce.
         */
        public SimpleAnalysisComponent(Configuration config, String... results) {
            super(config);
            this.results = results;
        }

        @Override
        protected void execute() {
            for (String result : results) {
                addResult(result);
            }
        }
        
        @Override
        public String getResultName() {
            return "SimpleResult";
        }
        
    }
    
    /**
     * A simple analysis component that combines the output of multiple {@link SimpleAnalysisComponent}s.
     */
    private static class CombinedAnalysisComponent extends AnalysisComponent<String> {

        private AnalysisComponent<String>[] results;
        
        /**
         * Creates this {@link SimpleAnalysisComponent}.
         * 
         * @param config The configuration.
         * @param results The result strings that this component should produce.
         */
        @SafeVarargs
        public CombinedAnalysisComponent(Configuration config, AnalysisComponent<String>... results) {
            super(config);
            this.results = results;
        }

        @Override
        protected void execute() {
            for (AnalysisComponent<String> result : results) {
                String data;
                while ((data = result.getNextResult()) != null) {
                    addResult(data);
                }
            }
        }
        
        @Override
        public String getResultName() {
            return "CombinedResult";
        }
        
    }
    
    /**
     * A simple analysis component that uses the variability model.
     */
    private static class VariabilityAnalysisComponent extends AnalysisComponent<String> {
        
        private AnalysisComponent<VariabilityModel> varModelProvider1;
        
        private AnalysisComponent<VariabilityModel> varModelProvider2;
        
        /**
         * Creates this {@link SimpleAnalysisComponent}.
         * 
         * @param config The configuration.
         * @param varModelProvider1 The providing component for the {@link VariabilityModel}.
         * @param varModelProvider2 Another providing component for the {@link VariabilityModel}.
         */
        public VariabilityAnalysisComponent(Configuration config,
                AnalysisComponent<VariabilityModel> varModelProvider1,
                AnalysisComponent<VariabilityModel> varModelProvider2) {
            
            super(config);
            this.varModelProvider1 = varModelProvider1;
            this.varModelProvider2 = varModelProvider2;
        }
        
        @Override
        protected void execute() {
            Set<String> names = new TreeSet<>((s1, s2) -> s1.compareTo(s2));
            
            VariabilityModel model = varModelProvider1.getNextResult();
            for (VariabilityVariable variable : model.getVariables()) {
                names.add(variable.getName());
            }
            
            model = varModelProvider2.getNextResult();
            for (VariabilityVariable variable : model.getVariables()) {
                names.add(variable.getName() + "_M2");
            }
            
            for (String name : names) {
                addResult(name);
            }
        }

        @Override
        public String getResultName() {
            return "VariabilityResult";
        }
    }
    
    /**
     * A simple analysis component reads strings and appends some suffix to them.
     */
    private static class StringConsumerComponent extends AnalysisComponent<String> {

        private AnalysisComponent<String> input;
        
        private String suffix;
        
        /**
         * Creates this {@link SimpleAnalysisComponent}.
         * 
         * @param config The configuration.
         * @param input The input to get the strings from.
         * @param suffix The suffix to append to the strings.
         */
        public StringConsumerComponent(Configuration config, AnalysisComponent<String> input, String suffix) {
            super(config);
            this.input = input;
            this.suffix = suffix;
        }

        @Override
        protected void execute() {
            String data;
            while ((data = input.getNextResult()) != null) {
                addResult(data + suffix);
            }
        }
        
        @Override
        public String getResultName() {
            return "StringConsumer" + suffix;
        }
        
    }
    
    /**
     * Creates and runs a simple pipeline with a single analysis component. Tests whether the output file contains
     * the expected output.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testSimple() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3"));
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(1));
        assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
        assertThat(outputFiles[0].getName(), endsWith("_SimpleResult.csv"));
        FileContentsAssertion.assertContents(outputFiles[0], "Result1\nResult2\nResult3\n");
        
        assertThat(analysis.getOutputFiles().size(), is(1));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
    /**
     * Creates and runs a pipeline with a combined analysis component. Tests whether the output file contains
     * the expected output.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testCombined() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new CombinedAnalysisComponent(config,
                        new SimpleAnalysisComponent(config, "ResultA1", "ResultA2", "ResultA3"),
                        new SimpleAnalysisComponent(config, "ResultB1", "ResultB2", "ResultB3")
                )
        );
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(1));

        assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
        assertThat(outputFiles[0].getName(), endsWith("_CombinedResult.csv"));
        FileContentsAssertion.assertContents(outputFiles[0],
                "ResultA1\nResultA2\nResultA3\nResultB1\nResultB2\nResultB3\n");
        
        assertThat(analysis.getOutputFiles().size(), is(1));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
    /**
     * Creates a pipeline which uses the starting component for the variability model multiple times.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testStartingComponent() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new VariabilityAnalysisComponent(config, pipeline.getVmComponent(), pipeline.getVmComponent())
        );
        

        PseudoVariabilityExtractor.configure(new File(""),
                new VariabilityVariable("Var_A", "bool"),
                new VariabilityVariable("Var_B", "bool"),
                new VariabilityVariable("Var_C", "bool"));
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(1));

        assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
        assertThat(outputFiles[0].getName(), endsWith("_VariabilityResult.csv"));
        FileContentsAssertion.assertContents(outputFiles[0],
                "Var_A\nVar_A_M2\nVar_B\nVar_B_M2\nVar_C\nVar_C_M2\n");
        
        assertThat(analysis.getOutputFiles().size(), is(1));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
    /**
     * Creates and runs a pipeline with a combined analysis component, with intermediate logging enabled. Tests whether
     * the output (of the final and the intermediate) result contains the expected output.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testIntermediateResult() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        props.put(DefaultSettings.ANALYSIS_COMPONENTS_LOG.getKey(), "SimpleAnalysisComponent");
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new CombinedAnalysisComponent(config,
                        new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3")
                )
        );
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(2));
        if (outputFiles[0].getName().contains("Combined")) {
            assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[0].getName(), endsWith("_CombinedResult.csv"));
            
            assertThat(outputFiles[1].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[1].getName(), endsWith("SimpleResult.csv"));
        } else {
            assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[0].getName(), endsWith("SimpleResult.csv"));

            assertThat(outputFiles[1].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[1].getName(), endsWith("_CombinedResult.csv"));
        }
        
        for (int i = 0; i < 2; i++) {
            FileContentsAssertion.assertContents(outputFiles[i],
                    "Result1\nResult2\nResult3\n");
        }
        
        assertThat(analysis.getOutputFiles().size(), is(2));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
    /**
     * An output writer that can be used for a {@link PipelineAnalysis} as a different output writer.
     */
    private static class TestOutputWriter extends AbstractTableWriter implements ITableCollection {

        /**
         * Global mapping of tablename -> lines. This is used for all instances of {@link TestOutputWriter}.
         */
        public static final Map<String, List<String>> TABLES = new HashMap<>();
        
        private String name;

        /**
         * Creates a test output writer. This instance can be used as a {@link ITableCollection} for the
         * {@link PipelineAnalysis}.
         * 
         * @param file Ignored.
         */
        @SuppressWarnings("unused")
        public TestOutputWriter(@NonNull File file) {
        }
        
        /**
         * Creates a test output writer with the given table name.
         * @param name The name of the table.
         */
        private TestOutputWriter(@NonNull String name) {
            this.name = name;
        }
        
        @Override
        public void close() throws IOException {
        }

        @Override
        public @NonNull ITableReader getReader(@NonNull String name) throws IOException {
            throw new IOException();
        }

        @Override
        public @NonNull Set<@NonNull String> getTableNames() throws IOException {
            @SuppressWarnings("null")
            @NonNull Set<@NonNull String> result = TABLES.keySet();
            return result;
        }

        @Override
        public @NonNull ITableWriter getWriter(@NonNull String name) throws IOException {
            TABLES.putIfAbsent(name, new LinkedList<>());
            return new TestOutputWriter(name);
        }

        @Override
        public @NonNull Set<@NonNull File> getFiles() throws IOException {
            return new HashSet<>();
        }

        @Override
        public void writeRow(@Nullable Object @NonNull ... columns) throws IOException {
            TABLES.get(name).add(Arrays.toString(columns));
        }
        
    }
    
    /**
     * Tests whether a different output writer class is used correctly.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testDifferentOutputWriter() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        props.put("analysis.output_writer.class", TestOutputWriter.class.getName());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3"));
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(0));
        assertThat(analysis.getOutputFiles().size(), is(0));
        
        assertThat(TestOutputWriter.TABLES.size(), is(1));
        List<String> lines = TestOutputWriter.TABLES.get("SimpleResult");
        assertThat(lines, notNullValue());
        assertThat(lines, is(Arrays.asList("[Result1]", "[Result2]", "[Result3]")));
    }
    
    /**
     * Tests whether a wrong output writer class name is handled correctly.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testWrongOutputWriter() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        props.put("analysis.output_writer.class", "doesnt_exist");
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) ->
                new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3"));
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(1));
        assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
        assertThat(outputFiles[0].getName(), endsWith("_SimpleResult.csv"));
        FileContentsAssertion.assertContents(outputFiles[0], "Result1\nResult2\nResult3\n");
        
        assertThat(analysis.getOutputFiles().size(), is(1));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
        
        assertThat(TestOutputWriter.TABLES.size(), is(0));
    }
    
    /**
     * Tests the {@link SplitComponent} (and {@link JoinComponent}).
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testSplitComponent() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) -> {
            SplitComponent<String> split = new SplitComponent<>(config,
                    new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3")
            );
            
            AnalysisComponent<String> out1 = new StringConsumerComponent(config, split.createOutputComponent(), " 1");
            AnalysisComponent<String> out2 = new StringConsumerComponent(config, split.createOutputComponent(), " 2");
            
            return new JoinComponent(config, out1, out2);
        });
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(2));

        if (outputFiles[0].getName().contains("StringConsumer 1")) {
            assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[0].getName(), endsWith("_StringConsumer 1.csv"));
            FileContentsAssertion.assertContents(outputFiles[0],
                    "Result1 1\nResult2 1\nResult3 1\n");
            
            assertThat(outputFiles[1].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[1].getName(), endsWith("_StringConsumer 2.csv"));
            FileContentsAssertion.assertContents(outputFiles[1],
                    "Result1 2\nResult2 2\nResult3 2\n");
        } else {
            assertThat(outputFiles[1].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[1].getName(), endsWith("_StringConsumer 1.csv"));
            FileContentsAssertion.assertContents(outputFiles[1],
                    "Result1 1\nResult2 1\nResult3 1\n");
            
            assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
            assertThat(outputFiles[0].getName(), endsWith("_StringConsumer 2.csv"));
            FileContentsAssertion.assertContents(outputFiles[0],
                    "Result1 2\nResult2 2\nResult3 2\n");
        }
        
        assertThat(analysis.getOutputFiles().size(), is(2));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
    /**
     * Tests the {@link ListCollectorComponent}.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testListCollectorComponent() throws SetUpException {
        Properties props = new Properties();
        props.put("output_dir", tempOutputDir.getPath());
        props.put("source_tree", tempOutputDir.getPath());
        TestConfiguration config = new TestConfiguration(props);
        
        PipelineAnalysis analysis = createAnalysis(config, (pipeline) -> 
            new ListCollectorComponent<>(config, 
                new SimpleAnalysisComponent(config, "Result1", "Result2", "Result3")
            )
        );
        
        analysis.run();
        
        File[] outputFiles = tempOutputDir.listFiles();
        assertThat(outputFiles.length, is(1));
        
        assertThat(outputFiles[0].getName(), startsWith("Analysis_"));
        assertThat(outputFiles[0].getName(), endsWith("_SimpleResult List.csv"));
        FileContentsAssertion.assertContents(outputFiles[0],
                "[Result1, Result2, Result3]\n");
        
        assertThat(analysis.getOutputFiles().size(), is(1));
        Set<File> files = new HashSet<>();
        for (File f : outputFiles) {
            files.add(f);
        }
        assertThat(analysis.getOutputFiles(), is(files));
    }
    
}
