package net.ssehub.kernel_haven.util.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.io.csv.CsvFileSet;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Static utility methods for opening {@link ITableCollection}s based on file suffix. Handlers can register themselves
 * via the {@link #registerHandler(String, Class)} method.
 *  
 * @author Adam
 */
public class TableCollectionUtils {

    private static final Map<String, Class<? extends ITableCollection>> HANDLERS;
    
    static {
        HANDLERS = new HashMap<>();
        HANDLERS.put("csv", CsvFileSet.class);
        
        // invoke static blocks for known ITableCollections
        // TODO: refactor this properly
        try {
            Class.forName("net.ssehub.kernel_haven.io.excel.ExcelBook");
        } catch (ClassNotFoundException e) {
            // ignore
        }
    }

    /**
     * Don't allow any instances.
     */
    private TableCollectionUtils() {
    }
    
    /**
     * Registers a handler for a given file suffix. The handler class must have a constructor that takes a single
     * {@link File} argument. If a handler with the same suffix already exists, this new one replaces it.
     * 
     * @param suffix The file suffix, without the "."; e.g. "csv" or "xlsx".
     * @param handler The handler class.
     */
    public static void registerHandler(@NonNull String suffix, @NonNull Class<? extends ITableCollection> handler) {
        HANDLERS.put(suffix, handler);
    }
    
    /**
     * Opens the given file with a matching handler. The handler is identified via the file suffix.
     * 
     * @param file The file to open.
     * @return An {@link ITableCollection} for the given file.
     * 
     * @throws IOException If no handler is available for the given file, or instantiating the handler fails.
     */
    public static ITableCollection openFile(File file) throws IOException {
        int dotIndex = file.getName().lastIndexOf('.');
        
        if (dotIndex == -1 || dotIndex == file.getName().length() - 1) {
            throw new IOException("Filename \"" + file.getName() + "\" has no suffix");
        }
        
        String suffix = file.getName().substring(dotIndex + 1);
        Class<? extends ITableCollection> handlerClass = HANDLERS.get(suffix);
        
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
