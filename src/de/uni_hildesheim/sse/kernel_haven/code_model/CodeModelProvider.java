package de.uni_hildesheim.sse.kernel_haven.code_model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_hildesheim.sse.kernel_haven.SetUpException;
import de.uni_hildesheim.sse.kernel_haven.config.CodeExtractorConfiguration;
import de.uni_hildesheim.sse.kernel_haven.util.BlockingQueue;
import de.uni_hildesheim.sse.kernel_haven.util.CodeExtractorException;
import de.uni_hildesheim.sse.kernel_haven.util.ExtractorException;
import de.uni_hildesheim.sse.kernel_haven.util.FormatException;
import de.uni_hildesheim.sse.kernel_haven.util.Logger;

/**
 * Provider for the code model. This gives the analysis an interface to start
 * and query the results of the {@link ICodeModelExtractor}s. It also manages
 * proper synchronization between the analysis thread and the extractor thread.
 * 
 * @author Adam
 * @author Alice
 */
public class CodeModelProvider {

    private static final Logger LOGGER = Logger.get();

    /**
     * The CodeModel extractor.
     */
    private ICodeModelExtractor extractor;
    
    private ICodeExtractorFactory factory;

    private CodeExtractorConfiguration config;
    
    private ExtractorException exception = null;
    
    private BlockingQueue<SourceFile> resultQueue;
    
    private BlockingQueue<CodeExtractorException> exceptionQueue;

    private Set<File> filesToParse;
    
    /**
     * The cache to write and read into. Not <code>null</code> if caching is enabled (readCache || writeCache).
     */
    private CodeModelCache cache;
    
    /**
     * Creates a new code model provider.
     */
    public CodeModelProvider() {
        this.resultQueue = new BlockingQueue<>();
        this.exceptionQueue = new BlockingQueue<>();
    }
    
    /**
     * Finds all files to parse specified in the properties.
     * 
     * @throws ExtractorException
     *             If the files cannot be found.
     */
    private void readFilesToParse() throws ExtractorException {
        filesToParse = new HashSet<>();

        Pattern pattern = config.getFilenamePattern();

        for (File relativeFile : config.getFiles()) {
            File absoluteFile = new File(config.getSourceTree(), relativeFile.getPath());

            if (absoluteFile.isFile()) {
                filesToParse.add(relativeFile);
            } else if (absoluteFile.isDirectory()) {
                readFilesFromDirectory(absoluteFile, pattern);
            } else {
                throw new ExtractorException("Non-existing file specified in code.extractor.files: "
                        + relativeFile.getPath());
            }
        }

    }

    /**
     * Retrieves the filesToParse set. Package visibility for test cases.
     * 
     * @return The files to parse.
     */
    Set<File> getFilesToParse() {
        return filesToParse;
    }

    /**
     * Finds all files in the given directory (recursively) that match the given
     * pattern. The files that match are added to filesToParse.
     * 
     * @param directory
     *            The directory to search in.
     * @param pattern
     *            The pattern to check against.
     */
    private void readFilesFromDirectory(File directory, Pattern pattern) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                readFilesFromDirectory(file, pattern);
            } else {
                Matcher m = pattern.matcher(file.getName());

                if (m.matches()) {
                    filesToParse.add(config.getSourceTree().toPath().relativize(file.toPath()).toFile());
                }

            }
        }

    }


    /**
     * Tells this provider which extractor factory to use. This has to be called before
     * start() or get() is used.
     * 
     * @param factory
     *            The factory to create extractors for the code model.
     *            Can be null if we should only read from the cache.
     */
    public void setFactory(ICodeExtractorFactory factory) {
        this.factory = factory;
    }

    /**
     * Starts the extractor.
     * 
     * @param filesToParse Queue containing the files relative to source code tree that should
     *      be parsed. 
     */
    private void startExtractor(BlockingQueue<File> filesToParse) {
        LOGGER.logInfo("Starting code extractor...");
        
        if (extractor == null) {
            this.exception = new ExtractorException("Code extractor required but not specified.");
        } else {
            extractor.start(filesToParse);
        }
    }
    
    /**
     * Starts the extraction process.
     * 
     * @param config The configuration for this execution. Do not change this after calling this method.
     *      Must not be <code>null</code>.
     */
    public void start(CodeExtractorConfiguration config) {
        this.config = config;
        
        try {
            if (factory != null) {
                this.extractor = factory.create(config);
                this.extractor.setProvider(this);
            }
        
            readFilesToParse();
        } catch (ExtractorException | SetUpException e) {
            this.exception = new ExtractorException(e);
            return;
        }
        
        if (config.isCacheRead() || config.isCacheWrite()) {
            cache = new CodeModelCache(config.getCacheDir());
        }
        
        LOGGER.logInfo("Code extraction started on " + this.filesToParse.size() + " files");
        
        BlockingQueue<File> filesToParse = new BlockingQueue<>();
        
        startExtractor(filesToParse);
        
        if (config.isCacheRead()) {
            LOGGER.logDebug("Starting to read code model cache");
            
            new Thread(() -> {
                Thread.currentThread().setName("CmCache");
                
                for (File file : this.filesToParse) {
                    boolean success = false;
                    try {
                        SourceFile result = cache.read(file);
                        if (result != null) {
                            LOGGER.logDebug("Read " + file.getPath() + " from cache");
                            addResult(result);
                            success = true;
                        }
                        
                    } catch (FormatException | IOException e) {
                        LOGGER.logException("Invalid cache for file " + file.getPath(), e);
                    }
                    
                    if (!success) {
                        filesToParse.add(file);
                    }
                }
                
                filesToParse.end();
                
            }).start();
            
        } else {
            for (File file : this.filesToParse) {
                filesToParse.add(file);
            }
            filesToParse.end();
        }
        
    }
    
    /**
     * Returns the next result in the result queue. If the result is not ready yet, then this method
     * waits until the extractor is finished.
     * 
     * @return The next result from the queue. <code>null</code> is returned, to signal that the
     *      extractor is finished an no more results are coming after this.
     *      
     * @throws ExtractorException If the extractor execution fails. In contrast to the exception
     *      queue that can be queried via getNextException(), this exception is thrown if the
     *      extractor can't even start to process files, or if the timeout for the extractor has exceeded.
     */ 
    public SourceFile getNext() throws ExtractorException {
        if (exception != null) {
            throw exception;
        }
        
        try {
            return resultQueue.get(config.getProviderTimeout());
        } catch (TimeoutException e) {
            if (extractor != null) {
                extractor.stop();
            }
            
            throw new ExtractorException("Did not get a result in time (timeout = "
                    + config.getProviderTimeout() + ")");
        }
    }
    
    /**
     * Returns the next exception in the exception queue. The exception queue contains all exceptions
     * that are added by the extractor. It is usually queried after getNext() returns null.
     * If the exception is not ready yet, then this method waits until the extractor add one.
     * 
     * @return The next exception from the queue. <code>null</code> is returned, to signal that the
     *      extractor is finished an no more exceptions are coming after this.
     */ 
    public CodeExtractorException getNextException() {
        return exceptionQueue.get();
    }

    /**
     * Tells the provider that an exception was encountered while parsing a file. The exceptions
     * added here can be queried later via getNextException().
     * 
     * @param exception
     *            The exception to add to the exception queue. Not null.
     */
    public void addException(CodeExtractorException exception) {
        exceptionQueue.add(exception);
    }

    /**
     * Adds a result to the result queue, to be returned by getNext(). This method
     * should be called for each result. After the extractor is finished with all files,
     * it should call addResult(null).
     * 
     * @param result The result to add to the result queue. Use <code>null</code> here to signal
     *      that no more results are coming after this.
     */
    public void addResult(SourceFile result) {
        if (result != null) {
            resultQueue.add(result);
            
            if (config.isCacheWrite()) {
                try {
                    LOGGER.logDebug("Writing CM cache...");
                    cache.write(result);
                } catch (IOException e) {
                    LOGGER.logException("Exception while writing cache", e);
                }
            }
        } else {
            resultQueue.end();
            exceptionQueue.end();
        }
    }

}
