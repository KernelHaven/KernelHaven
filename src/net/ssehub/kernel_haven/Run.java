package net.ssehub.kernel_haven;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.Logger;

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

    private static final Logger LOGGER;

    static {
        Logger.init();
        LOGGER = Logger.get();
    }
    
    /**
     * Formats the given amount of bytes correctly as KiB, MiB GiB or TiB.
     * 
     * @param amount The amount of bytes.
     * 
     * @return A string representing the amount of bytes.
     */
    private static String formatBytes(long amount) {
        
        int i = 0;
        String[] suffix = {"B", "KiB", "MiB", "GiB", "TiB"};
        amount *= 100; // this way we get two digits of precision after the comma
        
        while (i < suffix.length && amount > 102400) {
            i++;
            amount /= 1024;
        }
        
        return (amount / 100.0) + " " + suffix[i];
    }
    
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
        lines.add("\tMaximum Memory: " + formatBytes(runtime.maxMemory()));
        lines.add("\tAvailable Processors: " + runtime.availableProcessors());
        
        LOGGER.logInfo(lines.toArray(new String[] {}));
    }

    /**
     * Main method to execute the Pipeline defined in the the properties file.
     *
     * 
     * @param args
     *            the command line arguments. Used for parsing property-file
     *            location and flags.
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Setup");

        LOGGER.logInfo("Starting up...");
        LOGGER.logDebug("Please stand by. We are saddling the unicorn.");
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
                    System.exit(1);
                }

            } else {
                LOGGER.logError("Unknown command line option " + arg);
                System.exit(1);
            }
        }

        if (propertiesFile == null) {
            LOGGER.logError("No properties-file provided. Stopping system");
            System.exit(1);
        }

        Configuration config = null;

        try {
            LOGGER.logInfo("Loading properties from " + propertiesFile.getAbsolutePath());
            config = new Configuration(propertiesFile);

            if (archiveParam) {
                config.setArchive(true);
            }

            PipelineConfigurator.instance().init(config);

        } catch (SetUpException e) {
            LOGGER.logError("Invalid configuration detected:", e.getMessage());
            System.exit(1);
        }
        try {
            LOGGER.logDebug("Changing logger Setup by properties.");
            LOGGER.setup(config);

        } catch (SetUpException exc) {
            LOGGER.logException(
                    "Was not able to setup the Logger as defined in the properties. Logging now to Console only", exc);
        }
        
        Thread.setDefaultUncaughtExceptionHandler((Thread thread, Throwable exc) -> {
            LOGGER.logException("Unhandled exception in thread " + thread.getName(), exc);
        });
        
        printSystemInfo();

        PipelineConfigurator.instance().execute();
    }
}
