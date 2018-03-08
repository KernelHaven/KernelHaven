package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.config.Setting.Type.BOOLEAN;
import static net.ssehub.kernel_haven.config.Setting.Type.DIRECTORY;
import static net.ssehub.kernel_haven.config.Setting.Type.FILE;
import static net.ssehub.kernel_haven.config.Setting.Type.INTEGER;
import static net.ssehub.kernel_haven.config.Setting.Type.REGEX;
import static net.ssehub.kernel_haven.config.Setting.Type.SETTING_LIST;
import static net.ssehub.kernel_haven.config.Setting.Type.STRING;
import static net.ssehub.kernel_haven.config.Setting.Type.STRING_LIST;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.analysis.ConfiguredPipelineAnalysis;
import net.ssehub.kernel_haven.build_model.EmptyBuildModelExtractor;
import net.ssehub.kernel_haven.code_model.EmptyCodeModelExtractor;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.EmptyVariabilityModelExtractor;

/**
 * All settings that are used in the main infrastructure.
 *
 * @author Adam
 */
public class DefaultSettings {
    
    // CHECKSTYLE:OFF
    
    /*
     * Infrastructure directories
     */
    
    public static final @NonNull Setting<@NonNull File> RESOURCE_DIR = new Setting<>("resource_dir", DIRECTORY, true, null, "The path where extractors can store their resources. The extractors create sub-folders in this called the same as their fully qualified class names (to prevent conflicts). This has to be always set to a valid directory with write and read access.");
    public static final @NonNull Setting<@NonNull File> OUTPUT_DIR = new Setting<>("output_dir", DIRECTORY, true, null, "The path where the output files of the analysis will be stored. This has to be always set to a valid directory with write access.");
    public static final @NonNull Setting<@NonNull File> PLUGINS_DIR = new Setting<>("plugins_dir", DIRECTORY, true, null, "The path where plugin .jars are loaded from. Every .jar in this directory is loaded into the JVM. This has to be always set to a valid directory with read access.");
    public static final @NonNull Setting<@NonNull File> CACHE_DIR = new Setting<>("cache_dir", DIRECTORY, true, null, "This is the directory where the providers will write and read their cache. This has to be set to a valid directory with write and read access.");
    public static final @NonNull Setting<@NonNull File> LOG_DIR = new Setting<>("log.dir", DIRECTORY, true, ".", "The path where log files will be written. This has to be set to a valid directory with write access.");
    public static final @NonNull Setting<@NonNull File> ARCHIVE_DIR = new Setting<>("archive.dir", DIRECTORY, true, ".", "Directory to write the archive of the infrastrucure execution to. This has to be set to a valid directory with write access.");
    
    /*
     * Logging
     */
    
    public static final @NonNull Setting<@NonNull Boolean> LOG_CONSOLE = new Setting<>("log.console", BOOLEAN, true, "true", "If set to true all log messages will be written to console.");
    public static final @NonNull Setting<@NonNull Boolean> LOG_FILE = new Setting<>("log.file", BOOLEAN, true, "false", "If set to true all log messages will be written to a file in the log directory.");
    public static final @NonNull Setting<Logger.@NonNull Level> LOG_LEVEL = new EnumSetting<Logger.@NonNull Level>("log.level", Logger.Level.class, true, Logger.Level.INFO, "Defines the maximum log level to log."); 
    
    /*
     * Archiving
     */

    public static final @NonNull Setting<@NonNull Boolean> ARCHIVE = new Setting<>("archive", BOOLEAN, true, "false", "If set to true the infrastructure will archive itself, plugins, results, configuration and logs after the execution is finished. Alternative to the --archive command line parameter.");
    public static final @NonNull Setting<@NonNull Boolean> ARCHIVE_SOURCE_TREE = new Setting<>("archive.source_tree", BOOLEAN, true, "false", "Defines whether the source tree should be included in the archive.");
    public static final @NonNull Setting<@NonNull Boolean> ARCHIVE_CACHE_DIR = new Setting<>("archive.cache_dir", BOOLEAN, true, "false", "Defines whether the cache directory should be included in the archive.");
    public static final @NonNull Setting<@NonNull Boolean> ARCHIVE_RES_DIR = new Setting<>("archive.res_dir", BOOLEAN, true, "false", "Defines whether the ressource directory should be included in the archive.");
    
    /*
     * Analysis
     */
    
    public static final @NonNull Setting<@NonNull String> ANALYSIS_CLASS = new Setting<>("analysis.class", STRING, true, null, "The fully qualified class name of the analysis that should be run.");
    public static final @NonNull Setting<@NonNull List<String>> ANALYSIS_COMPONENTS_LOG = new Setting<>("analysis.output.intermediate_results", STRING_LIST, true, "", "Specifies which analysis components (simple class name) of a PipelineAnalysis should output their intermediate results. These will be written in addition to the result of the main component.");
    public static final @NonNull Setting<@NonNull String> ANALYSIS_PIPELINE = new Setting<>("analysis.pipeline", STRING, true, "", "A string specifying a pipeline of analyis components. This only has an effect if " + ANALYSIS_CLASS.getKey() + " is set to " + ConfiguredPipelineAnalysis.class.getName() + "."); // TODO specify format
    public static final @NonNull Setting<@NonNull String> ANALYSIS_RESULT = new Setting<>("analysis.output.type", STRING, false, "csv", "A file suffix that specifies which kind of output writer shall be used. If IOUtils is used, then xls or xlsx can be used here.");
    public static final @NonNull Setting<@NonNull Boolean> ANALYSIS_USE_VARMODEL_VARIABLES_ONLY = new Setting<>("analysis.consider_vm_vars_only", BOOLEAN, true, "false", "Defines whether the analysis should only consider variables that are present in the variability model.");
    
    /*
     * Common extractor parameters
     */
    
    public static final @NonNull Setting<@NonNull File> SOURCE_TREE = new Setting<>("source_tree", DIRECTORY, true, null, "The path to the source tree of the product line that should be analyzed.");
    public static final @NonNull Setting<@Nullable String> ARCH = new Setting<>("arch", STRING, false, null, "The architecture of the Linux Kernel that should be analyzed. Most Linux extractors require this.");
    
    /*
     * Code model parameters
     */
    
    public static final @NonNull Setting<@NonNull String> CODE_EXTRACTOR_CLASS = new Setting<>("code.extractor.class", STRING, true, EmptyCodeModelExtractor.class.getName(), "The fully qualified class name of the extractor for the code model.");
    public static final @NonNull Setting<@NonNull Integer> CODE_PROVIDER_TIMEOUT = new Setting<>("code.provider.timeout", INTEGER, true, "0", "The maximum time the provider waits for the results of the extractor until an exception is thrown. In milliseconds; 0 = no timeout used.");
    public static final @NonNull Setting<@NonNull Boolean> CODE_PROVIDER_CACHE_WRITE = new Setting<>("code.provider.cache.write", BOOLEAN, true, "false", "Defines whether the code model provider will write its results to the cache directory.");
    public static final @NonNull Setting<@NonNull Boolean> CODE_PROVIDER_CACHE_READ = new Setting<>("code.provider.cache.read", BOOLEAN, true, "false", "Defines whether the code model provider is allowed to read the cache instead of starting the extractor.");
    public static final @NonNull Setting<@NonNull Boolean> CODE_PROVIDER_CACHE_COMPRESS = new Setting<>("code.provider.cache.compress", BOOLEAN, true, "false", "Whether the individual cache files for the code model should written as compressed Zip archives. Reading of compressed cache files is always supported.");
    public static final @NonNull Setting<@NonNull List<String>> CODE_EXTRACTOR_FILES = new Setting<>("code.extractor.files", STRING_LIST, true, "", "Defines which files the code extractor should run on. Comma separated list of paths relative to the source tree. If directories are listed, then they are searched recursively for files that match the regular expression specified in code.extractor.file_regex. Set to an empty string to specify the complete source tree.");
    public static final @NonNull Setting<@NonNull Pattern> CODE_EXTRACTOR_FILE_REGEX = new Setting<>("code.extractor.file_regex", REGEX, true, ".*\\.c", "A Java regular expression defining which files are considered to be source files for parsing. See code.extractor.files for a description on which files this expression is tested on."); 
    public static final @NonNull Setting<@NonNull Integer> CODE_EXTRACTOR_THREADS = new Setting<>("code.extractor.threads", INTEGER, true, "1", "The number of threads the code extractor should use. This many files are parsed in parallel.");
    
    public static final @NonNull Setting<@NonNull Boolean> FUZZY_PARSING = new Setting<>("code.extractor.fuzzy_parsing", BOOLEAN, true, "false", "Defines whether non-boolean conditions that are encountered in the code should be (fuzzily) convereted into boolean conditions, instead of throwing an exception. For example, this replaces (A == 1) && B with A_eq_1 && B.");
    
    /*
     * Build model parameters
     */
    
    public static final @NonNull Setting<@NonNull String> BUILD_EXTRACTOR_CLASS = new Setting<>("build.extractor.class", STRING, true, EmptyBuildModelExtractor.class.getName(), "The fully qualified class name of the extractor for the build model.");
    public static final @NonNull Setting<@NonNull Integer> BUILD_PROVIDER_TIMEOUT = new Setting<>("build.provider.timeout", INTEGER, true, "0", "The maximum time the provider waits for the results of the extractor until an exception is thrown. In milliseconds; 0 = no timeout used.");
    public static final @NonNull Setting<@NonNull Boolean> BUILD_PROVIDER_CACHE_WRITE = new Setting<>("build.provider.cache.write", BOOLEAN, true, "false", "Defines whether the build model provider will write its results to the cache directory.");
    public static final @NonNull Setting<@NonNull Boolean> BUILD_PROVIDER_CACHE_READ = new Setting<>("build.provider.cache.read", BOOLEAN, true, "false", "Defines whether the code model build is allowed to read the cache instead of starting the extractor.");
    
    /*
     * Variability model parameters
     */
    
    public static final @NonNull Setting<@NonNull String> VARIABILITY_EXTRACTOR_CLASS = new Setting<>("variability.extractor.class", STRING, true, EmptyVariabilityModelExtractor.class.getName(), "The fully qualified class name of the extractor for the variability model.");
    public static final @NonNull Setting<@NonNull Integer> VARIABILITY_PROVIDER_TIMEOUT = new Setting<>("variability.provider.timeout", INTEGER, true, "0", "The maximum time the provider waits for the results of the extractor until an exception is thrown. In milliseconds; 0 = no timeout used.");
    public static final @NonNull Setting<@NonNull Boolean> VARIABILITY_PROVIDER_CACHE_WRITE = new Setting<>("variability.provider.cache.write", BOOLEAN, true, "false", "Defines whether the variability model provider will write its results to the cache directory.");
    public static final @NonNull Setting<@NonNull Boolean> VARIABILITY_PROVIDER_CACHE_READ = new Setting<>("variability.provider.cache.read", BOOLEAN, true, "false", "Defines whether the variability model provider is allowed to read the cache instead of starting the extractor.");
    public static final @NonNull Setting<@Nullable File> VARIABILITY_INPUT_FILE = new Setting<>("variability.input.file", FILE, false, null, "Path of a single file to be parsed by a variability model extractor.");
    
    /*
     * Other
     */
    
    public static final @NonNull Setting<@NonNull List<String>> PREPARATION_CLASSES = new Setting<>("preparation.class", SETTING_LIST, false, null, "A list of fully qualified class names that defines which preparations to run. A preparation class has to implement IPreperation. The preparations defined here are executed in the defined order.");
    
    // CHECKSTYLE:ON
    /**
     * Holds all declared setting constants.
     */
    private static final @NonNull Set<@NonNull Setting<?>> SETTINGS = new HashSet<>();
    
    static {
        for (Field field : DefaultSettings.class.getFields()) {
            if (Setting.class.isAssignableFrom(field.getType())
                    && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    SETTINGS.add(notNull((Setting<?>) field.get(null)));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Don't allow instance of this class.
     */
    private DefaultSettings() {
    }
    
    /**
     * Registers all settings declared in this class to the given configuration object.
     * 
     * @param config The configuration to register the settings to.
     * 
     * @throws SetUpException If any setting restrictions are violated.
     */
    public static void registerAllSettings(@NonNull Configuration config) throws SetUpException {
        // make sure that SOURCE_TREE is registered first, so that FILE types can be relative to this value
        config.registerSetting(SOURCE_TREE);
        
        for (Setting<?> setting : SETTINGS) {
            config.registerSetting(setting);
        }
    }
    
}
