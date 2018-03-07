package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.util.AbstractHandlerRegistry;
import net.ssehub.kernel_haven.util.io.csv.CsvFileSet;

/**
 * Factory for creating {@link ITableCollection}s for single files based on filename suffix. Handlers can register
 * themselves via the {@link #registerHandler(String, Class)} method.
 *  
 * @author Adam
 */
public class TableCollectionFactory extends AbstractHandlerRegistry<String, ITableCollection> {

    /**
     * The singleton instance for this factory.
     */
    public static final TableCollectionFactory INSTANCE = new TableCollectionFactory();
    
    /**
     * Only used for singleton instance.
     */
    private TableCollectionFactory() {
        registerHandler("csv", CsvFileSet.class);
    }
    
    /**
     * Opens the given file with a matching handler. The handler is identified via the file suffix.
     * 
     * @param file The file to open.
     * @return An {@link ITableCollection} for the given file.
     * 
     * @throws IOException If no handler is available for the given file, or instantiating the handler fails.
     */
    public ITableCollection openFile(File file) throws IOException {
        int dotIndex = file.getName().lastIndexOf('.');
        
        if (dotIndex == -1 || dotIndex == file.getName().length() - 1) {
            throw new IOException("Filename \"" + file.getName() + "\" has no suffix");
        }
        
        String suffix = notNull(file.getName().substring(dotIndex + 1));
        Class<? extends ITableCollection> handlerClass = getHandler(suffix);
        
        if (handlerClass == null) {
            throw new IOException("No handler for suffix " + suffix);
        }
        
        try {
            return  handlerClass.getConstructor(File.class).newInstance(file);
            
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IOException("Can't instantiate handler class", e);
        }
    }
    
}
