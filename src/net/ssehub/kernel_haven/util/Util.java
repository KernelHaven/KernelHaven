package net.ssehub.kernel_haven.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;

/**
 * Utility functions.
 * 
 * @author Adam
 * @author Manu
 * @author Johannes
 * @author Moritz
 */
public class Util {

    private static final Logger LOGGER = Logger.get();

    /**
     * Don't allow any instances of this class.
     */
    private Util() {
    }

    /**
     * Returns the directory where the given extractor can store or read its resources from.
     * Extractors have their own directories named the same as their fully qualified class names
     * in a global resource directory specified in the config.
     * 
     * If the directory for the specified extractor is not yet created, then this methods creates it.
     * 
     * @param config The configuration to read the {@link DefaultSettings#RESOURCE_DIR} setting from.
     * @param extractor The extractor that wants to store or read data.
     * @return The directory where the specified extractor can put and read its resources. Never null.
     * 
     * @throws SetUpException If creating the resource directory failed for some reason.
     */
    public static File getExtractorResourceDir(Configuration config, Class<?> extractor) throws SetUpException {
        File extractorResDir = new File(config.getValue(DefaultSettings.RESOURCE_DIR), extractor.getName());
        extractorResDir.mkdir();
        
        if (!extractorResDir.isDirectory() || !extractorResDir.canWrite()) {
            throw new SetUpException("Couldn't create resource dir for extractor " + extractor.getName());
        }
        
        return extractorResDir;
    }
    
    /**
     * Extracts a resource from this jar (this ClassLoader, to be precise) to a temporary file.
     * 
     * @param resource
     *            The resource to extract. Must contain at least one dot (".") in the filename. Must not be null.
     * @return The temporary file.
     * 
     * @throws FileNotFoundException
     *             If the specified resource was not found.
     * @throws IOException
     *             If extracting the resource fails.
     */
    public static File extractJarResourceToTemporaryFile(String resource) throws IOException {
        // split at dot, to find the file suffix
        int index = resource.lastIndexOf('.');
        File tempFile = File.createTempFile("resource", resource.substring(index));

        extractJarResourceToFile(resource, tempFile);
        return tempFile;
    }

    /**
     * Extracts a resource from this jar (this ClassLoader, to be precise) to a specified file.
     * 
     * @param resource
     *            The resource to extract. Must not be null.
     * @param destination
     *            The destination file to write the content to (this is overwritten if already present). Must not be
     *            null.
     * 
     * @throws FileNotFoundException
     *             If the specified resource was not found.
     * @throws IOException
     *             If extracting the resource fails.
     */
    public static void extractJarResourceToFile(String resource, File destination) throws IOException {
        InputStream in = Util.class.getClassLoader().getResourceAsStream(resource);

        if (in == null) {
            throw new FileNotFoundException("Resource \"" + resource + "\" not found");
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(destination);
            copyStream(in, out);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    /**
     * Reads a complete stream until it is closed.
     * 
     * @param in
     *            The stream to read.
     * @return The contents of the stream.
     * @throws IOException
     *             If reading the stream fails.
     */
    public static String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        copyStream(in, out);
        return out.toString();
    }

    /**
     * Waits until the given process has finished.
     * 
     * @param process
     *            The process to wait for. Must not be <code>null</code>.
     * @return The exit code of the process.
     */
    public static int waitForProcess(Process process) {
        return waitForProcess(process, 0);
    }

    /**
     * Waits until the given process has finished. If the given timeout was reached, then the process is forcibly
     * terminated.
     * 
     * @param process
     *            The process to wait for. Must not be <code>null</code>.
     * @param timeout
     *            The maximum time to wait (in milliseconds) until the process is killed forcibly. 0 if no timeout
     *            should be used.
     * @return The exit code of the process, or <code>null</code> if the process was killed.
     */
    public static Integer waitForProcess(Process process, long timeout) {
        int returnValue = 0;
        boolean timeoutReached = false;

        boolean success = false;
        do {
            try {
                if (timeout != 0) {
                    timeoutReached = !process.waitFor(timeout, TimeUnit.MILLISECONDS);
                } else {
                    process.waitFor();
                }
                success = true;
            } catch (InterruptedException e) {
            }
        } while (!success);

        if (timeoutReached) {
            process.destroyForcibly();
            waitForProcess(process);
        }

        returnValue = process.exitValue();

        return timeoutReached ? null : returnValue;
    }

    /**
     * Runs the process until it is finished. Logs the output from stderr and stdout to the logger.
     * 
     * @param processBuilder
     *            The process to start and run. Must not be null.
     * @param name
     *            The name of this process; used for logging only. Must not be null.
     * 
     * @return <code>true</code> if successful (i.e. exit code != 0); <code>false</code> otherwise.
     * 
     * @throws IOException
     *             If executing the process or reading it's output fails.
     */
    public static boolean executeProcess(ProcessBuilder processBuilder, String name) throws IOException {
        Process process = processBuilder.start();

        BufferThread th1 = new BufferThread(process.getInputStream());
        BufferThread th2 = new BufferThread(process.getErrorStream());
        th1.start();
        th2.start();

        int returnValue = waitForProcess(process);

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            // This never happens
        }

        String stdout = th1.content;
        if (stdout != null && !stdout.equals("")) {
            LOGGER.logDebug(("Stdout:\n" + stdout).split("\n"));
        }
        String stderr = th2.content;
        if (stderr != null && !stderr.equals("")) {
            LOGGER.logDebug(("Stderr:\n" + stderr).split("\n"));
        }

        return returnValue == 0;
    }

    /**
     * Runs the process until it is finished. Writes the output of stdout and stderr to the given streams. The streams
     * are closed when the output of the process is closed.
     * 
     * @param processBuilder
     *            The process to start and run. Must not be null.
     * @param name
     *            The name of this process; used for logging only. Must not be null.
     * @param stdout
     *            The stream to write the standard output of the process to.
     * @param stderr
     *            The stream to write the error output of the process to.
     * @param timeout
     *            The maximum time to wait (in milliseconds) until the process is killed forcibly. 0 if no timeout
     *            should be used.
     * 
     * @return <code>true</code> if successful (i.e. exit code != 0); <code>false</code> otherwise.
     * 
     * @throws IOException
     *             If executing the process or reading it's output fails.
     */
    public static boolean executeProcess(ProcessBuilder processBuilder, String name, OutputStream stdout,
            OutputStream stderr, long timeout) throws IOException {
        Process process = processBuilder.start();

        BufferThread th1 = new BufferThread(process.getInputStream(), stdout);
        BufferThread th2 = new BufferThread(process.getErrorStream(), stderr);
        th1.start();
        th2.start();

        Integer returnValue = waitForProcess(process, timeout);

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            // This never happens
        }

        return returnValue != null && returnValue == 0;
    }

    /**
     * Buffer that reads the output stream of a process. This is needed so that the output buffer for stdout/stderr of
     * the process doesn't get full; this would cause the process to halt (deadlock).
     */
    private static class BufferThread extends Thread {

        private InputStream in;

        private OutputStream out;

        private String content;

        /**
         * Creates a new bufferthread to read from the given stream.
         * 
         * @param in
         *            The stream to read.
         */
        public BufferThread(InputStream in) {
            this.in = in;
        }

        /**
         * Creates a new bufferthread to read from the given stream and write into the given stream.
         * 
         * @param in
         *            The stream to read from.
         * @param out
         *            The stream to write to.
         */
        public BufferThread(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                if (out == null) {
                    content = readStream(in);
                } else {
                    copyStream(in, out);
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.logException("Exception while reading process output", e);
            }
        }
    }

    /**
     * Deletes a folder. This recursively deletes all the contents of the folder and all its subfolders.
     * 
     * @param folder
     *            The folder to delete.
     * @throws IOException
     *             If deleting the folder fails.
     */
    public static void deleteFolder(File folder) throws IOException {
        Files.walk(folder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    /**
     * Copies the contents of one file to another file.
     * 
     * @param src
     *            The source file, to read the contents from. If null IOException.
     * @param dst
     *            The destination file, to write the contents to. If null IOException.
     * @throws IOException
     *             If reading or writing operations fail.
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {

            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            copyStream(in, out);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    /**
     * Copies the complete content of InputStream in to OutputStream out. This reads from in until the end of stream is
     * reached. This does not close the streams.
     * 
     * @param in
     *            The stream to read from. Must not be null.
     * @param out
     *            The stream to write to. Must not be null.
     * 
     * @throws IOException
     *             If reading or writing throws an exception.
     */
    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        final int bufferSize = 1024;

        byte[] buffer = new byte[bufferSize];

        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Formats the given amount of bytes correctly as B, KiB, MiB GiB or TiB. This uses 2 as the base, e.g. 1024 B = 1
     * KiB. The result is given with two digits precision, always rounded down.
     * 
     * @param amount
     *            The amount of bytes.
     * 
     * @return A string representing the amount of bytes.
     */
    public static String formatBytes(long amount) {

        int i = 0;
        String[] suffix = {"B", "KiB", "MiB", "GiB", "TiB"};
        amount *= 100; // this way we get two digits of precision after the comma

        while (i < suffix.length && amount >= 102400) {
            i++;
            amount /= 1024;
        }

        String result;
        if (amount - ((amount / 100) * 100) == 0) {
            // if the last two digits are 0, then we don't need floating point precision here
            result = (amount / 100) + " " + suffix[i];
        } else {
            result = (amount / 100.0) + " " + suffix[i];
        }

        return result;
    }
    
    /**
     * Platforms / operating sytems.
     * 
     * @author El-Sharkawy
     */
    public static enum OSType {
        WIN32, WIN64, LINUX32, LINUX64, MACOS64;
    }
    
    /**
     * Determines the installed operating system (including bit version of the OS).
     * 
     * @return OS and bit version of the OS, <tt>null</tt> if it could not be determined.
     * 
     * @see <a href="https://stackoverflow.com/a/18417382">https://stackoverflow.com/a/18417382</a> for determining
     * rules.
     */
    public static OSType determineOS() {
        OSType result = null;
        String os = System.getProperty("os.name", "generic").toLowerCase();
        // Checks only if the JRE is a 64 Bit version, but it's still possible to install 32 Bit Java on 64 Bit OS.
        boolean is64JRE = (System.getProperty("os.arch").indexOf("64") != -1);
        
        if (null != os) {
            if (os.startsWith("win")) {
                if (is64JRE || (System.getenv("ProgramFiles(x86)") != null)) {
                    result = OSType.WIN64;
                } else {
                    result = OSType.WIN32;
                }
            } else if (os.indexOf("nux") >= 0) {
                if (is64JRE) {
                    // TODO: This is not complete test, since you might install a 32 Bit JRE on 64 Bit Linux.
                    result = OSType.LINUX64;
                } else {
                    result = OSType.LINUX32;
                }
            } else if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
                // As far as I know is 32 Bit MacOS now longer supported
                result = OSType.MACOS64;
            }
        }
        
        return result;
    }

}
