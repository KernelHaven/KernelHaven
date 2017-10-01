package net.ssehub.kernel_haven.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.util.Logger;

/**
 * The global configuration. This class holds the complete user configuration that defines the pipeline.
 * 
 * @author Adam
 * @author Moritz
 */
public class Configuration implements IConfiguration {
    
    protected Properties properties;
    
    protected File propertyFile;
    
    // General
    
    protected File resourceDir;
    
    protected File outputDir;

    protected File pluginsDir;
    
    protected File logDir;

    protected boolean logConsole;
    
    protected boolean logFile;
    
    protected boolean logError;
    
    protected boolean logWarning;
    
    protected boolean logInfo;

    protected boolean logDebug;
    
    protected boolean archive;
    
    protected File archiveDir;
    
    protected boolean archiveSourceTree;
    
    // analysis
    
    protected String analysisClassName;
    
    // common extractor
    
    protected File sourceTree;
    
    protected String arch;
    
    // code
    
    protected String codeExtractorClassName;
    
    // build
    
    protected String buildExtractorClassName;
    
    // variability model
    
    protected String variabilityExtractorClassName;
    
    /**
     * Creates an empty configuration; useful for unit tests.
     */
    protected Configuration() {
    }
    
    /**
     * Creates a configuration from the given property file.
     * 
     * @param propertyFile The file to read the properties from. Must not be <code>null</code>.
     * 
     * @throws SetUpException If some properties are invalid or the file cannot be read.
     */
    public Configuration(File propertyFile) throws SetUpException {
        properties = new Properties();
        this.propertyFile = propertyFile;
        
        try {
            properties.load(new FileReader(propertyFile));
        } catch (IOException e) {
            throw new SetUpException(e);
        }
        
        init();
    }
    
    /**
     * Initializes the attributes from the properties.
     * 
     * @throws SetUpException If some properties are invalid.
     */
    private void init() throws SetUpException {
        // general
        this.resourceDir = readFileProperty("resource_dir",
                new FileProps().require().existing().dir().readable().writeable());
        this.outputDir = readFileProperty("output_dir",
                new FileProps().require().existing().dir().writeable());
        this.pluginsDir = readFileProperty("plugins_dir",
                new FileProps().require().existing().dir().readable());
        
        this.logConsole = Boolean.parseBoolean(getProperty("log.console", "true"));
        this.logFile = Boolean.parseBoolean(getProperty("log.file", "false"));
        this.logError = Boolean.parseBoolean(getProperty("log.error", "true"));
        this.logWarning = Boolean.parseBoolean(getProperty("log.warning", "true"));
        this.logInfo = Boolean.parseBoolean(getProperty("log.info", "true"));
        this.logDebug = Boolean.parseBoolean(getProperty("log.debug", "false"));
        FileProps logDirProps = new FileProps().existing().dir().writeable();
        if (this.logFile) {
            logDirProps = logDirProps.require();
        }
        this.logDir = readFileProperty("log.dir", logDirProps);
        
        this.archive = Boolean.parseBoolean(getProperty("archive", "false"));
        FileProps archiveDirProps = new FileProps().existing().dir().writeable();
        if (this.archive) {
            archiveDirProps = archiveDirProps.require();
        }
        this.archiveDir = readFileProperty("archive.dir", archiveDirProps);
        this.archiveSourceTree = Boolean.parseBoolean(getProperty("archive.source_tree", "false"));
        
        // analysis
        this.analysisClassName = readStringRequiredProperty("analysis.class");
        
        // common extractor stuff
        this.sourceTree =  readFileProperty("source_tree", new FileProps().existing().dir().readable());
        this.arch = getProperty("arch");
        
        // code
        this.codeExtractorClassName = getProperty("code.extractor.class");
        
        // build
        this.buildExtractorClassName = getProperty("build.extractor.class");
        
        // variabiltiy
        this.variabilityExtractorClassName = getProperty("variability.extractor.class");
        
    }
    
    /**
     * Properties that should be checked for files.
     */
    protected static class FileProps {
        
        private boolean required;
        
        private boolean existing;
        
        private boolean file;
        
        private boolean dir;
        
        private boolean readable;
        
        private boolean writeable;

        /**
         * Creates an empty file props.
         */
        public FileProps() {
        }
        
        /**
         * Sets the file to be required.
         * @return this.
         */
        public FileProps require() {
            required = true;
            return this;
        }
        
        /**
         * Sets the file to be existing.
         * @return this.
         */
        public FileProps existing() {
            existing = true;
            return this;
        }
        
//        /**
//         * Sets the file to be a file.
//         * @return this.
//         */
//        public FileProps file() {
//            file = true;
//            return this;
//        }
        
        /**
         * Sets the file to be a directory.
         * @return this.
         */
        public FileProps dir() {
            dir = true;
            return this;
        }
        
        /**
         * Sets the file to be a readable.
         * @return this.
         */
        public FileProps readable() {
            readable = true;
            return this;
        }
        
        /**
         * Sets the file to be a writeable.
         * @return this.
         */
        public FileProps writeable() {
            writeable = true;
            return this;
        }
        
    }
    
    /**
     * Reads the given property name into a file.
     * 
     * @param name The property name.
     * @param requiredProps The properties that should be checked on the file.
     * @return The file, or <code>null</code> if not specified and not required.
     * 
     * @throws SetUpException If any of the properties of the file are not satisfied.
     */
    protected File readFileProperty(String name, FileProps requiredProps) throws SetUpException {
        File result = null;
        
        String setting = properties.getProperty(name);
        if (setting != null) {
            
            result = new File(setting);
            if (requiredProps.existing && !result.exists()) {
                throw new SetUpException("File \"" + setting + "\" does not exist");
            }
            
            // check the following only if the file exists...
            
            if (result.exists() && requiredProps.file && !result.isFile()) {
                throw new SetUpException("File \"" + setting + "\" is not an existing file");
            }
            
            if (result.exists() && requiredProps.dir && !result.isDirectory()) {
                throw new SetUpException("File \"" + setting + "\" is not an existing directory");
            }
            
            if (result.exists() && requiredProps.readable && !result.canRead()) {
                throw new SetUpException("File \"" + setting + "\" is not readable");
            }
            
            if (result.exists() && requiredProps.writeable && !result.canWrite()) {
                throw new SetUpException("File \"" + setting + "\" is not writeable");
            }
            
        } else if (requiredProps.required) {
            throw new SetUpException("Setting " + name + " missing, but required");
        }
        
        return result;
    }
    
    /**
     * Reads a required string property.
     * 
     * @param key The property to read.
     * @return The value of the property.
     * @throws SetUpException If the property is not defined.
     */
    protected String readStringRequiredProperty(String key) throws SetUpException {
        String result = getProperty(key);
        if (result == null) {
            throw new SetUpException("Setting " + key + " required but not defined");
        }
        return result;
    }
    
    /**
     * Reads a long property.
     * 
     * @param key The property to read.
     * @param defaultValue The value to return if the setting is not defined.
     * @return The value of the property as a long, or the default value.
     * @throws SetUpException If the property is not a valid long.
     */
    protected long readLong(String key, long defaultValue) throws SetUpException {
        long result = defaultValue;
        String value = getProperty(key);
        
        if (value != null) {
            try {
                result = Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new SetUpException("Setting " + key + " is not a valid number");
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
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Reads a property from the user configuration file.
     * 
     * @param key The key of the property.
     * @param defaultValue The default value to return if not specified in file.
     * @return The value set by the user, or the default value.
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Reads a property from the user configuration file and returns a boolean value.<br/>
     * Will return:
     * <ol>
     *   <li>The <tt>defaultValue</tt> if the property was not defined</li>
     *   <li><tt>true</tt> if specified value was <tt>"true"</tt> (ignoring case)</li>
     *   <li><tt>false</tt> else</li>
     * </ol>
     * @param key The key of the property.
     * @param defaultValue The default value to return if not specified in file.
     * @return The value set by the user, or the default value.
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        boolean result = defaultValue;
        String value = properties.getProperty(key);
        if (null != value) {
            result = Boolean.valueOf(value);
        }
        
        return result;
    }
    
    /**
     * Reads a property from the user configuration and returns an enum value.<br/>
     * Will return:
     * <ol>
     *   <li>The <tt>defaultValue</tt> if the property was not defined</li>
     *   <li>The specified enum value if the specified value is a valid literal (case insensitive).</li>
     *   <li>An {@link SetUpException} if the specified value does not exist.</li>
     * </ol>
     * @param key The key of the property.
     * @param defaultValue The default value to return if not specified in file.
     * @param <E> The enum type for which an literal shall be returned.
     * @return The specified value or the default value.
     * @throws SetUpException If a value was specified, which does not exist for the defined enumeration.
     */
    public <E extends Enum<E>> E getEnumProperty(String key, E defaultValue) throws SetUpException {
        String tmp = getProperty(key);
        
        E result;
        if (null == tmp) {
            Logger.get().logInfo("\"" + key + "\" not defined, will use \"" + defaultValue.name() + "\".");
            result = defaultValue;
        } else {
            try {
                result = Enum.valueOf(defaultValue.getDeclaringClass(), tmp.toUpperCase());
            } catch (IllegalArgumentException exc) {
                throw new SetUpException("\"" + key + "=" + tmp + "\" is an invalid option.");
            }
        }
        
        return result;
    }

    /**
     * Returns the output directory where analyzes can place their output. We have write access here.
     * 
     * @return The output directory, never <code>null</code>.
     */
    public File getOutputDir() {
        return outputDir;
    }

    /**
     * Returns the directory where to read the plugin jars from. We have read access here.
     * 
     * @return The plugin directory, never <code>null</code>.
     */
    public File getPluginsDir() {
        return pluginsDir;
    }

    /**
     * The directory where log files will be placed. If present, then we have write access here.
     * This is present, if logFile is true.
     * 
     * @return The directory for log files; may be <code>null</code>.
     */
    public File getLogDir() {
        return logDir;
    }

    /**
     * Whether to log to console or not.
     * 
     * @return Whether to log to console or not.
     */
    public boolean isLogConsole() {
        return logConsole;
    }

    /**
     * Whether to log to a log file or not. If this is <code>true</code>, then the logDir exists.
     * 
     * @return Whether to log to console or not.
     */
    public boolean isLogFile() {
        return logFile;
    }

    /**
     * Whether to log log messages with error level.
     * 
     * @return Whether to log error messages.
     */
    public boolean isLogError() {
        return logError;
    }

    /**
     * Whether to log log messages with warning level.
     * 
     * @return Whether to log warning messages.
     */
    public boolean isLogWarning() {
        return logWarning;
    }

    /**
     * Whether to log log messages with info level.
     * 
     * @return Whether to log info messages.
     */
    public boolean isLogInfo() {
        return logInfo;
    }

    /**
     * Whether to log log messages with debug level.
     * 
     * @return Whether to log debug messages.
     */
    public boolean isLogDebug() {
        return logDebug;
    }

    /**
     * Whether the pipeline should archive itself after running.
     * 
     * @return Whether to archive the pipeline.
     */
    public boolean isArchive() {
        return archive;
    }

    /**
     * The directory where to store the archive in. This is present if isArchive is true.
     * If present, then we have write access.
     * 
     * @return The directory where to place the archive.
     */
    public File getArchiveDir() {
        return archiveDir;
    }
    
    /**
     * Whether the source tree should be added to the archive, too.
     * 
     * @return Whether the source tree should be archived, too.
     */
    public boolean isArchiveSourceTree() {
        return archiveSourceTree;
    }

    /**
     * Returns the fully qualified class name of the analysis class.
     * 
     * @return The name of the analysis. Never <code>null</code>.
     */
    public String getAnalysisClassName() {
        return analysisClassName;
    }

    /**
     * The extractor class to use.
     * 
     * @return The name of the extractor class. May be <code>null</code>.
     */
    public String getCodeExtractorClassName() {
        return codeExtractorClassName;
    }

    /**
     * The extractor class to use.
     * 
     * @return The name of the extractor class. May be <code>null</code>.
     */
    public String getBuildExtractorClassName() {
        return buildExtractorClassName;
    }

    /**
     * The extractor class to use.
     * 
     * @return The name of the extractor class. May be <code>null</code>.
     */
    public String getVariabilityExtractorClassName() {
        return variabilityExtractorClassName;
    }

    /**
     * Overrides the archive setting.
     * 
     * @param archive The new archive value.
     */
    public void setArchive(boolean archive) {
        this.archive = archive;
    }
    
    /**
     * Returns the file that this configuration was created with.
     * 
     * @return The file with the properties; never <code>null</code>.
     */
    public File getPropertyFile() {
        return propertyFile;
    }
    
    /**
     * Sets the property file. Used in test cases.
     * 
     * @param propertyFile The property file.
     */
    protected void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }
    
    /**
     * Returns the directory that contains the product line to analyze.
     * 
     * @return The source tree directory.
     */
    public File getSourceTree() {
        return sourceTree;
    }
    
    /**
     * Creates a {@link BuildExtractorConfiguration} from this global configuration.
     * 
     * @return The {@link BuildExtractorConfiguration} derived from this global configuration.
     * 
     * @throws SetUpException If the configuration is not valid.
     */
    public BuildExtractorConfiguration getBuildConfiguration() throws SetUpException {
        BuildExtractorConfiguration config = new BuildExtractorConfiguration();
        
        config.setProviderTimeout(readLong("build.provider.timeout", 0));
        config.setCacheWrite(Boolean.parseBoolean(getProperty("build.provider.cache.write", "false")));
        config.setCacheRead(Boolean.parseBoolean(getProperty("build.provider.cache.read", "false")));
        config.setSourceTree(sourceTree);
        config.setArch(arch);
        
        FileProps cacheProps = new FileProps().existing().dir().readable().writeable();
        if (config.isCacheRead() || config.isCacheWrite()) {
            cacheProps = cacheProps.require();
        }
        config.setCacheDir(readFileProperty("cache_dir", cacheProps));
        config.setResourceDir(resourceDir);
        config.setProperties(properties);
        
        return config;
    }
    
    /**
     * Creates a {@link VariabilityExtractorConfiguration} from this global configuration.
     * 
     * @return The {@link VariabilityExtractorConfiguration} derived from this global configuration.
     * 
     * @throws SetUpException If the configuration is not valid.
     */
    public VariabilityExtractorConfiguration getVariabilityConfiguration() throws SetUpException {
        VariabilityExtractorConfiguration config = new VariabilityExtractorConfiguration();
        
        config.setProviderTimeout(readLong("variability.provider.timeout", 0));
        config.setCacheWrite(Boolean.parseBoolean(getProperty("variability.provider.cache.write", "false")));
        config.setCacheRead(Boolean.parseBoolean(getProperty("variability.provider.cache.read", "false")));
        config.setSourceTree(sourceTree);
        config.setArch(arch);
        
        FileProps cacheProps = new FileProps().existing().dir().readable().writeable();
        if (config.isCacheRead() || config.isCacheWrite()) {
            cacheProps = cacheProps.require();
        }
        config.setCacheDir(readFileProperty("cache_dir", cacheProps));
        config.setResourceDir(resourceDir);
        config.setProperties(properties);
        
        return config;
    }
    
    /**
     * Creates a {@link CodeExtractorConfiguration} from this global configuration.
     * 
     * @return The {@link CodeExtractorConfiguration} derived from this global configuration.
     * 
     * @throws SetUpException If the configuration is not valid.
     */
    public CodeExtractorConfiguration getCodeConfiguration() throws SetUpException {
        CodeExtractorConfiguration config = new CodeExtractorConfiguration();
        
        config.setProviderTimeout(readLong("code.provider.timeout", 0));
        config.setCacheWrite(Boolean.parseBoolean(getProperty("code.provider.cache.write", "false")));
        config.setCacheRead(Boolean.parseBoolean(getProperty("code.provider.cache.read", "false")));
        
        String fileListSetting = getProperty("code.extractor.files", "");
        String[] fileListSettingParts = fileListSetting.split(",");
        File[] files = new File[fileListSettingParts.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(fileListSettingParts[i].trim());
        }
        config.setFiles(files);
        try {
            Pattern pattern = Pattern.compile(getProperty("code.extractor.file_regex", ".*\\.c"));
            config.setFilenamePattern(pattern);
        } catch (PatternSyntaxException e) {
            throw new SetUpException(e);
        }
        
        config.setThreads((int) readLong("code.extractor.threads", 1));
        
        config.setSourceTree(sourceTree);
        config.setArch(arch);
        
        FileProps cacheProps = new FileProps().existing().dir().readable().writeable();
        if (config.isCacheRead() || config.isCacheWrite()) {
            cacheProps = cacheProps.require();
        }
        config.setCacheDir(readFileProperty("cache_dir", cacheProps));
        config.setResourceDir(resourceDir);
        config.setProperties(properties);
        
        return config;
    }

}
