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
        
//        // general
//        this.resourceDir = readFileProperty("resource_dir", new FileProps());
//        this.outputDir = readFileProperty("output_dir", new FileProps());
//        this.pluginsDir = readFileProperty("plugins_dir", new FileProps());
////        this.cacheDir = readFileProperty("cache_dir", new FileProps());
//        
//        this.logConsole = Boolean.parseBoolean(getProperty("log.console", "true"));
//        this.logFile = Boolean.parseBoolean(getProperty("log.file", "false"));
//        this.logError = Boolean.parseBoolean(getProperty("log.error", "true"));
//        this.logWarning = Boolean.parseBoolean(getProperty("log.warning", "true"));
//        this.logInfo = Boolean.parseBoolean(getProperty("log.info", "true"));
//        this.logDebug = Boolean.parseBoolean(getProperty("log.debug", "false"));
//        this.logDir = readFileProperty("log.dir", new FileProps());
//        
//        this.archive = Boolean.parseBoolean(getProperty("archive", "false"));
//        this.archiveDir = readFileProperty("archive.dir", new FileProps());
//        this.archiveCacheDir = Boolean.parseBoolean(getProperty("archive.cache_dir", "false"));
//        this.archiveResDir = Boolean.parseBoolean(getProperty("archive.res_dir", "false"));
//        this.archiveSourceTree = Boolean.parseBoolean(getProperty("archive.source_tree", "false"));
//        
//        // analysis
//        this.analysisClassName = getProperty("analysis.class");
//        List<String> list = readList("analysis.components.log");
//        this.loggingAnalyissComponents = new HashSet<>(list == null ? new LinkedList<>() : list);
//        
//        // common extractor stuff
//        this.sourceTree = readFileProperty("source_tree", new FileProps());
//        this.arch = getProperty("arch");
//        
//        // code
//        // TODO
////        this.codeProviderTimeout = readLong("code.provider.timeout", 0);
//        this.codeExtractorClassName = getProperty("code.extractor.class");
////        this.codeCacheWrite = Boolean.parseBoolean(getProperty("code.provider.cache.write", "false"));
////        this.codeCacheRead = Boolean.parseBoolean(getProperty("code.provider.cache.read", "false"));
////        String fileListSetting = getProperty("code.extractor.files", "");
////        String[] fileListSettingParts = fileListSetting.split(",");
////        this.codeExtractorFiles = new File[fileListSettingParts.length];
////        for (int i = 0; i < this.codeExtractorFiles.length; i++) {
////            this.codeExtractorFiles[i] = new File(fileListSettingParts[i].trim());
////        }
////        this.codeExtractorFilenamePattern = Pattern.compile(getProperty("code.extractor.file_regex", ".*\\.c"));
////        this.codeExtractorThreads = (int) readLong("code.extractor.threads", 0);
//        
//        // build
////        this.buildProviderTimeout = readLong("build.provider.timeout", 0);
////        this.buildCacheWrite = Boolean.parseBoolean(getProperty("build.provider.cache.write", "false"));
////        this.buildCacheRead = Boolean.parseBoolean(getProperty("build.provider.cache.read", "false"));
//        this.buildExtractorClassName = getProperty("build.extractor.class");
//        
//        // variabiltiy
//        // TODO
////        this.variabilityProviderTimeout = readLong("variability.provider.timeout", 0);
////        this.variabilityCacheWrite = Boolean.parseBoolean(getProperty("variability.provider.cache.write", "false"));
////        this.variabilityCacheRead = Boolean.parseBoolean(getProperty("variability.provider.cache.read", "false"));
//        this.variabilityExtractorClassName = getProperty("variability.extractor.class");
    }
    
    @Override
    public void setPropertyFile(File propertyFile) {
        super.setPropertyFile(propertyFile);
    }
    
}
