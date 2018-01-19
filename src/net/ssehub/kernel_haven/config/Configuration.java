package net.ssehub.kernel_haven.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Setting.Type;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * The global configuration. This class holds the complete user configuration that defines the pipeline.
 * 
 * @author Adam
 * @author Moritz
 */
public class Configuration {
    
    private @NonNull Properties properties;
    
    private @Nullable File propertyFile;
    
    private @NonNull Map<String, Object> values;
    
    private @NonNull Map<String, Setting<?>> settings;
    
    private boolean doChecks;
    
    /**
     * Creates a configuration from the given properties.
     * 
     * @param propreties The properties. Must not be <code>null</code>.
     */
    public Configuration(@NonNull Properties propreties) {
        this.properties = propreties;
        this.values = new HashMap<>();
        this.settings = new HashMap<>();
        this.doChecks = true;
    }
    
    /**
     * Creates a configuration from the given properties. Additionally, allows to disable all checks on setting
     * constraints. Should only be used by test cases.
     * 
     * @param propreties The properties. Must not be <code>null</code>.
     * @param doChecks Whether to check constraints for setting values or not.
     */
    protected Configuration(@NonNull Properties propreties, boolean doChecks) {
        this.properties = propreties;
        this.values = new HashMap<>();
        this.settings = new HashMap<>();
        this.doChecks = doChecks;
    }
    
    /**
     * Creates a configuration from the given property file.
     * 
     * @param propertyFile The file to read the properties from. Must not be <code>null</code>.
     * 
     * @throws SetUpException If some properties are invalid or the file cannot be read.
     */
    public Configuration(@NonNull File propertyFile) throws SetUpException {
        this.properties = new Properties();
        this.values = new HashMap<>();
        this.settings = new HashMap<>();
        this.doChecks = true;
        this.propertyFile = propertyFile;
        
        try (FileReader in = new FileReader(propertyFile)) {
            properties.load(in);
        } catch (IOException e) {
            throw new SetUpException(e);
        }
    }
    
    /**
     * Registers the given setting and reads, stores and checks the value for it. After this, the value for the
     * setting may be retrieved via {@link #getValue(Setting)}.
     * 
     * @param setting The setting to register. Must not be <code>null</code>.
     * 
     * @throws SetUpException If the constraints for a setting are not satisfied, or a different setting with a same
     *      key was already registered.
     */
    public void registerSetting(@NonNull Setting<?> setting) throws SetUpException {
        String key = setting.getKey();
        if (settings.containsKey(key)) {
            if (settings.get(key) != setting) {
                throw new SetUpException("Setting with key " + key + " already registered");
            } else {
                return;
            }
        }
        settings.put(key, setting);
        
        Object value;
        if (setting.getType() == Type.SETTING_LIST) {
            value = readSettingList(setting);
        } else if (setting.getType() == Type.ENUM) {
            value = readEnumSetting(setting);
        } else {
            value = readValue(setting);
        }
        values.put(key, value);
    }
    
    /**
     * Retrieves the value that was stored for the given setting.
     * 
     * @param <T> The type of value that the setting represents.
     * 
     * @param setting The setting to retrieve the value for. Must not be <code>null</code>.
     * @return The value for the setting. If the setting is not mandatory, then this may be <code>null</code>.
     * 
     * @throws IllegalArgumentException If the setting has not been registered before this call.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(@NonNull Setting<T> setting) throws IllegalArgumentException {
        if (!values.containsKey(setting.getKey())) {
            throw new IllegalArgumentException("Can't access setting that is not yet registered");
        }
        return (T) values.get(setting.getKey());
    }
    
    /**
     * Changes the value of a given setting. The constraints of the setting are <b>not</b> checked.
     * 
     * @param <T> The type of value that this setting represents.
     * 
     * @param setting The setting to change the value for.
     * @param value The new value for the setting.
     * 
     * @throws IllegalArgumentException If the setting has not been registered before this call.
     */
    public <T> void setValue(@NonNull Setting<T> setting, @Nullable T value) throws IllegalArgumentException {
        if (!values.containsKey(setting.getKey())) {
            throw new IllegalArgumentException("Can't set setting that is not yet registered");
        }
        values.put(setting.getKey(), value);
    }
    
    /**
     * Reads a value from the properties, based on the key, type and value of the given setting.
     * 
     * @param setting The setting to read the value for.
     * @return The value for the given setting.
     * 
     * @throws SetUpException If any constraints of the setting are violated.
     */
    private @Nullable Object readValue(@NonNull Setting<?> setting) throws SetUpException {
        Object result;
        String key = setting.getKey();
        String value = properties.getProperty(key, setting.getDefaultValue());
        if (value == null) {
            if (setting.isMandatory() && doChecks) {
                throw new SetUpException("No value for mandatory setting " + key);
            }
            result = null;
        } else {
            switch (setting.getType()) {
            case STRING:
                result = value;
                break;
            case INTEGER:
                try {
                    result = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new SetUpException("Invalid value for integer setting " + key, e);
                }
                break;
            case DIRECTORY: {
                File f = new File(value);
                if (!f.isDirectory() && doChecks) {
                    throw new SetUpException("Value for setting " + key + " is not an existing directory"); 
                }
                result = f;
                break;
            }
            case FILE: {
                File f = new File(value);
                if (!f.isFile() && doChecks) {
                    throw new SetUpException("Value for setting " + key + " is not an existing file"); 
                }
                result = f;
                break;
            }
            case PATH:
                result = new File(value);
                break;
            case BOOLEAN:
                result = Boolean.parseBoolean(value);
                break;
            case REGEX:
                try {
                    result = Pattern.compile(value);
                } catch (PatternSyntaxException e) {
                    throw new SetUpException("Invalid regular expression for setting " + key, e);
                }
                break;
            case STRING_LIST: {
                List<String> list = new LinkedList<String>();
                for (String v : value.split(",")) {
                    list.add(v.trim());
                }
                result = list;
                break;
            }
            default:
                throw new SetUpException("Unknown setting type " + setting.getType() + " for setting " + key);
            }
        }
        return result;
    }
    
    /**
     * Reads a setting that has the type {@link Setting.Type#SETTING_LIST}.
     * 
     * @param setting The setting to read.
     * @return The read list.
     */
    private @NonNull List<String> readSettingList(@NonNull Setting<?> setting) {
        List<String> result = new LinkedList<>();
        int index = 0;
        String baseKey = setting.getKey();
        
        String value;
        while ((value = properties.getProperty(baseKey + "." + index)) != null) {
            result.add(value);
            index++;
        }
        
        return result;
    }
    
    /**
     * Reads a setting that has the type {@link Setting.Type#ENUM}.
     * 
     * @param setting The setting to read.
     * @return The read enum value.
     * 
     * @throws SetUpException If reading the enum value fails.
     */
    private @Nullable Object readEnumSetting(@NonNull Setting<?> setting) throws SetUpException {
        String key = setting.getKey();
        
        if (!(setting instanceof EnumSetting)) {
            throw new SetUpException("Setting with key " + key
                    + " has type ENUM but is not an instance of EnumSetting");
        }
        
        EnumSetting<?> enumSetting = (EnumSetting<?>) setting;
        
        String value = properties.getProperty(key, setting.getDefaultValue());
        
        Object result;
        
        if (value == null) {
            if (setting.isMandatory() && doChecks) {
                throw new SetUpException("No value for mandatory setting " + key);
            }
            result = null;
        } else {
            try {
                result = Enum.valueOf(enumSetting.getEnumClass(), value.toUpperCase());
            } catch (IllegalArgumentException exc) {
                throw new SetUpException("Invalid enum value for setting with key " + key + ": " + value);
            }
        }
        
        return result;
        
    }
    
    /**
     * Reads a property from the user configuration file.
     * 
     * @param key The key of the property.
     * @return The value set for the key, or <code>null</code> if not specified.
     */
    @Deprecated
    public @Nullable String getProperty(@NonNull String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Reads a property from the user configuration file.
     * 
     * @param key The key of the property.
     * @param defaultValue The default value to return if not specified in file.
     * @return The value set by the user, or the default value.
     */
    @Deprecated
    public @NonNull String getProperty(@NonNull String key, @NonNull String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Returns the file that this configuration was created with.
     * 
     * @return The file with the properties.
     */
    public @Nullable File getPropertyFile() {
        return propertyFile;
    }
    
    /**
     * Sets the property file. Used in test cases.
     * 
     * @param propertyFile The property file.
     */
    protected void setPropertyFile(@Nullable File propertyFile) {
        this.propertyFile = propertyFile;
    }
    
}
