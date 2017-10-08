package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.config.Setting.Type.ENUM;

/**
 * A setting for values based on a Java enum.
 *
 * @param <E> The type of enum that this setting represents.
 *
 * @author Adam
 */
public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private Class<E> enumClass;
    
    /**
     * Creates an enum setting.
     * 
     * @param key The key in the properties file.
     * @param enumClass The enum that contains the values that this setting should represent.
     * @param mandatory Whether this setting is mandatory. If this is <code>true</code>>, the properties file does not
     *      contain this key and the default value is <code>null</code>, then an exception is thrown when the setting
     *      is registered.
     * @param defaultValue The default value to use if the key is not specified in the properties. May be
     *      <code>null</code>, in which case no default value is used.
     * @param description The description of this setting. Currently not used, but should be provided for documentation
     *      purposes.
     */
    public EnumSetting(String key, Class<E> enumClass, boolean mandatory, E defaultValue, String description) {
        super(key, ENUM, mandatory, defaultValue != null ? defaultValue.name() : null, description);
        this.enumClass = enumClass;
    }
    
    /**
     * Returns the enum class that this setting represents.
     * 
     * @return The enum class.
     */
    public Class<E> getEnumClass() {
        return enumClass;
    }

}
