package net.ssehub.kernel_haven.config;

/**
 * Parent interface for the general configuration and the specific configurations of the extractor pipelines.
 * @author El-Sharkawy
 *
 */
public interface IConfiguration {

    /**
     * Reads a property from the user configuration file.
     * 
     * @param key The key of the property.
     * @return The value set for the key, or <code>null</code> if not specified.
     */
    public String getProperty(String key);
    
    /**
     * Reads a property from the user configuration file.
     * 
     * @param key The key of the property.
     * @param defaultValue The default value to return if not specified in file.
     * @return The value set by the user, or the default value.
     */
    public String getProperty(String key, String defaultValue);
}
