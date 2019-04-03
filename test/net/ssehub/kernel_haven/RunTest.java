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
