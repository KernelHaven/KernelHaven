package net.ssehub.kernel_haven.test_utils;

import java.io.File;
import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;

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
    public TestConfiguration(Properties properties) throws SetUpException {
        super(properties, false);
        
        DefaultSettings.registerAllSettings(this);
    }
    
    @Override
    public void setPropertyFile(File propertyFile) {
        super.setPropertyFile(propertyFile);
    }
    
}
