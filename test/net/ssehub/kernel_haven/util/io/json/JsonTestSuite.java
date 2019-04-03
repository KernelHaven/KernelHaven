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
package net.ssehub.kernel_haven.util.io.json;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the {@link JsonParser} with a
 * <a href="https://github.com/nst/JSONTestSuite">test suite from Nicolas Seriot</a>.
 *
 * @author Adam
 */
@RunWith(Parameterized.class)
public class JsonTestSuite {

    private static final File TESTDATA = new File("testdata/json/test_suite");
    
    private @NonNull File file;
    
    /**
     * Creates this test instance.
     * 
     * @param file The file to run on.
     */
    public JsonTestSuite(@NonNull File file) {
        this.file = file;
    }
    
    /**
     * Creates the parameters for this test.
     * 
     * @return The parameters of this test.
     * 
     * @throws IOException unwanted. 
     */
    @Parameters(name = "{0}")
    public static Object[] getParameters() throws IOException {
        return Files.walk(TESTDATA.toPath())
                .map((path) -> path.toFile())
                .filter((file) -> file.isFile())
                .filter((file) -> file.getName().endsWith(".json"))
                .sorted()
                .toArray();
    }
    
    /**
     * Executes the actual test.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void test() throws IOException {
        // n_ -> must throw exception
        // y_ -> must not throw exception
        // i_ -> implementation defined (may or may not throw)
        boolean expectedException = file.getName().startsWith("n_");
        boolean bothAllowed = file.getName().startsWith("i_");
        
        try (JsonParser parser = new JsonParser(file)) {
            
            parser.parse();
            
            if (!bothAllowed && expectedException) {
                fail("Didn't get exception");
            }
            
        } catch (FormatException e) {
            if (!bothAllowed && !expectedException) {
                fail("Got exception: " + e.getMessage());
            }
        }
    }
    
}
