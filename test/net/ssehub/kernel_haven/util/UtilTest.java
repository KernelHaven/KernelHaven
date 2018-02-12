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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.test_utils.FileContentsAssertion;
import net.ssehub.kernel_haven.util.Util.OSType;


/**
 * Tests the utility functions from the Util class.
 * 
 * @author Adam
 * @author Manu
 */
@SuppressWarnings("null")
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
                Thread.sleep(200);
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
    
    /**
     * Tests {@link Util#copyFolder(File, File)}.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCopyFolder() throws IOException {
        File src = new File("testdata/folderToCopy");
        File dst = new File("testdata/tmp_folder_copy");
        
        try {
            dst.mkdir();
            
            // precondition
            assertThat(dst.listFiles().length, is(0));
            
            Util.copyFolder(src, dst);
            
            Map<String, File> files = new HashMap<>();
            for (File f : dst.listFiles()) {
                files.put(f.getName(), f);
            }
            HashSet<String> expectedNames = new HashSet<>();
            expectedNames.add("subFolder");
            expectedNames.add("a.txt");
            assertThat(files.keySet(), is(expectedNames));
            
            FileContentsAssertion.assertContents(files.get("a.txt"), "Hello World!");
            
            File subFolder = files.get("subFolder");
            files.clear();
            for (File f : subFolder.listFiles()) {
                files.put(f.getName(), f);
            }
            
            expectedNames.clear();
            expectedNames.add("b.txt");
            assertThat(files.keySet(), is(expectedNames));
            
            FileContentsAssertion.assertContents(files.get("b.txt"), "Hello Again!");
            
        } finally {
            Util.deleteFolder(dst);
        }
    }
    
    /**
     * Tests that {@link Util#copyFolder(File, File)} correctly throws an exception if the target doesn't exist.
     * 
     * @throws IOException expected.
     */
    @Test(expected = IOException.class)
    public void testCopyFolderNotExistingTarget() throws IOException {
        File src = new File("testdata/folderToCopy");
        File dst = new File("testdata/doesnt_exist");
        
        Util.copyFolder(src, dst);
    }
    
    /**
     * Tests that {@link Util#copyFolder(File, File)} correctly throws an exception if the source doesn't exist.
     * 
     * @throws IOException expected.
     */
    @Test(expected = IOException.class)
    public void testCopyFolderNotExistingSource() throws IOException {
        File src = new File("testdata/doesnt_exist");
        File dst = new File("testdata/tmp_folder_copy");
        
        try {
            dst.mkdir();
            
            Util.copyFolder(src, dst);
            
        } finally {
            Util.deleteFolder(dst);
        }
    }
    
    /**
     * Tests that {@link Util#copyFolder(File, File)} correctly throws an exception if the target already contains a
     * file with a folder name.
     * 
     * @throws IOException expected.
     */
    @Test(expected = IOException.class)
    public void testCopyFolderTargetNameCollision() throws IOException {
        File src = new File("testdata/folderToCopy");
        File dst = new File("testdata/notEmptyDirectory");
        
        try {
            Util.copyFolder(src, dst);
            
        } finally {
            new File("testdata/notEmptyDirectory/a.txt").delete(); // this gets copied before the exception
        }
        
    }
    
    /**
     * Tests the formatBytes() method.
     */
    @Test
    public void testFormatBytes() {
        assertThat(Util.formatBytes(1), is("1 B"));
        assertThat(Util.formatBytes(132), is("132 B"));
        assertThat(Util.formatBytes(1023), is("1023 B"));
        assertThat(Util.formatBytes(1024), is("1 KiB"));
        assertThat(Util.formatBytes(34234), is("33.43 KiB"));
        assertThat(Util.formatBytes(32134341), is("30.64 MiB"));
        assertThat(Util.formatBytes(3213434341L), is("2.99 GiB"));
        assertThat(Util.formatBytes(32134343345341L), is("29.22 TiB"));
        assertThat(Util.formatBytes(1125899906842624L), is("1024 TiB"));
    }
    
    /**
     * Tests the determineOS() method.
     */
    @Test
    public void testDetermineOS() {
        String originalName = System.getProperty("os.name");
        String originalArch = System.getProperty("os.arch");
        try {
            
            String[][] input = {
                // can't spoof 32 bit Windows, because it uses System.getenv() in detection
                {"Windows 7", "amd64"},
                {"Windows XP", "amd64"},
                {"Windows 2003", "amd64"},
                {"Windows 2000", "amd64"},
                {"Windows 98", "amd64"},
                {"Windows NT", "amd64"},
                {"Windows Me", "amd64"},
                
                {"Linux", "amd64"},
                {"Linux", "i386"},
                {"Linux", "x86"},
                
                {"SunOS", "x86"},
                {"SunOS", "sparc"},
                {"FreeBSD", "i386"},
                
                {"Mac OS X", "i386"},
                {"Mac OS X", "ppc"},
            };
            
            Util.OSType[] expected = {
                OSType.WIN64,
                OSType.WIN64,
                OSType.WIN64,
                OSType.WIN64,
                OSType.WIN64,
                OSType.WIN64,
                OSType.WIN64,
                
                OSType.LINUX64,
                OSType.LINUX32,
                OSType.LINUX32,
                
                null,
                null, // TODO: check if this should be Linux instead
                null,
                
                OSType.MACOS64, // TODO: check if this should be 32 bit instead
                OSType.MACOS64,
            };

            for (int i = 0; i < input.length; i++) {
                System.setProperty("os.name", input[i][0]);
                System.setProperty("os.arch", input[i][1]);
                assertThat(Util.determineOS(), is(expected[i]));
            }
            
        } finally {
            // make sure to always set properties back to original value
            System.setProperty("os.name", originalName);
            System.setProperty("os.arch", originalArch);
        }
    }
    
    /**
     * Tests that {@link Util#isNestedInDirectory(File, File)} returns true if the same file is passed to it.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testIsNestedInDirectorySameFile() throws IOException {
        File dir = new File("some/dir");
        File file = new File("some/dir");
        
        // same object
        assertThat(Util.isNestedInDirectory(dir, dir), is(true));
        // different objects, same content
        assertThat(Util.isNestedInDirectory(dir, file), is(true));
        // different content, but same dir
        assertThat(Util.isNestedInDirectory(dir, new File("some/dir/sub/..")), is(true));
    }
    
    /**
     * Tests that {@link Util#isNestedInDirectory(File, File)} returns false is non-nested files are passed to it.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testIsNestedInDirectoryFalse() throws IOException {
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("other/dir/file")), is(false));
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/dir/..")), is(false));
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/")), is(false));
    }
    
    /**
     * Tests that {@link Util#isNestedInDirectory(File, File)} returns true for files that are directly nested in the
     * given directory.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testIsNestedInDirectoryDirectly() throws IOException {
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/dir/file")), is(true));
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/dir/other_file.txt")), is(true));
    }
    
    /**
     * Tests that {@link Util#isNestedInDirectory(File, File)} returns true for files that are nested in
     * sub-directories.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testIsNestedInDirectorySubDirectories() throws IOException {
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/dir/another/file")), is(true));
        assertThat(Util.isNestedInDirectory(new File("some/dir/"), new File("some/dir/a/b/c/other_file.txt")),
                is(true));
    }
    
}
