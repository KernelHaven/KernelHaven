/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * The provider for the code model. This class serves as an intermediate between the analysis and the code model
 * extractor.
 *
 * @author Adam
 */
public class CodeModelProvider extends AbstractProvider<SourceFile<?>> {

    @Override
    protected long getTimeout() {
        return config.getValue(DefaultSettings.CODE_PROVIDER_TIMEOUT);
    }
    
    @Override
    protected @NonNull List<@NonNull File> getTargets() throws SetUpException {
        List<@NonNull File> result = new LinkedList<>();
        
        Pattern pattern = config.getValue(DefaultSettings.CODE_EXTRACTOR_FILE_REGEX);

        for (String relativeStr : config.getValue(DefaultSettings.CODE_EXTRACTOR_FILES)) {
            File relativeFile = new File(relativeStr);
            File absoluteFile = new File(config.getValue(DefaultSettings.SOURCE_TREE), relativeFile.getPath());

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
                    result.add(config.getValue(DefaultSettings.SOURCE_TREE).toPath()
                            .relativize(file.toPath()).toFile());
                }

            }
        }

    }

    @Override
    protected @NonNull AbstractCache<SourceFile<?>> createCache() {
        return new JsonCodeModelCache(config.getValue(DefaultSettings.CACHE_DIR),
                config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_COMPRESS));
    }

    @Override
    public boolean readCache() {
        return config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_READ);
    }

    @Override
    public boolean writeCache() {
        return config.getValue(DefaultSettings.CODE_PROVIDER_CACHE_WRITE);
    }

    @Override
    public int getNumberOfThreads() {
        return config.getValue(DefaultSettings.CODE_EXTRACTOR_THREADS);
    }

}
