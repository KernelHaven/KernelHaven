package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A utility class for periodically logging the progress of a long-running task. This class is fully thread-safe.
 * 
 * @author Adam
 */
public class ProgressLogger implements Closeable {

    private static final ProgressLoggerThread LOG_THREAD = new ProgressLoggerThread();
    
    /**
     * The interval to log in, in milliseconds.
     */
    private static int interval = Integer.parseInt(DefaultSettings.LOG_PROGRESS_INTERVAL.getDefaultValue());
    
    private @NonNull String task;
    
    private int numItems;
    
    private @NonNull AtomicInteger processedItems;
    
    private @NonNull AtomicBoolean finished;
    
    private long tStart;
    
    private long tEnd;
    
    /**
     * Creates a  new {@link ProgressLogger} for the given task, without an estimated number of items to process.
     * 
     * @param task The name of the task. This will appear in the log.
     */
    public ProgressLogger(@NonNull String task) {
        this(task, -1);
    }
    
    /**
     * Creates a  new {@link ProgressLogger} for the given task, with an estimated number of items to process.
     * 
     * @param task The name of the task. This will appear in the log.
     * @param numItems The number of items to process. The percentage of progress is calculated from this. -1 if if no
     *      number of items can be estimated.
     */
    public ProgressLogger(@NonNull String task, int numItems) {
        this.task = task;
        this.numItems = numItems;
        this.processedItems = new AtomicInteger(0);
        this.finished = new AtomicBoolean(false);
        
        LOG_THREAD.add(this);
        
        this.tStart = System.currentTimeMillis();
    }
    
    /**
     * Signals that another item is processed.
     */
    public void processedOne() {
        processedItems.incrementAndGet();
    }
    
    /**
     * Signals that a number of items are processed.
     * 
     * @param numDone The number of items that are processed. This is added to the previous amount of processed items.
     */
    public void processed(int numDone) {
        processedItems.addAndGet(numDone);
    }
    
    /**
     * Signals that the task is done. This should be called, even if the number of items is reached via processed()
     * calls. This will immediately trigger a log message.
     */
    @Override
    public void close() {
        this.tEnd = System.currentTimeMillis();
        this.finished.set(true);
        
        // trigger a new log round
        synchronized (LOG_THREAD) {
            LOG_THREAD.notifyAll();
        }
    }
    
    /**
     * Sets the interval for logging the status of {@link ProgressLogger}s.
     * 
     * @param interval The logging interval, in milliseconds.
     */
    static void setInterval(int interval) {
        ProgressLogger.interval = interval;
        
        // skip current waiting, so that shorter intervals have an immediate effect
        synchronized (LOG_THREAD) {
            LOG_THREAD.notifyAll();
        }
    }
    
    /**
     * The thread that periodically logs the status of the {@link ProgressLogger}s.
     */
    private static class ProgressLoggerThread extends Thread {
        
        private List<@NonNull ProgressLogger> list;
        
        /**
         * Creates and starts this logger. Used only for the singleton instance.
         */
        ProgressLoggerThread() {
            super("ProgressLogger");
            
            this.list = new LinkedList<>();
            
            setDaemon(true); // this thread will run forever
            start();
        }

        /**
         * Adds a new {@link ProgressLogger} to observe. This will be observed (and periodically logged) until it is
         * closed.
         * 
         * @param progressLogger The {@link ProgressLogger} to add.
         */
        void add(@NonNull ProgressLogger progressLogger) {
            synchronized (list) {
                list.add(progressLogger);
            }
        }
        
        @Override
        public void run() {
            while (true) {
                List<String> lines;
                
                synchronized (list) {
                    lines = new ArrayList<>(list.size());
                    
                    for (int i = 0; i < list.size(); i++) {
                        ProgressLogger logger = notNull(list.get(i));
                        
                        int max = logger.numItems;
                        int current = logger.processedItems.get();
                        boolean done = logger.finished.get();
                        
                        String finishedStr = "";
                        if (done) {
                            finishedStr = " and finished in " + Util.formatDurationMs(logger.tEnd - logger.tStart);
                        }
                        
                        if (max >= 0) {
                            lines.add(String.format("%s processed %d of %d (%d%%) items%s",
                                    logger.task, current, max, (int) (current * 100.0 / max), finishedStr));
                        } else {
                            lines.add(String.format("%s processed %d items%s", logger.task, current, finishedStr));
                        }

                        // only remove after we logged the final message
                        if (done) {
                            list.remove(i);
                            i--; // decrement because list is moved up
                        }
                    }
                }
                
                if (!lines.isEmpty()) {
                    Logger.get().logInfo(lines.toArray(new String[0]));
                }
                
                try {
                    // wait instead of sleep, so that close() can trigger a log message via notifyAll()
                    synchronized (this) {
                        wait(interval);
                    }
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        
    }
    
}
