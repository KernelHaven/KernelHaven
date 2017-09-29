package net.ssehub.kernel_haven.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Executes command line commands in a separate thread and returns their (string) output.
 * @author El-Sharkawy
 *
 */
public class CommandExecutor {
    
    /**
     * Stores the result of an executed command. This comprises:
     * <ul>
     * <li>The standard output.</li>
     * <li>The error output.</li>
     * <li>The status code.</li>
     * </ul>
     * @author El-Sharkawy
     *
     */
    public static class ExecutionResult {
        private String output;
        private String errMsg;
        private int returnCode;
        
        /**
         * The default output of the executed command.
         * @return The standard output or <tt>null</tt> if there wasn't any.
         */
        public String getOutput() {
            return output;
        }
        
        /**
         * The error output of the executed command.
         * @return The error output or <tt>null</tt> if there wasn't any.
         */
        public String getError() {
            return errMsg;
        }
        
        /**
         * Returns the exist/return code of the executed command.
         * @return the exit value of the subprocess represented by this
         *         {@code Process} object.  By convention, the value
         *         {@code 0} indicates normal termination.
         */
        public int getCode() {
            return returnCode;
        }
    }
    
    private Process process;
    private BufferedReader inStream;
    private BufferedReader errStream;
    private ExecutionResult result;

    /**
     * Executes the given command.
     * @param folder Where to execute the command, this must be a folder and exist!
     * @param commands The command to be executed on a command line, must not be <tt>null</tt>, all parameters must be
     *          separated as separate elements.
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public CommandExecutor(File folder, String... commands) throws IOException {
        Logger.get().logDebug(folder.getAbsolutePath() + File.pathSeparatorChar + Arrays.toString(commands));
        
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(folder);   
        process = builder.start();
        inStream = new BufferedReader( new InputStreamReader(process.getInputStream()));
        errStream = new BufferedReader( new InputStreamReader(process.getErrorStream()));
        result = new ExecutionResult();
    }
    
    /**
     * Input stream where successful messages will be printed.
     * @return Input stream of successful messages.
     */
    private BufferedReader getInStream() {
        return inStream;
    }

    /**
     * Input stream where error messages will be printed.
     * @return Input stream of error messages.
     */
    private BufferedReader getErrStream() {
        return errStream;
    }
    
    /**
     * Reads the output of the executed command.
     * @throws IOException If an IO error occurs during reading.
     */
    private void read() throws IOException {
        StringBuffer msg = new StringBuffer();
        String line = null;
        
        // Collect all normal messages, before collecting all errors.
        while ((line = getInStream().readLine()) != null) {
            msg.append(line);
            msg.append("\n");
        }
        result.output = msg.toString();
        
        // Collect and return all errors
        msg = new StringBuffer();
        while ((line = getErrStream().readLine()) != null) {
            msg.append(line);
            msg.append("\n");
        }
        String tmpResult = msg.toString().trim();
        result.errMsg = tmpResult.isEmpty() ? null : tmpResult;
    }
    
    /**
     * Just waits for the end of the process.
     * This method is a blocking method, i.e., it will wait until the process has finished its work.
     * 
     * @return The read output as well as the return code of the executed command.
     * @throws InterruptedException in case that the process is interrupted
     * @throws IOException If an IO error occurs during reading.
     */
    public ExecutionResult execute() throws InterruptedException, IOException {
        result.returnCode = process.waitFor();
        read();
        
        return result;
    }
}
