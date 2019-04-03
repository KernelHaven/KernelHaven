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

import java.io.File;
import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A configuration that does no consistency checks. Useful for test cases.
 * 
 * @author Adam
 * @author Moritz
 *
 */
public class TestConfiguration extends Configuration {

    /**
     * Creates a test configuration with no consistency checks.
     * 
     * @param properties The properties to generate this from.
     * 
     * @throws SetUpException Never thrown.
     */
    public TestConfiguration(@NonNull Properties properties) throws SetUpException {
        super(properties, false);
        
        DefaultSettings.registerAllSettings(this);
    }
    
    @Override
    public void setPropertyFile(File propertyFile) {
        super.setPropertyFile(propertyFile);
    }
    
}
