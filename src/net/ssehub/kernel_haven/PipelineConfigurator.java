package net.ssehub.kernel_haven;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.ssehub.kernel_haven.analysis.IAnalysis;
import net.ssehub.kernel_haven.build_model.AbstractBuildModelExtractor;
import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.code_model.AbstractCodeModelExtractor;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractExtractor;
import net.ssehub.kernel_haven.util.KernelHavenClassLoader;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.PipelineArchiver;
import net.ssehub.kernel_haven.util.StaticClassLoader;
import net.ssehub.kernel_haven.util.ZipArchive;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModelProvider;

/**
 * 
 * Class for configuring the pipeline.
 * 
 * @author Marvin
 * @author Moritz
 * @author Adam
 * @author Manu
 */
public class PipelineConfigurator {

    private static final Logger LOGGER = Logger.get();

    private static final @NonNull PipelineConfigurator INSTANCE = new PipelineConfigurator();

    private @Nullable Configuration config;
    
    private AbstractVariabilityModelExtractor vmExtractor;

    private VariabilityModelProvider vmProvider;

    private AbstractBuildModelExtractor bmExtractor;

    private BuildModelProvider bmProvider;

    private AbstractCodeModelExtractor cmExtractor;

    private CodeModelProvider cmProvider;

    private IAnalysis analysis;

    /**
     * Constructor; package because this class is a singleton. Only used by test
     * cases and for singleton.
     */
    PipelineConfigurator() {
    }

    /**
     * Initializes PipelineConfigurator with a set of properties.
     * 
     * @param config
     *            the configuration that the configurator should use.
     * 
     * @throws SetUpException If the config is invalid. 
     */
    public void init(@NonNull Configuration config) throws SetUpException {
        this.config = config;
    }

    /**
     * Returns the variability model extractor that was configured for this
     * pipeline. May be <code>null</code> if not specified in config. Package
     * visibility for test cases.
     * 
     * @return The vm extractor.
     */
    @Nullable AbstractVariabilityModelExtractor getVmExtractor() {
        return vmExtractor;
    }

    /**
     * Returns the provider for the variability model. This is <code>null</code>
     * until createProviders() is called.
     * 
     * @return The variability model provider.
     */
    public @Nullable VariabilityModelProvider getVmProvider() {
        return vmProvider;
    }

    /**
     * Returns the build model extractor that was configured for this pipeline.
     * May be <code>null</code> if not specified in config. Package visibility
     * for test cases.
     * 
     * @return The bm extractor.
     */
    @Nullable AbstractBuildModelExtractor getBmExtractor() {
        return bmExtractor;
    }

    /**
     * Returns the provider for the build model. This is <code>null</code> until
     * createProviders() is called.
     * 
     * @return The build model provider.
     */
    public @Nullable BuildModelProvider getBmProvider() {
        return bmProvider;
    }

    /**
     * Returns the code model extractor that was configured for this pipeline.
     * May be <code>null</code> if not specified in config. Package visibility
     * for test cases.
     * 
     * @return The cm extractor.
     */
    @Nullable AbstractCodeModelExtractor getCmExtractor() {
        return cmExtractor;
    }

    /**
     * Returns the provider for the code model. This is <code>null</code> until
     * createProviders() is called.
     * 
     * @return The code model provider.
     */
    public @Nullable CodeModelProvider getCmProvider() {
        return cmProvider;
    }

    /**
     * Returns the analysis that was configured for this pipeline. This is
     * <code>null</code> until instantiateAnalysis() is called.
     * 
     * @return The analysis.
     */
    public @Nullable IAnalysis getAnalysis() {
        return analysis;
    }

    /**
     * Adds jar to the class path. After it is added, all of its classes can be
     * accessed via reflection-api.
     * 
     * @param jarFile
     *            the jar file that should be added to the class path. Must not
     *            be null.
     *            
     * @return Whether the loading was successful or not.
     */
    private boolean addJarToClasspath(@NonNull File jarFile) {
        boolean success = false;
        
        try {
            URL url = jarFile.toURI().toURL();
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            
        
            if (systemClassLoader instanceof KernelHavenClassLoader) {
                // For Java >= 9, the class loader is manually set to KernelHavenClassLoader
                @SuppressWarnings("resource")
                KernelHavenClassLoader classLoader = (KernelHavenClassLoader) systemClassLoader;
                classLoader.addURL(url);
            
                success = true;
                
            } else if (systemClassLoader instanceof URLClassLoader) {
                // For Java 8, the default is an URLClassLoader
                URLClassLoader classLoader = (URLClassLoader) systemClassLoader;
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                
                method.setAccessible(true);
                method.invoke(classLoader, url);
                
                success = true;
                
            } else {
                LOGGER.logError("Can't add " + jarFile.getName() + " to classpath because the class loader is wrong",
                        "Try running java with "
                        + "-Djava.system.class.loader=net.ssehub.kernel_haven.util.KernelHavenClassLoader");
            }
            
            
        } catch (ReflectiveOperationException | MalformedURLException | IllegalArgumentException exc) {
            LOGGER.logException("Could not add jar to classpath: " + jarFile.getName(), exc);
        }
        
        if (success) {
            LOGGER.logDebug("Successfully added jar to classpath: " + jarFile.getName());
            
            // try to get and log the version
            String version = null;
            
            try (ZipArchive archive = new ZipArchive(jarFile)) {
                File versionTxt = new File("version.txt");
                if (archive.containsFile(versionTxt)) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(
                            archive.getInputStream(versionTxt)))) {
                        
                        version = in.readLine();
                    }
                }
            } catch (IOException e) {
                // ignore
            }
            if (version != null) {
                LOGGER.logInfo(jarFile.getName() + " version: " + version);
            }
        }
        
        return success;
    }

    /**
     * Adds all jar files in the given directory to the class path.
     * 
     * @param dir
     *            the directory containing the jar files. Must not be null.
     * @return The number of successfully loaded jars.
     */
    private int loadJarsFromDirectory(@NonNull File dir) {
        int numLoaded = 0;
        File[] jars = dir.listFiles((directory, fileName) -> fileName.toLowerCase().endsWith(".jar"));
        for (File jar : jars) {
            if (addJarToClasspath(notNull(jar))) {
                numLoaded++;
            }
        }
        
        return numLoaded;
    }


    /**
     * Instantiates the extractors that the pipeline-configurator should use.
     * 
     * @throws SetUpException
     *             If the setup fails.
     */
    public void instantiateExtractors() throws SetUpException {
        LOGGER.logInfo("Instantiating extractor factories...");
        Configuration config = this.config;
        if (config == null) {
            throw new SetUpException("Configuration not set");
        }
        
        vmExtractor = instantiateExtractor(config.getValue(DefaultSettings.VARIABILITY_EXTRACTOR_CLASS),
                "variability");

        bmExtractor = instantiateExtractor(config.getValue(DefaultSettings.BUILD_EXTRACTOR_CLASS),
            "build");
        
        cmExtractor = instantiateExtractor(config.getValue(DefaultSettings.CODE_EXTRACTOR_CLASS),
            "code");
    }

    /**
     * Generic method to load/instantiate one extractor.
     * 
     * @param extractorClassName The fully qualified name of the class to load; not <tt>null</tt>.
     * @param type The type of extractor to load, this is only used in log messages.
     * @param <E> The type of extractor to load.
     * 
     * @return The instantiated extractor, won't be <tt>null</tt>.
     * 
     * @throws SetUpException If loading or instantiating the extractor class fails.
     */
    @SuppressWarnings("unchecked")
    private <E extends AbstractExtractor<?>> @NonNull E instantiateExtractor(@NonNull String extractorClassName,
            @NonNull String type) throws SetUpException {
        
        E extractor;
        Class<E> extractorClass;
        
        if (extractorClassName.contains(" ")) {
            LOGGER.logWarning("Name of " + type + " extractor contains a space character");
        }
        try {
            extractorClass = (Class<E>) ClassLoader.getSystemClassLoader().loadClass(extractorClassName);

        } catch (ClassNotFoundException  e) {
            LOGGER.logException("Error while loading " + type + " extractor class", e);
            throw new SetUpException(e);
        }
        
        try {
            extractor = notNull(extractorClass.getConstructor().newInstance());
            
            LOGGER.logInfo("Successfully instantiated " + type + " extractor " + extractor.getClass().getName());
        } catch (ReflectiveOperationException | IllegalArgumentException | ClassCastException e) {
            LOGGER.logException("Error while instantiating " + type + " extractor", e);
            throw new SetUpException(e);
        }
        return extractor;
    }

    /**
     * 
     * Create the providers that the pipeline-configurator should use.
     * 
     * @throws SetUpException
     *             If the setup fails.
     */
    public void createProviders() throws SetUpException {
        LOGGER.logInfo("Creating providers...");
        
        Configuration config = this.config;
        if (config == null) {
            throw new SetUpException("Configuration is not set");
        }
        
        AbstractVariabilityModelExtractor vmExtractor = this.vmExtractor;
        AbstractBuildModelExtractor bmExtractor = this.bmExtractor;
        AbstractCodeModelExtractor cmExtractor = this.cmExtractor;
        if (vmExtractor == null || bmExtractor == null || cmExtractor == null) {
            throw new SetUpException("Extractors not created");
        }
        
        vmProvider = new VariabilityModelProvider();
        vmProvider.setExtractor(vmExtractor);
        vmProvider.setConfig(config);
        LOGGER.logInfo("Created variability provider");

        bmProvider = new BuildModelProvider();
        bmProvider.setExtractor(bmExtractor);
        bmProvider.setConfig(config);
        LOGGER.logInfo("Created build provider");

        cmProvider = new CodeModelProvider();
        cmProvider.setExtractor(cmExtractor);
        cmProvider.setConfig(config);
        LOGGER.logInfo("Created code provider");
    }
    
    /**
     * Instantiates and executes the configured preparation class.
     * 
     * @throws SetUpException If instantiating or executing the preparation class fails.
     */
    public void runPreparation() throws SetUpException {
        String previousName = Thread.currentThread().getName();
        
        Configuration config = this.config;
        if (config == null) {
            throw new SetUpException("Configuration not set");
        }
        for (String className : config.getValue(DefaultSettings.PREPARATION_CLASSES)) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends IPreparation> prepartionClass =
                        (Class<? extends IPreparation>) ClassLoader.getSystemClassLoader().loadClass(className);
                
                LOGGER.logInfo("Running preparation " + prepartionClass.getCanonicalName());
                
                IPreparation preparation = notNull(prepartionClass.newInstance());
                
                Thread.currentThread().setName("Preparation");
                preparation.run(config);
            
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new SetUpException(e);
                
            } finally {
                Thread.currentThread().setName(previousName);
            }
        }
    }

    /**
     * Instantiates the providers that the pipeline-configurator should use.
     * 
     * @throws SetUpException
     *             If the setup fails.
     */
    public void instantiateAnalysis() throws SetUpException {
        LOGGER.logInfo("Instantiating analysis...");
        Configuration config = this.config;
        if (config == null) {
            throw new SetUpException("Configuration not set");
        }
        
        String analysisName = config.getValue(DefaultSettings.ANALYSIS_CLASS);
        if (analysisName.contains(" ")) {
            LOGGER.logWarning("Analysis class name contains a space character");
        }
        
        try {
            @SuppressWarnings("unchecked")
            Class<? extends IAnalysis> analysisClass = (Class<? extends IAnalysis>)
                    ClassLoader.getSystemClassLoader().loadClass(analysisName);
            analysis = analysisClass.getConstructor(Configuration.class).newInstance(config);

            analysis.setVariabilityModelProvider(notNull(vmProvider));
            analysis.setBuildModelProvider(notNull(bmProvider));
            analysis.setCodeModelProvider(notNull(cmProvider));

            analysis.setOutputDir(config.getValue(DefaultSettings.OUTPUT_DIR));
            LOGGER.logInfo("Successfully instantiated analysis " + analysisName);

        } catch (ReflectiveOperationException | IllegalArgumentException | ClassCastException e) {
            LOGGER.logException("Error while instantiating analysis " + analysisName, e);
            throw new SetUpException(e);
        }
    }

    /**
     * Runs the analysis. Should only be called after extractors, providers and
     * analysis have been defined.
     * 
     * 
     */
    public void runAnalysis() {
        LOGGER.logInfo("Starting analysis...");
        Thread.currentThread().setName("Analysis");
        analysis.run();
        
        Thread.currentThread().setName("Setup");
        LOGGER.logInfo("Analysis has finished");
    }

    /**
     * Executes the process defined in this instance of PipelineConfigurator.
     */
    public void execute() {
        LOGGER.logInfo("Start setting up pipeline...");
        try {
            loadPlugins();
            instantiateExtractors();
            createProviders();
            runPreparation();
            instantiateAnalysis();
            runAnalysis();
            archive();
        } catch (SetUpException e) {
            LOGGER.logException("Error while setting up pipeline", e);
        }
        
        LOGGER.logStatus("Pipeline done; exiting...");
    }

    /**
     * Archives the executed analysis including all plugin jars, KernelHaven.jar
     * itself, results, log, properties and a sweet cocktail cherry.
     */
    private void archive() {
        Configuration config = this.config;
        if (config == null) {
            throw new RuntimeException("Configuration not set");
        }
        
        // this setting is overriden by the command line option in Run.main()
        if (config.getValue(DefaultSettings.ARCHIVE)) {
            PipelineArchiver archiver = new PipelineArchiver(config);
            archiver.setAnalysisOutputFiles(analysis.getOutputFiles());
            
            try {
                archiver.archive();
            } catch (IOException exc) {
                LOGGER.logException("Could not create file for archiving the execution artifacts ", exc);
            }
        } else {
            LOGGER.logInfo("Not archiving pipeline (not enabled by user)");
        }

    }

    /**
     * Loads all jar-files from the directory-path defined in the property
     * plugins_dir and adds them to the classb path.
     */
    public void loadPlugins() {
        Configuration config = this.config;
        if (config == null) {
            throw new RuntimeException("Configuration not set");
        }
        
        File pluginsDir = config.getValue(DefaultSettings.PLUGINS_DIR);
        LOGGER.logInfo("Loading jars from directory " + pluginsDir.getAbsolutePath());
        int num = loadJarsFromDirectory(pluginsDir);
        LOGGER.logInfo("Successfully loaded " + num + " jars");
        
        StaticClassLoader.loadClasses(config);
    }
    
    /**
     * Getter for this singleton class.
     * 
     * @return The singleton instance.
     */
    public static @NonNull PipelineConfigurator instance() {
        return INSTANCE;
    }

}
