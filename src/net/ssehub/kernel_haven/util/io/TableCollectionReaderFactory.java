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
package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ssehub.kernel_haven.util.AbstractHandlerRegistry;
import net.ssehub.kernel_haven.util.io.csv.CsvArchive;
import net.ssehub.kernel_haven.util.io.csv.CsvFileSet;
import net.ssehub.kernel_haven.util.io.void_io.VoidTableCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Factory for creating {@link ITableCollection}s for single files based on filename suffix. The resulting
 * {@link ITableCollection} should only be used for reading. 
 * <p>
 * Handlers can register themselves via the
 * {@link AbstractHandlerRegistry#registerHandler(Object, Class) registerHandler} method. All handler classes must
 * have a constructor that takes a single {@link File} argument.
 * <p>
 * By default, {@link CsvFileSet} is registered as the handler for .csv files.
 *  
 * @author Adam
 */
public class TableCollectionReaderFactory extends AbstractHandlerRegistry<String, ITableCollection> {

    /**
     * The singleton instance for this factory.
     */
    public static final @NonNull TableCollectionReaderFactory INSTANCE = new TableCollectionReaderFactory();
    
    /**
     * Only used for singleton instance.
     */
    private TableCollectionReaderFactory() {
        registerHandler("csv", CsvFileSet.class);
        registerHandler("csv.zip", CsvArchive.class);
        registerHandler("void", VoidTableCollection.class);
    }
    
    /**
     * Opens the given file with a matching handler. The handler is identified via the file suffix.
     * 
     * @param file The file to open.
     * @return An {@link ITableCollection} for the given file.
     * 
     * @throws IOException If no handler is available for the given file, or instantiating the handler fails.
     */
    public @NonNull ITableCollection openFile(@NonNull File file) throws IOException {
        List<@NonNull String> suffixes = getSuffix(file);
        
        // Tries to find the most specific (reverse order) handler and returns null if no one can be found
        Class<? extends ITableCollection> handlerClass = suffixes.stream()
            .sorted(Collections.reverseOrder())
            .map(s -> getHandler(s))
            .findFirst()
            .orElse(null);
        
        if (handlerClass == null) {
            throw new IOException("No handler for suffix " + suffixes.get(0));
        }
        
        try {
            return  notNull(handlerClass.getConstructor(File.class).newInstance(file));
            
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IOException("Can't instantiate handler class", e);
        }
    }
    
    /**
     * Returns the (possible) suffixes of the given file. The first element in the result is always the suffix
     * after the last dot. If applicable, the second element contains the suffix after the second-to-last dot.
     * 
     * @param file The file to get the suffix for.
     * @return A list of possible suffixes. Contains at least one item.
     * 
     * @throws IOException If the file has no suffix.
     */
    public static @NonNull List<@NonNull String> getSuffix(@NonNull File file) throws IOException {
        List<@NonNull String> result = new ArrayList<>(2);
        
        int dotIndex = file.getName().lastIndexOf('.');
        
        if (dotIndex == -1 || dotIndex == file.getName().length() - 1) {
            throw new IOException("Filename \"" + file.getName() + "\" has no suffix");
        }
        
        result.add(notNull(file.getName().substring(dotIndex + 1)));
        
        // use the second to last dot for possible suffix, too
        dotIndex = file.getName().substring(0, dotIndex).lastIndexOf('.');
        if (dotIndex != -1) {
            result.add(notNull(file.getName().substring(dotIndex + 1)));
        }
        
        return result;
    }
    
}
