package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests the utility functions from the Util class.
 * 
 * @author Adam
 * @author Manu
 */
public class UtilTest {

    private static final String BASE_RES = "net/ssehub/kernel_haven/util/test_resources/";
    
    /**
     * Initializes the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }
    
    
    /**
     * Tests whether the extractJarResourceToTemporaryFile() method throws an exception, if the
     * specified resource is not there.
     * 
     * @throws IOException unwanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testExtractJarResourceToTemporaryFileNotExiting() throws IOException {
        Util.extractJarResourceToTemporaryFile(BASE_RES + "non_existing.txt");
    }
    
    /**
     * Tests whether the extractJarResourceToTemporaryFile() method correctly extracts a file.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExtractJarResourceToTemporaryFile() throws IOException {
        File extracted = Util.extractJarResourceToTemporaryFile(BASE_RES + "test.txt");
        
        assertThat(extracted.isFile(), is(true));
        
        BufferedReader in = new BufferedReader(new FileReader(extracted));
        assertThat(in.readLine(), is("This is a test file."));
        in.close();
        
        // cleanup
        extracted.delete();
    }
    
    /**
     * Tests whether the extractJarResourceToTemporaryFile() method sets a correct file suffix.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExtractJarResourceToTemporaryFileSuffix() throws IOException {
        File extracted = Util.extractJarResourceToTemporaryFile(BASE_RES + "test.txt");
        
        assertThat(extracted.getName().endsWith(".txt"), is(true));
        
        // cleanup
        extracted.delete();
    }
    
    /**
     * Tests whether read stream correctly returns the complete stream data.
     *  
     * @throws IOException unwanted.
     */
    @Test
    public void testReadStream() throws IOException {
        String input = "This is some stream data that needs to be tested.";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        
        String read = Util.readStream(in);
        
        assertThat(read, is(input));
    }
    
    
    /**
     * Tests whether waitForProcess actually waits for the process to finish.
     * @throws IOException unwanted.
     */
    @Test
    public void testWaitForProcess() throws IOException {
        ProcessBuilder builder = setUpTestProcess("sleep", 0);
        
        Process process = builder.start();
        assertThat(Util.waitForProcess(process), is(0));
        assertThat(process.isAlive(), is(false));
    }
    
    /**
     * Tests whether waitForProcess with a timeout waits for the process if the timeout is greater than process
     * executiontime.
     * @throws IOException unwanted.
     */
    @Test
    public void testWaitForProcessWithTimeout() throws IOException {
        ProcessBuilder builder = setUpTestProcess("sleep", 0);
        
        Process process = builder.start();
        assertThat(Util.waitForProcess(process, 2000), is(0));
        assertThat(process.isAlive(), is(false));
    }
    
    /**
     * Tests whether waitForProcess with a timeout kills the process.
     * @throws IOException unwanted.
     */
    @Test
    public void testWaitForProcessWithTimeoutKill() throws IOException {
        ProcessBuilder builder = setUpTestProcess("sleep", 0);
        
        Process process = builder.start();
        assertThat(Util.waitForProcess(process, 1), not(is(0)));
        assertThat(process.isAlive(), is(false));
    }
    
    /**
     * Tests whether waitForProcess returns the correct exit code.
     * @throws IOException unwanted.
     */
    @Test
    public void testWaitForProcessExitStatus() throws IOException {
        ProcessBuilder builder = setUpTestProcess("none", 0);
        Process process = builder.start();
        assertThat(Util.waitForProcess(process), is(0));
        
        builder = setUpTestProcess("none", 174);
        process = builder.start();
        assertThat(Util.waitForProcess(process), is(174));
    }
    
    /**
     * Tests whether executeProcess() correctly executes a process.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExecuteProcess() throws IOException {
        ProcessBuilder builder = setUpTestProcess("print", 0);
        
        boolean success = Util.executeProcess(builder, "testprocess");
        assertThat(success, is(true));
    }
    
    /**
     * Tests whether executeProcess() correctly detects a non-zero exit code.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExecuteProcessExitFail() throws IOException {
        ProcessBuilder builder = setUpTestProcess("none", 1);
        
        boolean success = Util.executeProcess(builder, "testprocess");
        assertThat(success, is(false));
    }
    
    /**
     * Tests whether the input and output streams correctly hand us the process output.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExecuteProcessStreams() throws IOException {
        ProcessBuilder builder = setUpTestProcess("print", 0);
        
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        
        boolean success = Util.executeProcess(builder, "testprocess", stdout, stderr, 0);
        assertThat(success, is(true));
        
        String out = stdout.toString();
        assertThat(out, startsWith("This is the standard output stream"));
        assertThat(out.length(), either(is(35)).or(is(36))); // 34 chars + (1 for \n, 2 for \r\n)
        
        String err = stderr.toString();
        assertThat(err, startsWith("This is the standard error stream"));
        assertThat(err.length(), either(is(34)).or(is(35))); // 33 chars + (1 for \n, 2 for \r\n)
    }
    
    /**
     * Creates a process builder for a process that either sleeps or prints output.
     * 
     * @param command Either "sleep" or "none"
     * @param exitCode The wanted exit code of the process.
     * @return A process builder for the specified process.
     */
    private ProcessBuilder setUpTestProcess(String command, int exitCode) {
        ProcessBuilder builder = new ProcessBuilder("java",
                "-cp", System.getProperty("java.class.path"),
                getClass().getName(),
                command, exitCode + "");
        
        return builder;
    }

    /**
     * A main method that is launched as a process for the process testing methods.
     * @param args An array with the following fields:
     *      <ul>
     *          <li>[0] either "sleep" or "none"</li>
     *          <li>[1] The exit code</li>
     *      </ul>
     */
    public static void main(String[] args) {
        if (args[0].equals("sleep")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        } else if (args[0].equals("print")) {
            System.out.println("This is the standard output stream");
            System.err.println("This is the standard error stream");
        }
        
        System.exit(Integer.parseInt(args[1]));
    }
    
    /**
     * Tests if deleteFolder() correctly deletes folders.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testDeleteFolder() throws IOException {
        // preconditions
        File dir = File.createTempFile("test", ".dir");
        dir.delete();
        dir.mkdir();
        assertThat(dir.isDirectory(), is(true));
        
        File f1 = new File(dir, "file1.txt");
        f1.createNewFile();
        assertThat(f1.isFile(), is(true));
        
        File dir2 = new File(dir, "dir2");
        dir2.mkdir();
        assertThat(dir2.isDirectory(), is(true));

        File f2 = new File(dir2, "file2.txt");
        f2.createNewFile();
        assertThat(f2.isFile(), is(true));
        
        // start test
        Util.deleteFolder(dir);
        
        assertThat(dir.isDirectory(), is(false));
        assertThat(f1.isFile(), is(false));
        assertThat(dir2.isDirectory(), is(false));
        assertThat(f2.isFile(), is(false));
    }
    
    /**
     * Tests if copy file works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCopyFile() throws IOException {
        File src = new File("testdata/testfile.txt");
        
        File dst = new File("testdata/testfile_copy.txt");
        // precondition
        assertThat(dst.exists(), is(false));
        
        Util.copyFile(src, dst);
        
        // check if file has the correct content
        BufferedReader in = new BufferedReader(new FileReader(dst));
        
        assertThat(in.readLine(), is("Hello World!"));
        assertThat(in.readLine(), is(""));
        assertThat(in.readLine(), is("This is a test file"));
        assertThat(in.readLine(), nullValue());
        
        in.close();
        

        // postcondition
        dst.delete();
        assertThat(dst.exists(), is(false));
    }
    
}
