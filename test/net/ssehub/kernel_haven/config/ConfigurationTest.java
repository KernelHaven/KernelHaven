package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.config.Setting.Type.BOOLEAN;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;

/**
 * Tests the {@link Configuration}.
 * @author El-Sharkawy
 *
 */
public class ConfigurationTest {

    /**
     * Tests that Boolean config values are handled correctly by the {@link Configuration}.
     * @throws SetUpException Must not occur, is not tested by this test.
     */
    @Test
    public void testBooleanValues() throws SetUpException {
        Setting<Boolean> falseValue = new Setting<>("value.false", BOOLEAN, false, "false", "");
        Setting<Boolean> trueValue = new Setting<>("value.true", BOOLEAN, false, "true", "");
        Setting<Boolean> overwriteableValue = new Setting<>("value.x", BOOLEAN, false, "true", "");
        
        Properties prop = new Properties();
        prop.setProperty(overwriteableValue.getKey(), "false");
        Configuration config = new Configuration(prop);
        config.registerSetting(falseValue);
        config.registerSetting(trueValue);
        config.registerSetting(overwriteableValue);
        
        boolean value = config.getValue(falseValue);
        Assert.assertFalse("False value is treated as true", value);
        
        value = config.getValue(trueValue);
        Assert.assertTrue("True value is treated as false", value);
        
        value = config.getValue(overwriteableValue);
        Assert.assertFalse("False value is treated as true", value);
    }
}
