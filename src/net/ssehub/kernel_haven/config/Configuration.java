package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Setting.Type;
import net.ssehub.kernel_haven.util.Logger;
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
        this.values = new HashMap<>();
        this.settings = new HashMap<>();
        this.doChecks = true;
        this.propertyFile = propertyFile;
        
        try {
            this.properties = loadFile(propertyFile); 
        } catch (IOException e) {
            throw new SetUpException(e);
        }
    }
    
    /**
     * Loads the given file. Recursively loads nested files.
     * 
     * @param file The file to load.
     * 
     * @return The read properties.
     * 
     * @throws IOException If loading any file fails.
     * @throws SetUpException If an include file is misconfigured.
     */
    private @NonNull Properties loadFile(@NonNull File file) throws IOException, SetUpException {
        Properties properties = new Properties();
        properties.load(new FileReader(file));

        // load all include_files.* properties
        List<@NonNull Properties> includedProps = new ArrayList<>();
        for (int index = 0; /*break will be called in the body*/; index++) {
            String key = DefaultSettings.INCLUDE_FILE.getKey() + "." + index;
            String includeFileString = properties.getProperty(key);
            properties.remove(key); // always remove the include_file setting
            
            if (includeFileString != null) {
                File includeFile = new File(file.getParentFile(), includeFileString);
                if (!includeFile.isFile()) {
                    throw new SetUpException(key + " in " + file + " points to an "
                            + "invalid file location: " + includeFileString);
                }
                
                // load included file
                Logger.get().logDebug2("Loading included setting file ", includeFile);
                includedProps.add(loadFile(includeFile));
            } else {
                break;
            }
        }
        
        // merge all included props with already loaded properties
        // previously loaded keys have precedence
        // go in reverse order, so that higher numbers in setting keys have precedence
        for (int i = includedProps.size() - 1; i >= 0; i--) {
            for (Map.Entry<Object, Object> entry : notNull(includedProps.get(i)).entrySet()) {
                properties.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        
        return properties;
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
            value = notNull(value.trim());
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
                result = readFileValue(key, value);
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
     * Reads a {@link Setting.Type#FILE} value. The value can either be an abosulte, working directory relative or
     * source_tree relative path.
     * 
     * @param key The key of the setting (used for exception message).
     * @param value The value of the setting.
     * @return A {@link File} specified by the value.
     * 
     * @throws SetUpException If checks are enabled and no such file exists.
     */
    private @Nullable File readFileValue(@NonNull String key, @NonNull String value) throws SetUpException {
        File f = new File(value);
        if (!f.isFile()) {
            
            // if the value isn't absolute and we have a source_tree setting, then try to create the file
            // relative to the source tree
            if (!f.isAbsolute() && values.containsKey(DefaultSettings.SOURCE_TREE.getKey())) {
                f = new File(getValue(DefaultSettings.SOURCE_TREE), f.getPath());
            }
            
            // we still haven't found a valid file
            if (!f.isFile()) {
                if (doChecks) {
                    // if checks are enable, throw an exception
                    throw new SetUpException("Value for setting " + key + " is not an existing file"); 
                } else {
                    // else return to the original; we don't want to have files always relative to source_tree
                    // if checks are disabled
                    f = new File(value);
                }
            }
        }
        return f;
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
        return notNull(properties.getProperty(key, defaultValue)); // not null because defaultValue is not null
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
