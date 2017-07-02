package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;

/**
 * The provider for the code model. This class serves as an intermediate between the analysis and the code model
 * extractor.
 *
 * @author Adam
 */
public class CodeModelProvider extends AbstractProvider<SourceFile, CodeExtractorConfiguration> {

    @Override
    protected long getTimeout() {
        return config.getProviderTimeout();
    }
    
    @Override
    protected List<File> getTargets() throws SetUpException {
        List<File> result = new LinkedList<>();
        
        Pattern pattern = config.getFilenamePattern();

        for (File relativeFile : config.getFiles()) {
            File absoluteFile = new File(config.getSourceTree(), relativeFile.getPath());

            if (absoluteFile.isFile()) {
                result.add(relativeFile);
            } else if (absoluteFile.isDirectory()) {
                readFilesFromDirectory(absoluteFile, pattern, result);
            } else {
                throw new SetUpException("Non-existing file specified in code.extractor.files: "
                        + relativeFile.getPath());
            }
        }
        
        return result;
    }
    
    /**
     * Finds all files in the given directory (recursively) that match the given
     * pattern. The files that match are added to filesToParse.
     * 
     * @param directory
     *            The directory to search in.
     * @param pattern
     *            The pattern to check against.
     * @param result
     *            The list to add the found files to.
     */
    private void readFilesFromDirectory(File directory, Pattern pattern, List<File> result) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                readFilesFromDirectory(file, pattern, result);
            } else {
                Matcher m = pattern.matcher(file.getName());

                if (m.matches()) {
                    result.add(config.getSourceTree().toPath().relativize(file.toPath()).toFile());
                }

            }
        }

    }

    @Override
    protected AbstractCache<SourceFile> createCache() {
        return new CodeModelCache(config.getCacheDir());
    }

    @Override
    public boolean readCache() {
        return config.isCacheRead();
    }

    @Override
    public boolean writeCache() {
        return config.isCacheWrite();
    }

    @Override
    public int getNumberOfThreads() {
        return config.getThreads();
    }

}
