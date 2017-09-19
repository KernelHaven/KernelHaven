package net.ssehub.kernel_haven.todo;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.AllTests;
import net.ssehub.kernel_haven.PipelineConfigurator;
import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.TestConfiguration;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.FileContentsAssertion;
import net.ssehub.kernel_haven.test_utils.PseudoVariabilityExtractor;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.variability_model.FiniteIntegerVariable;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Tests for the {@link NonBooleanPreperation}.
 * @author El-Sharkawy
 *
 */
public class NonBooleanPreperationTest {
    
    private static final File TESTDATA_FOLDER = new File(AllTests.TESTDATA, "nonBooleanPreparation");
    private static final File IN_FOLDER = new File(TESTDATA_FOLDER, "inDir");
    private static final File OUT_FOLDER = new File(TESTDATA_FOLDER, "outDir");
    
    /**
     * Inits the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }
    
    /**
     * Wipes the {@link #OUT_FOLDER} for testing.
     */
    @Before
    public void setUp() {
        if (!OUT_FOLDER.exists()) {
            OUT_FOLDER.mkdirs();
        } else {
            File[] elements = OUT_FOLDER.listFiles();
            for (int i = 0; i < elements.length; i++) {
                elements[i].delete();
            }
        }
    }
    
    /**
     * Tests the replacement of a comparison, without using a variability model.
     * @throws SetUpException If setup fails, should not happen.
     */
    @Test
    public void testGreaterExpressionWithoutVarModel() throws SetUpException {
        NonBooleanPreperation preparator = new NonBooleanPreperation();
        CodeExtractorConfiguration cconfig = createConfig();
        preparator.run(cconfig);
        
        FileContentsAssertion.assertContents(new File(OUT_FOLDER, "comparison.c"), 
            "#if ((defined(a_eq_2))) \n"
            + "    // Do something\n"
            + "#endif");
    }
    
    /**
     * Tests the replacement of a comparison, with a variability model.
     * Both variables have the same range.
     * @throws SetUpException If setup fails, should not happen.
     */
    @Test
    public void testGreaterExpressionWithVarModel() throws SetUpException {
        NonBooleanPreperation preparator = new NonBooleanPreperation();
        CodeExtractorConfiguration cconfig = createConfig(new FiniteIntegerVariable("a", "tristate",
            new int[] {1, 2, 3}));
        preparator.run(cconfig);
        
        FileContentsAssertion.assertContents(new File(OUT_FOLDER, "comparison.c"), 
            "#if ((defined(a_eq_2) || defined(a_eq_3))) \n"
            + "    // Do something\n"
            + "#endif");
    }
    
    /**
     * Tests the replacement of a comparison, with a variability model.
     * Both variables have the same range.
     * @throws SetUpException If setup fails, should not happen.
     */
    @Test
    public void testEqualityOnVarsWithVarModelSameRanges() throws SetUpException {
        NonBooleanPreperation preparator = new NonBooleanPreperation();
        CodeExtractorConfiguration cconfig = createConfig(
            new FiniteIntegerVariable("a", "tristate", new int[] {1, 2, 3}),
            new FiniteIntegerVariable("b", "tristate", new int[] {1, 2, 3}));
        preparator.run(cconfig);
        
        FileContentsAssertion.assertContents(new File(OUT_FOLDER, "equalityOnVars1.c"), 
            "#if ((((defined(a_eq_1) && defined(b_eq_1)) || (defined(a_eq_2) && defined(b_eq_2)) "
            + "|| (defined(a_eq_3) && defined(b_eq_3))))) {\n"
            + "    // Do something\n"
            + "#endif");
    }
    
    /**
     * Tests the replacement of a comparison, with a variability model.
     * The ranges of the variables differ.
     * @throws SetUpException If setup fails, should not happen.
     */
    @Test
    public void testEqualityOnVarsWithVarModelDifferentRanges() throws SetUpException {
        NonBooleanPreperation preparator = new NonBooleanPreperation();
        CodeExtractorConfiguration cconfig = createConfig(
                new FiniteIntegerVariable("a", "tristate", new int[] {0, 1, 2}),
                new FiniteIntegerVariable("b", "tristate", new int[] {1, 2, 3}));
        preparator.run(cconfig);
        
        FileContentsAssertion.assertContents(new File(OUT_FOLDER, "equalityOnVars1.c"), 
                "#if ((((defined(a_eq_1) && defined(b_eq_1)) || (defined(a_eq_2) && defined(b_eq_2))) "
                        + "&& !defined(a_eq_0) && !defined(b_eq_3))) {\n"
                        + "    // Do something\n"
                        + "#endif");
    }

    /**
     * Configures the {@link PipelineConfigurator} and creates the {@link CodeExtractorConfiguration}, which is needed
     * for testing the {@link NonBooleanPreperation}.
     * @param variables Should be <tt>null</tt> or empty if the preparation should be tested without a variability
     *     model or the complete list of relevant variables.
     * @return {@link CodeExtractorConfiguration}, which is needed for testing the {@link NonBooleanPreperation}.
     */
    private CodeExtractorConfiguration createConfig(VariabilityVariable... variables) {
        Properties prop = new Properties();
        boolean usesVarModel = null != variables && variables.length > 0;
        if (usesVarModel) {
            PseudoVariabilityExtractor.configure(new File("this is a mock"), variables);
            prop.put("variability.extractor.class", PseudoVariabilityExtractor.class.getName());
        } 
        
        prop.setProperty("source_tree", IN_FOLDER.getAbsolutePath());
        CodeExtractorConfiguration cconfig = null;
        try {
            Configuration config = new TestConfiguration(prop);
            PipelineConfigurator.instance().init(config);
            PipelineConfigurator.instance().instantiateExtractors();
            PipelineConfigurator.instance().createProviders();
            
            if (usesVarModel) {
                PipelineConfigurator.instance().getVmProvider().start();
            }
            
            cconfig = config.getCodeConfiguration();
        } catch (SetUpException e) {
            Assert.fail(e.getMessage());
        }
        
        cconfig.setProperty(NonBooleanPreperation.DESTINATION_CONFIG, OUT_FOLDER.getAbsolutePath());
        cconfig.setProperty(NonBooleanPreperation.VAR_IDENTIFICATION_REGEX_CONFIG, "\\p{Alpha}");
        cconfig.setSourceTree(IN_FOLDER);
        return cconfig;
    }

}
