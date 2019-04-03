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
package net.ssehub.kernel_haven.analysis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link AbstractAnalysis} class.
 * 
 * @author Adam
 * @author Kevin
 */
@SuppressWarnings("null")
public class AnalysisTest {

    /**
     * Tests whether the output stream properly creates files.
     * 
     * @throws IOException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testOutputStream() throws IOException, SetUpException {
        File outputDir = new File("testdata/tmp_output");
        outputDir.mkdir();
        
        TestAnalysis analysis = new TestAnalysis(new TestConfiguration(new Properties()));
        analysis.setOutputDir(outputDir);
        
        analysis.run();
        
        File expectedOutput = new File(outputDir, "result.txt");
        assertThat(expectedOutput.isFile(), is(true));
        
        BufferedReader in = new BufferedReader(new FileReader(expectedOutput));

        assertThat(in.readLine(), is("Hello World!"));
        assertThat(in.readLine(), nullValue());
        
        in.close();
        
        Util.deleteFolder(outputDir);
    }
    
    /**
     * Tests whether the output stream properly creates files.
     * 
     * @throws IOException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testOutputFileSet() throws IOException, SetUpException {
        File outputDir = new File("testdata/tmp_output");
        outputDir.mkdir();
        
        TestAnalysis analysis = new TestAnalysis(new TestConfiguration(new Properties()));
        analysis.setOutputDir(outputDir);
        analysis.run();

        Set<File> expected = new HashSet<>();
        expected.add(new File(outputDir, "result.txt"));
        assertThat(analysis.getOutputFiles(), is(expected));
        
        
        Util.deleteFolder(outputDir);
    }
    
    /**
     * A test analysis for testing purposes. 
     */
    private static class TestAnalysis extends AbstractAnalysis {
        
        /**
         * Creates a new test analysis.
         * @param config The pipeline configuration, is not used here, thus, it may be <tt>null</tt>
         */
        public TestAnalysis(@NonNull Configuration config) {
            super(config);
        }
        
        @Override
        public void run() {
            PrintStream resultStream = createResultStream("result.txt");
            
            resultStream.println("Hello World!");
            
            resultStream.close();
        }
        
    }
    
}
