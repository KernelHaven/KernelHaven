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
package net.ssehub.kernel_haven.test_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;


/**
 * Utilities to assert the contents of a file.
 * @author El-Sharkawy
 *
 */
public class FileContentsAssertion {
    
    /**
     * Tests line for line that the file contains the expected content.
     * @param actualFile The file to test.
     * @param expectedContent The expected content (line breaks and surrounding whitespaces won't be tested).
     */
    public static void assertContents(File actualFile, String expectedContent) {
        try (BufferedReader fIn = new BufferedReader(new FileReader(actualFile))) {
            String[] expectedLines = expectedContent.split("\\r?\\n");
            int actualLine = 0;
            String line;
            while ((line = fIn.readLine()) != null) {
                if (actualLine < expectedLines.length) {
                    Assert.assertEquals("Line " + actualLine + " is not as expected." ,
                        expectedLines[actualLine].trim(),
                        line.trim());
                    actualLine++;
                }
            }
        } catch (IOException ioExc) {
            Assert.fail("Could not read destination file: " + ioExc.getMessage());
        }
    }

}
