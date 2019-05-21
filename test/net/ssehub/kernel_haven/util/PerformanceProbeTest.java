/*
 * Copyright 2019 University of Hildesheim, Software Systems Engineering
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
package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link PerformanceProbe}.
 *
 * @author Adam
 */
public class PerformanceProbeTest {

    private static final @NonNull Logger LOGGER = Logger.get();
    
    private ByteArrayOutputStream logOutput;
    
    /**
     * Clears the performance probes before any tests are run. This makes sure that we have a "clean" start.
     */
    @BeforeClass
    public static void clearSetup() {
        PerformanceProbe.clear();
    }
    
    /**
     * Clears the performance probes after each test.
     */
    @After
    public void clearProbes() {
        PerformanceProbe.clear();
    }
    
    /**
     * Sets up catching of the log output.
     */
    @Before
    public void setUpLogCapture() {
        logOutput = new ByteArrayOutputStream();
        LOGGER.addTarget(logOutput);
    }
    
    /**
     * Clears the log catching.
     */
    @After
    public void clearLogCapture() {
        LOGGER.removeTarget(LOGGER.getTargets().size() - 1); // remove last target
    }
    
    /**
     * Disables {@link PerformanceProbe}s after all tests are done.
     */
    @AfterClass
    public static void disableProbes() {
        enableProbes(false);
    }
    
    /**
     * Returns the caught log output during this test.
     * 
     * @return The log output that was produced during this test.
     */
    private String getLogOutput() {
        return logOutput.toString();
    }
    
    /**
     * Enables or disables the {@link PerformanceProbe}s.
     * 
     * @param enable Whether to enable or disable.
     */
    private static void enableProbes(boolean enable) {
        try {
            TestConfiguration config = new TestConfiguration(new Properties());
            config.setValue(DefaultSettings.MEASURE_PERFORMANCE, enable);
            PerformanceProbe.initialize(config);
        } catch (SetUpException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests that {@link PerformanceProbe#printResult()} does nothing when no probes are recorded.
     */
    @Test
    public void printNothingIfNoProbes() {
        PerformanceProbe.printResult();
        
        assertThat(getLogOutput(), is(""));
    }
    
    /**
     * Tests that {@link PerformanceProbe}s do nothing when they are disabled.
     */
    @Test
    public void doNothingWhenDisabled() {
        enableProbes(false);
        
        PerformanceProbe p = new PerformanceProbe("C1");
        p.addExtraData("a", 2);
        p.addExtraData("b", 3);
        p.close();
        
        p = new PerformanceProbe("C1");
        p.addExtraData("a", 1);
        p.addExtraData("b", 6);
        p.close();
        
        PerformanceProbe.printResult();
        
        assertThat(getLogOutput(), is(""));
    }
    
    /**
     * Does basic format checks on the timings output.
     */
    @Test
    public void testSimpleTimings() {
        enableProbes(true);
        
        PerformanceProbe p = new PerformanceProbe("C1");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        p = new PerformanceProbe("C1");
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        PerformanceProbe.printResult();
        
        String[] lines = getLogOutput().split("\n");
        assertThat(lines[0].endsWith("Performance Measurements:"), is(true));
        assertThat(lines[1].trim(), is("C1"));
        assertThat(lines[2].trim(), is("Time:"));
        assertThat(lines[3].trim(), is("Num Measures: 2"));
        assertThat(lines[4].trim().matches("Min: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[5].trim().matches("Med: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[6].trim().matches("Avg: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[7].trim().matches("Max: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[8].trim().matches("Sum: 00:00\\.[0-9]{3}"), is(true));
        
        assertThat(lines.length, is(9));
    }
    
    /**
     * Does basic format checks on the timings output with two probe classes.
     */
    @Test
    public void testSimpleTimingsTwoProbeClases() {
        enableProbes(true);
        
        PerformanceProbe p = new PerformanceProbe("C1");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        p = new PerformanceProbe("C1");
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        p = new PerformanceProbe("C2");
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        p = new PerformanceProbe("C2");
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        p.close();
        
        PerformanceProbe.printResult();
        
        String[] lines = getLogOutput().split("\n");
        assertThat(lines[0].endsWith("Performance Measurements:"), is(true));
        assertThat(lines[1].trim(), is("C1"));
        assertThat(lines[2].trim(), is("Time:"));
        assertThat(lines[3].trim(), is("Num Measures: 2"));
        assertThat(lines[4].trim().matches("Min: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[5].trim().matches("Med: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[6].trim().matches("Avg: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[7].trim().matches("Max: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[8].trim().matches("Sum: 00:00\\.[0-9]{3}"), is(true));
        
        assertThat(lines[9].trim(), is("C2"));
        assertThat(lines[10].trim(), is("Time:"));
        assertThat(lines[11].trim(), is("Num Measures: 2"));
        assertThat(lines[12].trim().matches("Min: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[13].trim().matches("Med: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[14].trim().matches("Avg: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[15].trim().matches("Max: 00:00\\.[0-9]{3}"), is(true));
        assertThat(lines[16].trim().matches("Sum: 00:00\\.[0-9]{3}"), is(true));
        
        assertThat(lines.length, is(17));
    }
    
    /**
     * Tests that extra data is captured correctly.
     */
    @Test
    public void testExtraData3Probes() {
        enableProbes(true);
        
        PerformanceProbe p = new PerformanceProbe("C1");
        p.addExtraData("e1", 1);
        p.close();
        
        p = new PerformanceProbe("C1");
        p.addExtraData("e1", 4);
        p.close();
        
        p = new PerformanceProbe("C1");
        p.addExtraData("e1", 10);
        p.close();
        
        PerformanceProbe.printResult();
        
        String[] lines = getLogOutput().split("\n");
        
        assertThat(lines[0].endsWith("Performance Measurements:"), is(true));
        assertThat(lines[1].trim(), is("C1"));
        // 2-8 are time measurements
        assertThat(lines[9].trim(), is("e1:"));
        assertThat(lines[10].trim(), is("Num Measures: 3"));
        assertThat(lines[11].trim(), is("Min: 1.0"));
        assertThat(lines[12].trim(), is("Med: 4.0"));
        assertThat(lines[13].trim(), is("Avg: 5.0"));
        assertThat(lines[14].trim(), is("Max: 10.0"));
        assertThat(lines[15].trim(), is("Sum: 15.0"));

        assertThat(lines.length, is(16));
    }
    
    /**
     * Tests that extra data is captured correctly.
     */
    @Test
    public void testExtraData2Probes() {
        enableProbes(true);
        
        PerformanceProbe p = new PerformanceProbe("C1");
        p.addExtraData("e1", 1);
        p.close();
        
        p = new PerformanceProbe("C1");
        p.addExtraData("e1", 3);
        p.close();
        
        PerformanceProbe.printResult();
        
        String[] lines = getLogOutput().split("\n");
        
        assertThat(lines[0].endsWith("Performance Measurements:"), is(true));
        assertThat(lines[1].trim(), is("C1"));
        // 2-8 are time measurements
        assertThat(lines[9].trim(), is("e1:"));
        assertThat(lines[10].trim(), is("Num Measures: 2"));
        assertThat(lines[11].trim(), is("Min: 1.0"));
        assertThat(lines[12].trim(), is("Med: 2.0"));
        assertThat(lines[13].trim(), is("Avg: 2.0"));
        assertThat(lines[14].trim(), is("Max: 3.0"));
        assertThat(lines[15].trim(), is("Sum: 4.0"));
        
        assertThat(lines.length, is(16));
    }
    
}
