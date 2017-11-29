package net.ssehub.kernel_haven;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the {@link Run} class. This class only has negative tests, since we don't want to execute the main method of
 * the pipeline to artificially increase test coverage.
 * 
 * @author Adam
 */
public class RunTest {
    
    /**
     * Tests whether a missing properties file is detected.
     */
    @Test
    public void testMissingPropertiesFile() {
        assertThat(Run.run(), is(false));
    }
    
    /**
     * Tests whether a non existing properties file is detected.
     */
    @Test
    public void testNonExistingPropertiesFile() {
        assertThat(Run.run("doesnt_exist.properties"), is(false));
    }
    
    /**
     * Tests whether a too many property files are detected.
     */
    @Test
    public void testTooManyPropertyFiles() {
        assertThat(Run.run("1.properties", "2.properties"), is(false));
    }
    
    /**
     * Tests whether an invalid command line argument is detected.
     */
    @Test
    public void testInvalidArgument() {
        assertThat(Run.run("--invalid"), is(false));
    }
    
}
