package net.ssehub.kernel_haven;

import java.io.File;

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
        LOGGER.logDebug("Logger initialized. Now logging every level to console only.");
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

        PipelineConfigurator.instance().execute();
    }
}
