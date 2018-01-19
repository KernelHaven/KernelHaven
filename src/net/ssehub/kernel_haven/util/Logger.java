package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNullArrayWithNotNullContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A thread-safe singleton logger.
 * 
 * @author adam
 * @author alice
 * @author moritz
 */
public class Logger {
    
    /**
     * Enum of the available log levels.
     */
    public static enum Level {
        ERROR("error", 0),
        WARNING("warning", 1),
        INFO("info", 2),
        DEBUG("debug", 3);
        
        private @NonNull String str;
        
        private int level;
        
        /**
         * Creates a log level.
         * 
         * @param str The string representation of this log level.
         * @param level The level. All log levels with values <= this value will be logged.
         */
        private Level(@NonNull String str, int level) {
            this.str = str;
            this.level = level;
        }
        
        /**
         * Whether a message with the specified level will be logged, if the logger is set to this level.
         * 
         * @param other The other level to check.
         * @return Whether the other level will be logged if this level is set.
         */
        public boolean isLog(@NonNull Level other) {
            return this.level >= other.level;
        }
        
        @Override
        public @NonNull String toString() {
            return str;
        }
        
    }

    private static final int LOG_MESSAGE_SIZE_CONSOLE_LIMIT = 10;

    /**
     * The singleton instance. <code>null</code> until one of the init Methods
     * is called.
     */
    private static Logger instance;

    private Level level;
    
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
        this.level = Level.INFO;
    }

    /**
     * The setup method sets the log level and log output to console or file.
     * 
     * @param config The configuration for the logger; must not be <code>null</code>.
     * @throws SetUpException
     *             Throws the SetUpException when the path to log to is not a
     *             valid directory.
     */

    public void setup(@NonNull Configuration config) throws SetUpException {
        this.fileLogging = config.getValue(DefaultSettings.LOG_FILE);
        this.consoleLogging = config.getValue(DefaultSettings.LOG_CONSOLE);
        this.level = config.getValue(DefaultSettings.LOG_LEVEL);

        if (fileLogging) {
            logFile = new File(config.getValue(DefaultSettings.LOG_DIR), 
                    Timestamp.INSTANCE.getFilename("KernelHaven", "log"));
        }

        if (!consoleLogging) {
            targets.clear();
        }
        
        if (fileLogging) {
            // if console logging is still on, then we can shorten that one
            if (!targets.isEmpty()) {
                targets.get(0).maxLogLines = LOG_MESSAGE_SIZE_CONSOLE_LIMIT;
            }
            
            try {
                targets.add(new Target(new FileOutputStream(logFile)));
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
     * Initializes the logger to log to stdout in UTF-8.
     */
    public static void init() {
        init(notNull(System.out));
    }

    /**
     * Initializes the logger to log in UTF-8. Target must not be null.
     * 
     * @param target
     *            The output target of the logger. The logger will only write to
     *            it if it obtains a lock on it. Must not be null.
     */
    public static void init(@NonNull OutputStream target) {
        init(target, notNull(Charset.forName("UTF-8")));
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
    public static void init(@NonNull OutputStream target, @NonNull Charset charset) {
        instance = new Logger(charset);
        instance.targets.add(new Target(target));
    }

    /**
     * Gets the singleton instance of Logger.
     *
     * @return the logger
     */
    public static @Nullable Logger get() {
        return instance;
    }
    
    /**
     * Overwrite the current log level.
     * 
     * @param level The new log level.
     */
    public void setLevel(@NonNull Level level) {
        this.level = level;
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
        String timestamp = new Timestamp().getTimestamp();

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
    private void log(@NonNull Level level, String @NonNull ... lines) {
        if (!this.level.isLog(level)) {
            return;
        }
        String header = constructHeader(level.toString());
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
    public void logInfo(String @NonNull ... lines) {
        log(Level.INFO, lines);
    }

    /**
     * Logs a log entry with the log level "debug".
     * 
     * @param lines
     *            The content of the log entry. Must not be null.
     */
    public void logDebug(String @NonNull ... lines) {
        log(Level.DEBUG, lines);
    }

    /**
     * Logs a log entry with the log level "warning".
     * 
     * @param lines
     *            The content of the log entry.
     */
    public void logWarning(String @NonNull ... lines) {
        log(Level.WARNING, lines);
    }

    /**
     * Logs a log entry with the log level "error".
     * 
     * @param lines
     *            The content of the log entry. Must not be null.
     */
    public void logError(String @NonNull ... lines) {
        log(Level.ERROR, lines);
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
    private void exceptionToString(@NonNull Throwable exc, @NonNull List<String> lines) {
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
     *            The exception to log. A stack trace will be logged.
     */
    private void logException(@NonNull Level level, @NonNull String comment, @Nullable Throwable exc) {
        List<String> lines;
        if (exc != null) {
            lines = new ArrayList<>(exc.getStackTrace().length + 2);
            lines.add(comment + ":");
            exceptionToString(exc, lines);
            
        } else {
            lines = new ArrayList<>(1);
            lines.add(comment + ": <exception is null>");
        }
        log(level, notNullArrayWithNotNullContent(lines.toArray(new String[0])));
    }

    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "error".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged.
     */
    public void logException(@NonNull String comment, @Nullable Throwable exc) {
        logException(Level.ERROR, comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "debug".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged.
     */
    public void logExceptionDebug(@NonNull String comment, @Nullable Throwable exc) {
        logException(Level.DEBUG, comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "warning".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged.
     */
    public void logExceptionWarning(@NonNull String comment, @Nullable Throwable exc) {
        logException(Level.WARNING, comment, exc);
    }
    
    /**
     * Creates a log entry from the given comment and exception. The log level
     * is "info".
     * 
     * @param comment
     *            A comment that is displayed above the exception. A ":" is
     *            appended to it by this method. Must not be null.
     * @param exc
     *            The exception to log. A stack trace will be logged.
     */
    public void logExceptionInfo(@NonNull String comment, @Nullable Throwable exc) {
        logException(Level.INFO, comment, exc);
    }
    
    /**
     * Gets the target logging file.
     * 
     * @return the file used as logging target. May be null if not logging to a file.
     */
    public @Nullable File getLogFile() {
        return logFile;
    }

}