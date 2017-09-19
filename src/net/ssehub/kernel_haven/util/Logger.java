package net.ssehub.kernel_haven.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;

/**
 * A thread-safe singleton logger.
 * 
 * @author adam
 * @author alice
 * @author moritz
 */
public class Logger {

    private static final int LOG_MESSAGE_SIZE_CONSOLE_LIMIT = 10;

    /**
     * The singleton instance. <code>null</code> until one of the init Methods
     * is called.
     */
    private static Logger instance;

    /** Activates error logging. */
    private boolean errorLogging = true;

    /** Activates warning logging. */
    private boolean warningLogging = true;

    /** Activates debug logging. */
    private boolean debugLogging = true;

    /** Activates info logging. */
    private boolean infoLogging = true;

    /** Activates file logging. */
    private boolean fileLogging = false;

    /** Activates console logging. */
    private boolean consoleLogging = true;

    /** The OutputStream for the logger to log to. */
    private List<Target> targets;

    /** The charset used by the logger. */
    private Charset charset;

    /** File used as target for logging. **/
    private File logFile;
    
    /**
     * A single target of the logger.
     */
    static final class Target {
        
        private OutputStream out;
        
        private int maxLogLines;
     
        /**
         * Creates a new target.
         * 
         * @param out The target stream.
         */
        public Target(OutputStream out) {
            this.out = out;
        }
        
        /**
         * The output stream for this target.
         * 
         * @return The output stream, never null.
         */
        public OutputStream getOut() {
            return out;
        }
        
        /**
         * How many lines a single log entry may contain.
         * 
         * @return The maximum number of lines for a single log entry. 0 means no limit.
         */
        public int getMaxLogLines() {
            return maxLogLines;
        }
        
    }

    /**
     * Instantiates a new logger.
     *
     * @param charset
     *            Charset to use for logging. Must not be null.
     */
    private Logger(Charset charset) {
        this.targets = new ArrayList<>(2);
        this.charset = charset;
    }

    /**
     * The setup method sets the log level and log output to console or file.
     * 
     * @param config The configuration for the logger; must not be <code>null</code>.
     * @throws SetUpException
     *             Throws the SetUpException when the path to log to is not a
     *             valid directory.
     */

    public void setup(Configuration config) throws SetUpException {
        this.fileLogging = config.isLogFile();
        this.consoleLogging = config.isLogConsole();
        this.errorLogging = config.isLogError();
        this.warningLogging = config.isLogWarning();
        this.debugLogging = config.isLogDebug();
        this.infoLogging = config.isLogInfo();

        if (fileLogging) {
            logFile = new File(config.getLogDir(), "KernelHaven_" + getTimestampForFile() + ".log");
        }

        if (!consoleLogging) {
            log("info", "Stop logging to console");
            targets.clear();
        }
        
        if (fileLogging) {
            // if console logging is still on, then we can shorten that one
            if (!targets.isEmpty()) {
                targets.get(0).maxLogLines = LOG_MESSAGE_SIZE_CONSOLE_LIMIT;
            }
            
            try {
                targets.add(new Target(new FileOutputStream(logFile)));
                log("info", "Logging to log file " + logFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                throw new SetUpException(e);
            }
        }
        
    }
    
    /**
     * Returns the list of targets for this logger.
     * Package visibility for test cases.
     * 
     * @return The targets of this logger.
     */
    List<Target> getTargets() {
        return targets;
    }

    /**
     * Gets the time stamp for the current time and date.
     *
     * @return the time stamp
     */
    private static String getTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Gets the time stamp for the current time and date repleacing : by - for
     * filenames.
     *
     * @return the time stamp
     */
    private static String getTimestampForFile() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Initializes the logger to log to stdout in UTF-8.
     */
    public static void init() {
        init(System.out);
    }

    /**
     * Initializes the logger to log in UTF-8. Target must not be null.
     * 
     * @param target
     *            The output target of the logger. The logger will only write to
     *            it if it obtains a lock on it. Must not be null.
     */
    public static void init(OutputStream target) {
        init(target, Charset.forName("UTF-8"));
    }

    /**
     * Initializes the logger.
     * 
     * @param target
     *            The output target of the logger. The logger will only write to
     *            it if it obtains a lock on it. Must not be null.
     * @param charset
     *            The charset the logger writes in. Must not be null.
     */
    public static void init(OutputStream target, Charset charset) {
        instance = new Logger(charset);
        instance.targets.add(new Target(target));
    }

    /**
     * Gets the singleton instance of Logger.
     *
     * @return the logger
     */
    public static Logger get() {
        return instance;
    }

    /**
     * Creates a "header" prefix for log lines. The lines contain the specified
     * log level, the name of the current thread and the time.
     * 
     * @param level
     *            The log level that will be used. Must not be null.
     * @return A string in the format "[level] [time] [threadName] "
     */
    private String constructHeader(String level) {
        StringBuffer hdr = new StringBuffer();
        String timestamp = Logger.getTimestamp();

        hdr.append('[').append(level).append("] [").append(timestamp).append("] [")
                .append(Thread.currentThread().getName()).append("] ");
        return hdr.toString();
    }

    /**
     * Writes a single log entry consisting of the specified lines with the
     * specified log level to the target. Internally, a lock on {@link #target}
     * is acquired to ensure that messages are not splitted in a multi-threaded
     * environment.
     * 
     * @param level
     *            The log level to be written. Must not be null.
     * @param lines
     *            The lines that are written together as one log entry. Must not
     *            be null.
     */
    private void log(String level, String... lines) {
        String header = constructHeader(level);
        String indent = "";
        if (lines.length > 1) {
            indent = header.replaceAll(".", " ");
        }

        StringBuffer str = new StringBuffer(header);

        for (int i = 0; i < lines.length; i++) {
            if (i != 0) {
                str.append(indent);
            }
            str.append(lines[i]).append('\n');
        }
        byte[] bytes = str.toString().getBytes(charset);

        for (Target target : targets) {

            if (target.maxLogLines > 0 && lines.length > target.maxLogLines) {
                StringBuffer shortened = new StringBuffer(header);
                for (int i = 0; i < lines.length && i < target.maxLogLines; i++) {
                    if (i != 0) {
                        shortened.append(indent);
                    }
                    shortened.append(lines[i]).append('\n');
                }
                shortened.append(indent).append("... (log shortened, see log file for full output)\n");
                
                synchronized (target.out) {
                    try {
                        target.out.write(shortened.toString().getBytes(charset));
                        target.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            } else {
                synchronized (target.out) {
                    try {
                        target.out.write(bytes);
                        target.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Logs a log entry with the log level "info".
     * 
     * @param lines
     *            The content of the log entry. Must not be null.
     */
    public void logInfo(String... lines) {
        if (infoLogging) {
            log("info", lines);
        }
    }

    /**
     * Logs a log entry with the log level "debug".
     * 
     * @param lines
     *            The content of the log entry. Must not be null.
     */
    public void logDebug(String... lines) {
        if (debugLogging) {
            log("debug", lines);
        }
    }

    /**
     * Logs a log entry with the log level "warning".
     * 
     * @param lines
     *            The content of the log entry.
     */
    public void logWarning(String... lines) {
        if (warningLogging) {
            log("warning", lines);
        }
    }

    /**
     * Logs a log entry with the log level "error".
     * 
     * @param lines
     *            The content of the log entry. Must not be null.
     */
    public void logError(String... lines) {
        if (errorLogging) {
            log("error", lines);
        }
    }

    /**
     * Converts a given exception to a string and adds the lines to the list.
     * The string will contain the stack trace, much like
     * {@link Throwable#printStackTrace()}. Additionally, the causing exceptions
     * are converted into strings, too.
     * 
     * @param exc
     *            The exception to convert into a string. Must not be null.
     * @param lines
     *            The output target. The lines are appended to this list. Must
     *            not be null.
     */
    private void exceptionToString(Throwable exc, List<String> lines) {
        lines.add(exc.toString());

        StackTraceElement[] stack = exc.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            lines.add("    at " + stack[i].toString());
        }

        Throwable cause = exc.getCause();
        if (cause != null) {
            lines.add("Caused by:");
            exceptionToString(cause, lines);
        }
    }
    
    /**
     * Creates a log entry from the given comment and exception.
     * 
     * @param level The log level to log at.
     * @param comment 
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged. Must not
     *            be null.
     */
    private void logException(String level, String comment, Throwable exc) {
        List<String> lines = new ArrayList<>(exc.getStackTrace().length + 2);
        lines.add(comment + ":");
        exceptionToString(exc, lines);
        log(level, lines.toArray(new String[0]));
    }

    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "error".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged. Must not
     *            be null.
     */
    public void logException(String comment, Throwable exc) {
        logException("error", comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "debug".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged. Must not
     *            be null.
     */
    public void logExceptionDebug(String comment, Throwable exc) {
        logException("debug", comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "warning".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged. Must not
     *            be null.
     */
    public void logExceptionWarning(String comment, Throwable exc) {
        logException("warning", comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "info".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged. Must not
     *            be null.
     */
    public void logExceptionInfo(String comment, Throwable exc) {
        logException("info", comment, exc);
    }
    
    /**
     * Gets the target logging file.
     * 
     * @return the file used as logging target. May be null if not logging to a file.
     */
    public File getLogFile() {
        return logFile;
    }

}