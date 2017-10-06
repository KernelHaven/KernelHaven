package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;

/**
 * The Class LoggerTest.
 * 
 * @author moritz
 * @author alice
 * @author adam
 */
public class LoggerTest {

    /**
     * Test levels.
     */
    @Test
    public void testLevels() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        l.logError("a");
        l.logWarning("a");
        l.logInfo("a");
        l.logDebug("a");
        l.logException("a", new Exception());

        String[] lines = out.toString().split("\n");

        Assert.assertThat(lines[0], startsWith("[error]"));
        Assert.assertThat(lines[1], startsWith("[warning]"));
        Assert.assertThat(lines[2], startsWith("[info]"));
        Assert.assertThat(lines[3], startsWith("[debug]"));
        Assert.assertThat(lines[4], startsWith("[error]"));
    }

    /**
     * Tests if content of log-event is represented in log.
     */
    @Test
    public void testContent() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        String content = "This is a test text";
        l.logInfo(content);

        String result = out.toString();

        Assert.assertTrue(result.endsWith(content + "\n"));
    }

    /**
     * Test logger behavior for multiple line output.
     */
    @Test
    public void testMultipleLines() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        String line0 = "This is a test text";
        String line1 = "This is another test text";
        String line2 = "This is another line in the output";
        l.logInfo(line0, line1, line2);

        String[] lines = out.toString().split("\n");

        Assert.assertThat(lines[0], endsWith(line0));
        Assert.assertThat(lines[1], endsWith(line1));
        Assert.assertThat(lines[2], endsWith(line2));
    }

    /**
     * Test logging of exceptions.
     */
    @Test
    public void testLogException() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        try {
            throw new LoggerTestException("This is a test");
        } catch (LoggerTestException e) {
            l.logException("This is the comment", e);
        }

        String[] lines = out.toString().split("\n");
        Assert.assertThat(lines[0], endsWith("This is the comment:"));
        Assert.assertThat(lines[1].trim(),
                equalTo("net.ssehub.kernel_haven.util.LoggerTest$LoggerTestException: This is a test"));
        for (int i = 2; i < lines.length; i++) {
            Assert.assertTrue(lines[i].trim().startsWith("at "));
        }
    }

    /**
     * Test logging of exception with Cause.
     */
    @Test
    public void testLogExceptionWithCause() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        try {
            try {
                throw new LoggerTestException("This is a test");
            } catch (LoggerTestException e) {
                throw new LoggerTestException("This is another test", e);
            }
        } catch (LoggerTestException e) {
            l.logException("This is the comment", e);
        }

        String[] lines = out.toString().split("\n");
        Assert.assertTrue(lines[0].endsWith("This is the comment:"));
        Assert.assertThat(lines[1].trim(), equalTo(
                "net.ssehub.kernel_haven.util.LoggerTest$LoggerTestException: This is another test"));

        int i = 2;
        while (lines[i].trim().startsWith("at ")) {
            i++;
        }

        Assert.assertTrue(lines[i].trim().equals("Caused by:"));
        i++;
        Assert.assertThat(lines[i].trim(),
                equalTo("net.ssehub.kernel_haven.util.LoggerTest$LoggerTestException: This is a test"));
        i++;
        for (; i < lines.length; i++) {
            Assert.assertThat(lines[i].trim(), startsWith("at "));
        }
    }
    
    /**
     * Test exception levels.
     */
    @Test
    public void testExceptionLevels() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        l.logException("one", new Exception());
        l.logExceptionWarning("two", new Exception());
        l.logExceptionInfo("two", new Exception());
        l.logExceptionDebug("two", new Exception());

        String[] lines = out.toString().split("\n");
        
        int i = 0;
        String[] expectedPrefixes = {"[error]", "[warning]", "[info]", "[debug]" };

        for (String line : lines) {
            if (!line.startsWith(" ")) {
                assertThat(line, startsWith(expectedPrefixes[i]));
                i++;
            }
        }
    }

    /**
     * Test if thread names are represented correctly in the log.
     */
    @Test
    public void testThreadNames() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut

        Thread.currentThread().setName("test1");
        l.logInfo("test");

        Thread.currentThread().setName("test2");
        l.logInfo("test");

        String[] lines = out.toString().split("\n");

        Assert.assertThat(lines[0], containsString("[test1]"));
        Assert.assertThat(lines[0], not(containsString("[test2]")));

        Assert.assertThat(lines[1], containsString("[test2]"));
        Assert.assertThat(lines[1], not(containsString("[test1]")));
    }

    /**
     * Test logger with multiple threads that are started right after another.
     *
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    public void testMultiThreading() throws InterruptedException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);

        Worker w1 = new Worker("worker 1", "message 1");
        Worker w2 = new Worker("worker 2", "message 2");
        Worker w3 = new Worker("worker 3", "message 3");
        Worker w4 = new Worker("worker 4", "message 4");

        new Thread(w1).start();
        new Thread(w2).start();
        new Thread(w3).start();
        new Thread(w4).start();

        while (!w1.isDone() || !w2.isDone() || !w3.isDone() || !w4.isDone()) {
            Thread.sleep(10);
        }

        String[] lines = out.toString().split("\n");
        Assert.assertEquals(4 * 1000, lines.length);

        for (int i = 0; i < lines.length; i++) {
            Assert.assertThat(lines[i], startsWith("[info]"));
            int number = Integer.parseInt(lines[i].substring(lines[i].length() - 1));
            Assert.assertThat(lines[i], endsWith("[worker " + number + "] message " + number));
        }
    }
    
    /**
     * Tests whether the logger is correctly created from the configuration.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testConfigurationInitOnlyConsole() throws SetUpException {
        Properties props = new Properties();
        TestConfiguration config = new TestConfiguration(props);
        
        Logger.init();
        Logger logger = Logger.get();
        logger.setup(config);
        
        List<Logger.Target> targets = logger.getTargets();
        assertThat(targets.size(), is(1));
        
        Logger.Target target = targets.get(0);
        
        assertThat(target.getOut(), is(System.out));
        assertThat(target.getMaxLogLines(), is(0));
        
        // clean up
        Logger.init();
    }
    
    /**
     * Tests whether the logger is correctly created from the configuration.
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testConfigurationInitOnlyFile() throws SetUpException, IOException {
        Properties props = new Properties();
        props.setProperty("log.console", "false");
        props.setProperty("log.file", "true");
        TestConfiguration config = new TestConfiguration(props);
        
        Logger.init();
        Logger logger = Logger.get();
        logger.setup(config);
        
        List<Logger.Target> targets = logger.getTargets();
        assertThat(targets.size(), is(1));
        
        Logger.Target target = targets.get(0);
        
        assertThat(target.getOut(), instanceOf(FileOutputStream.class));
        assertThat(target.getMaxLogLines(), is(0));
        
        
        // clean up
        target.getOut().close();
        logger.getLogFile().delete();
        Logger.init();
    }
    
    /**
     * Tests whether the logger is correctly created from the configuration.
     * @throws SetUpException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testConfigurationInitBothConsoleAndFile() throws SetUpException, IOException {
        Properties props = new Properties();
        props.setProperty("log.file", "true");
        TestConfiguration config = new TestConfiguration(props);
        
        Logger.init();
        Logger logger = Logger.get();
        logger.setup(config);
        
        List<Logger.Target> targets = logger.getTargets();
        assertThat(targets.size(), is(2));
        
        Logger.Target target = targets.get(0);
        
        assertThat(target.getOut(), is(System.out));
        assertThat(target.getMaxLogLines(), is(10));
        
        target = targets.get(1);
        
        assertThat(target.getOut(), instanceOf(FileOutputStream.class));
        assertThat(target.getMaxLogLines(), is(0));
        
        // clean up
        target.getOut().close();
        logger.getLogFile().delete();
        Logger.init();
    }

    /**
     * Tests whether the logger correctly cuts of long messages.
     * @throws IOException unwanted.
     * @throws SetUpException unwanted.
     */
    @Test
    public void testMaxLines() throws IOException, SetUpException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger.init(out);
        Logger l = Logger.get(); // just a shortcut
        
        Properties props = new Properties();
        props.setProperty("log.file", "true");
        TestConfiguration config = new TestConfiguration(props);
        l.setup(config);
        
        l.logInfo("This", "is", "a", "log", "message", "with", "a", "lot", "of", "lines", "in", "it"); // 12 lines
        
        String result = out.toString();
        String[] lines = result.split("\n");
        
        // we expect 12 lines: 10 (maxLogLines) + 1 (the log shortened message)
        //                      + 1 (a previous message about file logging)
        assertThat(lines.length, is(l.getTargets().get(0).getMaxLogLines() + 2));
        assertThat(lines[lines.length - 1].trim(), is("... (log shortened, see log file for full output)"));
        
        // clean up
        l.getTargets().get(1).getOut().close();
        l.getLogFile().delete();
        Logger.init();
    }
    
    /**
     * The Class Worker. Helper Class for tests.
     */
    private static class Worker implements Runnable {

        /** The name. */
        private String name;

        /** The message. */
        private String message;

        /** The done. */
        private boolean done;

        /**
         * Instantiates a new worker.
         *
         * @param name
         *            Must not be null.
         * @param message
         *            The Message of the logInfo. Must not be null.
         */
        public Worker(String name, String message) {
            this.name = name;
            this.message = message;
            this.done = false;
        }

        /**
         * Checks if is done.
         *
         * @return true, if is done
         */
        public boolean isDone() {
            return done;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);

            for (int i = 0; i < 1000; i++) {
                Logger.get().logInfo(message);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }

            done = true;
        }

    }

    /**
     * The Class LoggerTestException.
     */
    private class LoggerTestException extends RuntimeException {

        /**
         * Generated Serial version UID.
         */
        private static final long serialVersionUID = -7059614939949460856L;

        /**
         * Instantiates a new logger test exception.
         *
         * @param test
         *            is the test exception message.
         */
        public LoggerTestException(String test) {
            super(test);
        }

        /**
         * Instantiates a new logger test exception.
         *
         * @param test
         *            is the test exception message.
         * @param exc
         *            is the type of the exception.
         */
        public LoggerTestException(String test, Exception exc) {
            super(test, exc);
        }

    }

}
