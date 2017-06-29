package net.ssehub.kernel_haven;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.ssehub.kernel_haven.analysis.IAnalysis;
import net.ssehub.kernel_haven.build_model.BuildModelProvider;
import net.ssehub.kernel_haven.build_model.IBuildExtractorFactory;
import net.ssehub.kernel_haven.code_model.CodeModelProvider;
import net.ssehub.kernel_haven.code_model.ICodeExtractorFactory;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Zipper;
import net.ssehub.kernel_haven.variability_model.IVariabilityExtractorFactory;
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

    private static final PipelineConfigurator INSTANCE = new PipelineConfigurator();

    private Configuration config;
    
    private IVariabilityExtractorFactory vmExtractorFactory;

    private VariabilityModelProvider vmProvider;

    private IBuildExtractorFactory bmExtractorFactory;

    private BuildModelProvider bmProvider;

    private ICodeExtractorFactory cmExtractorFactory;

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
    public void init(Configuration config) throws SetUpException {
        this.config = config;
    }

    /**
     * Returns the variability model extractor that was configured for this
     * pipeline. May be <code>null</code> if not specified in config. Package
     * visibility for test cases.
     * 
     * @return The vm extractor factory.
     */
    IVariabilityExtractorFactory getVmExtractorFactory() {
        return vmExtractorFactory;
    }

    /**
     * Returns the provider for the variability model. This is <code>null</code>
     * until createProviders() is called.
     * 
     * @return The variability model provider.
     */
    public VariabilityModelProvider getVmProvider() {
        return vmProvider;
    }

    /**
     * Returns the build model extractor that was configured for this pipeline.
     * May be <code>null</code> if not specified in config. Package visibility
     * for test cases.
     * 
     * @return The bm extractor factory.
     */
    IBuildExtractorFactory getBmExtractorFactory() {
        return bmExtractorFactory;
    }

    /**
     * Returns the provider for the build model. This is <code>null</code> until
     * createProviders() is called.
     * 
     * @return The build model provider.
     */
    public BuildModelProvider getBmProvider() {
        return bmProvider;
    }

    /**
     * Returns the code model extractor that was configured for this pipeline.
     * May be <code>null</code> if not specified in config. Package visibility
     * for test cases.
     * 
     * @return The cm extractor factory.
     */
    ICodeExtractorFactory getCmExtractorFactory() {
        return cmExtractorFactory;
    }

    /**
     * Returns the provider for the code model. This is <code>null</code> until
     * createProviders() is called.
     * 
     * @return The code model provider.
     */
    public CodeModelProvider getCmProvider() {
        return cmProvider;
    }

    /**
     * Returns the analysis that was configured for this pipeline. This is
     * <code>null</code> until. instantiateAnalysis() is called.
     * 
     * @return The analysis.
     */
    public IAnalysis getAnalysis() {
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
    private boolean addJarToClasspath(File jarFile) {
        boolean status;
        try {
            URL url = jarFile.toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

            method.setAccessible(true);
            method.invoke(classLoader, url);
            LOGGER.logDebug("Successfully added jar to classpath: " + jarFile.getName());
            status = true;
            
        } catch (NoSuchMethodException | SecurityException | MalformedURLException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException exc) {
            LOGGER.logException("Could not add jar to classpath: " + jarFile.getName(), exc);
            
            status = false;
        }
        
        return status;
    }

    /**
     * Adds all jar files in the given directory to the class path.
     * 
     * @param dir
     *            the directory containing the jar files. Must not be null.
     * @return The number of successfully loaded jars.
     */
    private int loadJarsFromDirectory(File dir) {
        int numLoaded = 0;
        File[] jars = dir.listFiles((directory, fileName) -> fileName.toLowerCase().endsWith(".jar"));
        for (File jar : jars) {
            if (addJarToClasspath(jar)) {
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
    @SuppressWarnings("unchecked")
    public void instantiateExtractors() throws SetUpException {
        LOGGER.logInfo("Instantiating extractor factories...");
        
        /*
         * VM
         */
        String vmExtractorName = config.getVariabilityExtractorClassName();
        if (vmExtractorName != null) {
            if (vmExtractorName.contains(" ")) {
                LOGGER.logWarning("Variability extractor factory class name contains a space character");
            }
            try {
                Class<? extends IVariabilityExtractorFactory> vmExtractorClass =
                        (Class<? extends IVariabilityExtractorFactory>) Class.forName(vmExtractorName);
                vmExtractorFactory = vmExtractorClass.getConstructor().newInstance();

                LOGGER.logInfo("Successfully instantiated variability extractor factory " + vmExtractorName);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                    | SecurityException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
                LOGGER.logException("Error while instantiating variability extractor factory", e);
                throw new SetUpException(e);
            }
        } else {
            LOGGER.logInfo("No variability extractor factory specified");
        }

        /*
         * Build model
         */
        String bmExtractorName = config.getBuildExtractorClassName();
        if (bmExtractorName != null) {
            if (bmExtractorName.contains(" ")) {
                LOGGER.logWarning("Build extractor factory class name contains a space character");
            }
            try {
                Class<? extends IBuildExtractorFactory> bmExtractorClass = (Class<? extends IBuildExtractorFactory>)
                        Class.forName(bmExtractorName);
                bmExtractorFactory = bmExtractorClass.getConstructor().newInstance();
                LOGGER.logInfo("Successfully instantiated build extractor factory " + bmExtractorName);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                    | SecurityException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
                LOGGER.logException("Error while instantiating build extractor factory", e);
                throw new SetUpException(e);
            }
        } else {
            LOGGER.logInfo("No build extractor factory specified");
        }

        /*
         * Code model
         */
        String cmExtractorName = config.getCodeExtractorClassName();
        if (cmExtractorName != null) {
            if (cmExtractorName.contains(" ")) {
                LOGGER.logWarning("Code extractor factory class name contains a space character");
            }
            try {
                Class<? extends ICodeExtractorFactory> cmExtractorClass = (Class<? extends ICodeExtractorFactory>)
                        Class.forName(cmExtractorName);
                cmExtractorFactory = cmExtractorClass.getConstructor().newInstance();
                LOGGER.logInfo("Successfully instantiated code extractor factory " + cmExtractorName);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                    | SecurityException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
                LOGGER.logException("Error while instantiating code extractor factory", e);
                throw new SetUpException(e);
            }
        } else {
            LOGGER.logInfo("No code extractor factory specified");
        }
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
        
        vmProvider = new VariabilityModelProvider();
        vmProvider.setFactory(vmExtractorFactory);
        LOGGER.logInfo("Created variability provider");

        bmProvider = new BuildModelProvider();
        bmProvider.setFactory(bmExtractorFactory);
        LOGGER.logInfo("Created build provider");

        cmProvider = new CodeModelProvider();
        cmProvider.setFactory(cmExtractorFactory);
        LOGGER.logInfo("Created code provider");
    }

    /**
     * Instantiates the providers that the pipeline-configurator should use.
     * 
     * @throws SetUpException
     *             If the setup fails.
     */
    @SuppressWarnings("unchecked")
    public void instantiateAnalysis() throws SetUpException {
        LOGGER.logInfo("Instantiating analysis...");
        
        String analysisName = config.getAnalysisClassName();
        if (analysisName.contains(" ")) {
            LOGGER.logWarning("Analysis class name contains a space character");
        }
        
        try {
            Class<? extends IAnalysis> analysisClass = (Class<? extends IAnalysis>) Class.forName(analysisName);
            analysis = analysisClass.getConstructor(Configuration.class).newInstance(config);

            analysis.setVariabilityModelProvider(vmProvider);
            analysis.setBuildModelProvider(bmProvider);
            analysis.setCodeModelProvider(cmProvider);

            analysis.setOutputDir(config.getOutputDir());
            LOGGER.logInfo("Successfully instantiated analysis " + analysisName);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                | SecurityException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
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
            instantiateAnalysis();
            runAnalysis();
            archive();
        } catch (SetUpException e) {
            LOGGER.logException("Error while setting up pipeline", e);
        }
        
        LOGGER.logInfo("Pipeline done; exiting...");
    }

    /**
     * Archives the executed analysis including all plugin jars, KernelHaven.jar
     * itself, results, log, properties and a sweet cocktail cherry.
     */
    private void archive() {
        // this setting is overriden by the command line option in Run.main()
        if (config.isArchive()) {
            LOGGER.logInfo("Archiving the pipeline...");
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            Zipper zipper = null;
            File archiveTargetDir = config.getArchiveDir();

            try {
                zipper = new Zipper(new File(archiveTargetDir, "archived_execution_" + dtf.format(now)));
            } catch (IOException exc) {
                LOGGER.logException("Could not create file for archiving the execution artifacts ", exc);
                return;
            }
            try {
                // this is set in Run.main()
                zipper.copyFileToZip(config.getPropertyFile(), "/" + config.getPropertyFile().getName());
            } catch (IOException e) {
                LOGGER.logWarning("Could not archive configuration: " + e.getMessage());
            }
            
            try {
                zipper.copyFileToZip(config.getPluginsDir(), "/plugins");
            } catch (IOException e) {
                LOGGER.logWarning("Could not archive plugin jars: " + e.getMessage());
            }
            
            try {
                for (File outputFile : analysis.getOutputFiles()) {
                    zipper.copyFileToZip(outputFile, "/output");
                }
            } catch (IOException e) {
                LOGGER.logWarning("Could not archive output: " + e.getMessage());
            }

            try {
                File kernelHavenJar = new File(PipelineConfigurator.class.getProtectionDomain()
                        .getCodeSource().getLocation().getFile());
                zipper.copyFileToZip(kernelHavenJar, "/KernelHaven.jar");
            } catch (IOException e) {
                LOGGER.logError("Could not Archive KernelHaven.jar: " + e.getMessage());
            }

            if (LOGGER.getLogFile() != null) {
                try {
                    zipper.copyFileToZip(LOGGER.getLogFile(), "/log/log.txt");
                } catch (IOException e) {
                    LOGGER.logWarning("Could not archive log output: " + e.getMessage());
                }
            }
            
            LOGGER.logInfo("Archiving finished");
        } else {
            LOGGER.logInfo("Not archiving pipeline (not enabled by user)");
        }

    }

    /**
     * Loads all jar-files from the directory-path defined in the property
     * plugins_dir and adds them to the classb path.
     */
    public void loadPlugins() {
        File pluginsDir = config.getPluginsDir();
        LOGGER.logInfo("Loading jars from directory " + pluginsDir.getAbsolutePath());
        int num = loadJarsFromDirectory(pluginsDir);
        LOGGER.logInfo("Sucessfully loaded " + num + " jars");
    }

    /**
     * Getter for this singleton class.
     * 
     * @return The singleton instance.
     */
    public static PipelineConfigurator instance() {
        return INSTANCE;
    }

}
