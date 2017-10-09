package net.ssehub.kernel_haven.config;

import static net.ssehub.kernel_haven.config.Setting.Type.BOOLEAN;
import static net.ssehub.kernel_haven.config.Setting.Type.DIRECTORY;
import static net.ssehub.kernel_haven.config.Setting.Type.INTEGER;
import static net.ssehub.kernel_haven.config.Setting.Type.REGEX;
import static net.ssehub.kernel_haven.config.Setting.Type.STRING;
import static net.ssehub.kernel_haven.config.Setting.Type.STRING_LIST;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.EmptyBuildModelExtractor;
import net.ssehub.kernel_haven.code_model.EmptyCodeModelExtractor;
import net.ssehub.kernel_haven.config.Setting.Type;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;
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
    
    public static final Setting<File> RESOURCE_DIR = new Setting<>("resource_dir", DIRECTORY, true, null, "TODO");
    public static final Setting<File> OUTPUT_DIR = new Setting<>("output_dir", DIRECTORY, true, null, "TODO");
    public static final Setting<File> PLUGINS_DIR = new Setting<>("plugins_dir", DIRECTORY, true, null, "TODO");
    public static final Setting<File> CACHE_DIR = new Setting<>("cache_dir", DIRECTORY, true, null, "TODO");
    public static final Setting<File> LOG_DIR = new Setting<>("log.dir", DIRECTORY, true, ".", "TODO");
    public static final Setting<File> ARCHIVE_DIR = new Setting<>("archive.dir", DIRECTORY, true, ".", "TODO");
    
    /*
     * Logging
     */
    
    public static final Setting<Boolean> LOG_CONSOLE = new Setting<>("log.console", BOOLEAN, true, "true", "TODO");
    public static final Setting<Boolean> LOG_FILE = new Setting<>("log.file", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> LOG_ERROR = new Setting<>("log.error", BOOLEAN, true, "true", "TODO");
    public static final Setting<Boolean> LOG_WARNING = new Setting<>("log.warning", BOOLEAN, true, "true", "TODO");
    public static final Setting<Boolean> LOG_INFO = new Setting<>("log.info", BOOLEAN, true, "true", "TODO");
    public static final Setting<Boolean> LOG_DEBUG = new Setting<>("log.debug", BOOLEAN, true, "false", "TODO");
    
    /*
     * Archiving
     */

    public static final Setting<Boolean> ARCHIVE = new Setting<>("archive", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> ARCHIVE_SOURCE_TREE = new Setting<>("archive.source_tree", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> ARCHIVE_CACHE_DIR = new Setting<>("archive.cache_dir", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> ARCHIVE_RES_DIR = new Setting<>("archive.res_dir", BOOLEAN, true, "false", "TODO");
    
    /*
     * Analysis
     */
    
    public static final Setting<String> ANALYSIS_CLASS = new Setting<>("analysis.class", STRING, true, null, "TODO");
    public static final Setting<List<String>> ANALYSIS_COMPONENTS_LOG = new Setting<>("analysis.output.intermediate_results", STRING_LIST, true, "", "Specifies analysis classes (simple class name), which results should be saved.");
    public static final Setting<String> ANALYSIS_PIPELINE = new Setting<>("analysis.pipeline", STRING, true, "", "TODO");
    public static final Setting<String> ANALYSIS_RESULT = new Setting<>("analysis.output_writer.class", STRING, false, null, "Specifies which kind of output writer (and thus which writer) shall be used. Must be the full qualified name of the writer class. A CSV writer will be used by default if nothing is specified. IO-Utils offers: net.ssehub.kernel_haven.io.excel.ExcelBook");
    
    /*
     * Common extractor parameters
     */
    
    public static final Setting<File> SOURCE_TREE = new Setting<>("source_tree", DIRECTORY, true, null, "TODO");
    public static final Setting<String> ARCH = new Setting<>("arch", STRING, false, null, "TODO");
    
    /*
     * Code model parameters
     */
    
    public static final Setting<Integer> CODE_PROVIDER_TIMEOUT = new Setting<>("code.provider.timeout", INTEGER, true, "0", "TODO");
    public static final Setting<Boolean> CODE_PROVIDER_CACHE_WRITE = new Setting<>("code.provider.cache.write", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> CODE_PROVIDER_CACHE_READ = new Setting<>("code.provider.cache.read", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> CODE_PROVIDER_CACHE_COMPRESS = new Setting<>("code.provider.cache.compress", BOOLEAN, true, "false", "TODO");
    public static final Setting<String> CODE_EXTRACTOR_CLASS = new Setting<>("code.extractor.class", STRING, true, EmptyCodeModelExtractor.class.getName(), "TODO");

    // TODO: make to list?
    public static final Setting<List<String>> CODE_EXTRACTOR_FILES = new Setting<>("code.extractor.files", Type.STRING_LIST, true, "", "TODO");
    public static final Setting<Pattern> CODE_EXTRACTOR_FILE_REGEX = new Setting<>("code.extractor.file_regex", REGEX, true, ".*\\.c", "TODO"); 
    public static final Setting<Integer> CODE_EXTRACTOR_THREADS = new Setting<>("code.extractor.threads", INTEGER, true, "1", "TODO");
    
    public static final Setting<Boolean> FUZZY_PARSING = new Setting<>("code.extractor.fuzzy_parsing", Type.BOOLEAN, true, "false", "TODO");
    
    /*
     * Build model parameters
     */
    
    public static final Setting<String> BUILD_EXTRACTOR_CLASS = new Setting<>("build.extractor.class", STRING, true, EmptyBuildModelExtractor.class.getName(), "TODO");
    public static final Setting<Integer> BUILD_PROVIDER_TIMEOUT = new Setting<>("build.provider.timeout", INTEGER, true, "0", "TODO");
    public static final Setting<Boolean> BUILD_PROVIDER_CACHE_WRITE = new Setting<>("build.provider.cache.write", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> BUILD_PROVIDER_CACHE_READ = new Setting<>("build.provider.cache.read", BOOLEAN, true, "false", "TODO");
    
    /*
     * Variability model parameters
     */
    
    public static final Setting<String> VARIABILITY_EXTRACTOR_CLASS = new Setting<>("variability.extractor.class", STRING, true, EmptyVariabilityModelExtractor.class.getName(), "TODO");
    public static final Setting<Integer> VARIABILITY_PROVIDER_TIMEOUT = new Setting<>("variability.provider.timeout", INTEGER, true, "0", "TODO");
    public static final Setting<Boolean> VARIABILITY_PROVIDER_CACHE_WRITE = new Setting<>("variability.provider.cache.write", BOOLEAN, true, "false", "TODO");
    public static final Setting<Boolean> VARIABILITY_PROVIDER_CACHE_READ = new Setting<>("variability.provider.cache.read", BOOLEAN, true, "false", "TODO");
    
    /*
     * Other
     */
    
    public static final Setting<Boolean> PREPARE_NON_BOOLEAN = new Setting<>("prepare_non_boolean", BOOLEAN, true, "false", "TODO");
    
    // CHECKSTYLE:ON
    /**
     * Holds all declared setting constants.
     */
    private static final Set<Setting<?>> SETTINGS = new HashSet<>();
    
    static {
        for (Field field : DefaultSettings.class.getFields()) {
            if (Setting.class.isAssignableFrom(field.getType())
                    && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    SETTINGS.add((Setting<?>) field.get(null));
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
    public static void registerAllSettings(Configuration config) throws SetUpException {
        for (Setting<?> setting : SETTINGS) {
            config.registerSetting(setting);
        }
    }
    
}
