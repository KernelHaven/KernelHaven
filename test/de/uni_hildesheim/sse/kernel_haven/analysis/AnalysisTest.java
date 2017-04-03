package de.uni_hildesheim.sse.kernel_haven.analysis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.uni_hildesheim.sse.kernel_haven.util.Util;

/**
 * Tests the {@link AbstractAnalysis} class.
 * 
 * @author Adam
 * @author Kevin
 */
public class AnalysisTest {

    /**
     * Tests whether the output stream properly creates files.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testOutputStream() throws IOException {
        File outputDir = new File("testdata/tmp_output");
        outputDir.mkdir();
        
        TestAnalysis analysis = new TestAnalysis();
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
     */
    @Test
    public void testOutputFileSet() throws IOException {
        File outputDir = new File("testdata/tmp_output");
        outputDir.mkdir();
        
        TestAnalysis analysis = new TestAnalysis();
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
         */
        public TestAnalysis() {
            super(null);
        }
        
        @Override
        public void run() {
            PrintStream resultStream = createResultStream("result.txt");
            
            resultStream.println("Hello World!");
            
            resultStream.close();
        }
        
    }
    
}
