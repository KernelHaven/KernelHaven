package net.ssehub.kernel_haven;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;

/**
 * 
 * Class for loading a configuration and starting the PipelineConfigurator.
 * 
 * @author Moritz
 * @author Marvin
 * @author Kevin
 *
 */
public class Run {

    private static final Logger LOGGER = Logger.get();

    /**
     * Prints some information about the system to the log.
     */
    private static void printSystemInfo() {
        Properties properties = System.getProperties();
        
        List<String> lines = new LinkedList<>();
        lines.add("System Info:");
        
        String[][] relevantKeys = {
            // key, visible name
            {"os.name", "OS Name"},
            {"os.version", "OS Version"},
            {"os.arch", "OS Arch"},
            {"java.home", "Java Home"},
            {"java.vendor", "Java Vendor"},
            {"java.version", "Java Version"},
            {"java.vm.name", "Java VM Name"},
            {"java.vm.version", "Java VM Version"},
            {"java.vm.vendor", "Java VM Vendor"},
            {"user.dir", "Working Directory"},
            {"java.io.tmpdir", "Temporary Directory"},
            {"file.encoding", "File Encoding"},
            {"java.class.path", "Java Class Path"},
        };
        
        for (String[] key : relevantKeys) {
            if (properties.containsKey(key[0])) {
                lines.add("\t" + key[1] + " = " + properties.get(key[0]));
            }
        }
        
        Runtime runtime = Runtime.getRuntime();
        lines.add("\tMaximum Memory: " + Util.formatBytes(runtime.maxMemory()));
        lines.add("\tAvailable Processors: " + runtime.availableProcessors());
        
        LOGGER.logInfo(notNull(lines.toArray(new String[0])));
    }

    /**
     * Main method to execute the Pipeline defined in the the properties file.
     *
     * 
     * @param args
     *            the command line arguments. Used for parsing property-file
     *            location and flags.
     * @return Whether run was successful or not.
     */
    // CHECKSTYLE:OFF // ignore "too many returns" error
    public static boolean run(String... args) {
    // CHECKSTYLE:ON
        Thread.currentThread().setName("Setup");
        
        Thread.setDefaultUncaughtExceptionHandler((Thread thread, Throwable exc) -> {
            LOGGER.logException("Unhandled exception in thread " + thread.getName(), exc);
        });

        File propertiesFile = null;

        boolean archiveParam = false;

        for (String arg : args) {
            if (arg.equals("--archive")) {
                archiveParam = true;
            } else if (!arg.startsWith("--")) {
                if (propertiesFile == null) {
                    propertiesFile = new File(arg);
                } else {
                    LOGGER.logError("You must not define more than one properties file");
                    return false;
                }

            } else {
                LOGGER.logError("Unknown command line option " + arg);
                return false;
            }
        }

        if (propertiesFile == null) {
            LOGGER.logError("No properties-file provided. Stopping system");
            return false;
        }

        Configuration config = null;

        try {
            config = new Configuration(propertiesFile);
            DefaultSettings.registerAllSettings(config);

            if (archiveParam) {
                config.setValue(DefaultSettings.ARCHIVE, true);
            }

            PipelineConfigurator.instance().init(config);

        } catch (SetUpException e) {
            LOGGER.logError("Invalid configuration detected:", e.getMessage());
            return false;
        }
        try {
            LOGGER.setup(config);

        } catch (SetUpException exc) {
            LOGGER.logException(
                    "Was not able to setup the Logger as defined in the properties. Logging now to Console only", exc);
        }
        
        LOGGER.logInfo("Start executing KernelHaven with configuration file " + propertiesFile.getPath());
        printSystemInfo();

        PipelineConfigurator.instance().execute();
        return true;
    }
    
    /**
     * Main method to execute the Pipeline defined in the the properties file.
     *
     * 
     * @param args
     *            the command line arguments. Used for parsing property-file
     *            location and flags.
     */
    public static void main(String... args) {
        boolean success = run(args);
        if (!success) {
            System.exit(1);
        }
    }
    
}
