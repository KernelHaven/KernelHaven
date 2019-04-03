/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.AllTests;
import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link DIMACSVariabilityModelExtractor}.
 * @author El-Sharkawy
 *
 */
public class DIMACSVariabilityModelExtractorTest {
    
    private static final File TESTDATA = new File(AllTests.TESTDATA, "vmDimacs");
    
    /**
     * Tests correct error message if no input DIMACS file was specified.
     */
    @Test
    public void testNoFileSpecified() {
        DIMACSVariabilityModelExtractor extractor = new DIMACSVariabilityModelExtractor();
        try {
            extractor.init(new TestConfiguration(new Properties()));
            Assert.fail("No error produced if no input file is specified.");
        } catch (SetUpException e) {
            Assert.assertEquals(DefaultSettings.VARIABILITY_INPUT_FILE.getKey() + " was not specified, it must "
                + "point to input DIMACS file.", e.getMessage());
        }
    }
    
    /**
     * Tests parsing of a DIMACS file.
     * With the following specification:
     * <ul>
     *   <li><b>Variables:</b>1 Variable, with no data type</li>
     *   <li><b>Constraints:</b> 1 Constraint</li>
     * </ul>
     */
    @Test
    public void testOneVar() {
        VariabilityModel varModel = parseDimacsFile(new File(TESTDATA, "oneVar.dimacs"));
        
        // Assert model
        Set<VariabilityVariable> variables = varModel.getVariables();
        Assert.assertEquals(1, variables.size());
        
        // Assert variable
        VariabilityVariable var = variables.iterator().next();
        Assert.assertEquals("Variable", var.getName());
        Assert.assertEquals(1, var.getDimacsNumber());
        Assert.assertEquals(DIMACSVariabilityModelExtractor.UNKNOWN_VARIABE_TYPE, var.getType());
    }
    
    /**
     * Tests parsing of a DIMACS file.
     * With the following specification:
     * <ul>
     *   <li><b>Variables:</b>1 Variable, data type specification</li>
     *   <li><b>Constraints:</b> 1 Constraint</li>
     * </ul>
     */
    @Test
    public void testOneVarWithDataType() {
        VariabilityModel varModel = parseDimacsFile(new File(TESTDATA, "oneVarWithDataType.dimacs"));
        
        // Assert model
        Set<VariabilityVariable> variables = varModel.getVariables();
        Assert.assertEquals(1, variables.size());
        
        // Assert variable
        VariabilityVariable var = variables.iterator().next();
        Assert.assertEquals("Variable", var.getName());
        Assert.assertEquals(1, var.getDimacsNumber());
        Assert.assertEquals("SomethingSpecial", var.getType());
    }
    
    /**
     * Tests parsing of a DIMACS file.
     * With the following specification:
     * <ul>
     *   <li><b>Variables:</b>1 Variable, data type specification</li>
     *   <li><b>Constraints:</b> 1 Constraint</li>
     * </ul>
     */
    @Test
    public void testTwoVarsWithMixedDataType() {
        VariabilityModel varModel = parseDimacsFile(new File(TESTDATA, "twoVarsWithMixedDataType.dimacs"));
        
        // Assert model
        Set<VariabilityVariable> variables = varModel.getVariables();
        Assert.assertEquals(2, variables.size());
        
        // Assert 1st variable
        VariabilityVariable var = varModel.getVariableMap().get("VAR1");
        Assert.assertNotNull("VAR1 was not translated.", var);
        Assert.assertEquals("VAR1", var.getName());
        Assert.assertEquals(1, var.getDimacsNumber());
        Assert.assertEquals(DIMACSVariabilityModelExtractor.UNKNOWN_VARIABE_TYPE, var.getType());
        
        // Assert 2nd variable
        var = varModel.getVariableMap().get("VAR2");
        Assert.assertNotNull("VAR2 was not translated.", var);
        Assert.assertEquals("VAR2", var.getName());
        Assert.assertEquals(2, var.getDimacsNumber());
        Assert.assertEquals("SomethingSpecial", var.getType());
    }
    
    /**
     * Tests a DIMACS file with "random" (i.e. normal text) comments.
     */
    @Test
    public void testRandomComments() {
        VariabilityModel varModel = parseDimacsFile(new File(TESTDATA, "randomComments.dimacs"));
        
        // Assert model
        Set<VariabilityVariable> variables = varModel.getVariables();
        Assert.assertEquals(0, variables.size());
    }
    
    /**
     * Tests a DIMACS file with variables mixed with normal text comments.
     */
    @Test
    public void testMixedCommentsAndVariables() {
        VariabilityModel varModel = parseDimacsFile(new File(TESTDATA, "mixedCommentsAndVariables.dimacs"));
        
        // Assert model
        Set<VariabilityVariable> variables = varModel.getVariables();
        Assert.assertEquals(2, variables.size());
        
        // Assert 1st variable
        VariabilityVariable var = varModel.getVariableMap().get("VAR1");
        Assert.assertNotNull("VAR1 was not translated.", var);
        Assert.assertEquals("VAR1", var.getName());
        Assert.assertEquals(1, var.getDimacsNumber());
        Assert.assertEquals(DIMACSVariabilityModelExtractor.UNKNOWN_VARIABE_TYPE, var.getType());
        
        // Assert 2nd variable
        var = varModel.getVariableMap().get("VAR2");
        Assert.assertNotNull("VAR2 was not translated.", var);
        Assert.assertEquals("VAR2", var.getName());
        Assert.assertEquals(2, var.getDimacsNumber());
        Assert.assertEquals("SomethingSpecial", var.getType());
    }

    /**
     * Helper function to parse a DIMACS file to a {@link VariabilityModel} and to facilitate testing in a test method.
     * @param dimcasFile The file (absolute path) to test.
     * @return The converted variability model, won't be <tt>null</tt>.
     */
    @SuppressWarnings("null")
    private @NonNull VariabilityModel parseDimacsFile(File dimcasFile) {
        DIMACSVariabilityModelExtractor extractor = new DIMACSVariabilityModelExtractor();
        Properties prop = new Properties();
        prop.setProperty(DefaultSettings.VARIABILITY_INPUT_FILE.getKey(), dimcasFile.getAbsolutePath());
        try {
            extractor.init(new TestConfiguration(prop));
        } catch (SetUpException e) {
            e.printStackTrace();
            Assert.fail("Could not initialize " + DIMACSVariabilityModelExtractor.class.getSimpleName() + " due to "
                + e.getMessage());
        }
        
        VariabilityModel varModel = null;
        try {
            varModel = extractor.runOnFile(null);
        } catch (ExtractorException e) {
            e.printStackTrace();
            Assert.fail("Could not parse " + dimcasFile.getAbsolutePath() + " due to "
                + e.getMessage());
        }
        
        Assert.assertNotNull(dimcasFile.getAbsolutePath() + " was parsed to null", varModel);
        Assert.assertEquals(readFile(dimcasFile), readFile(varModel.getConstraintModel()));

        return varModel;
    }
    
    /**
     * Helper method to compare DIMACS files. Reads the specified file to a String.
     * @param txtFile A text file to be read.
     * @return The content of the specified file.
     */
    private static String readFile(File txtFile) {
        Assert.assertNotNull(txtFile);
        Assert.assertTrue(txtFile.getAbsolutePath() + " does not exist", txtFile.exists());
        
        StringBuilder result = new StringBuilder();
        try {
            Files.lines(txtFile.toPath()).forEachOrdered(l -> result.append(l));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Could not read " + txtFile.getAbsolutePath() + " due to " + e.getMessage());
        }
        
        return result.toString();
    }

}
