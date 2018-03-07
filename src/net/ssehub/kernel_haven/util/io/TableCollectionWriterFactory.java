package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.util.AbstractHandlerRegistry;
import net.ssehub.kernel_haven.util.io.csv.CsvFileCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * Factory for creating {@link ITableCollection}s for writing tables with a common base name. For excel, the base name
 * is used as a workbook; for CSV, multiple files are created with the given base name.
 * </p>
 * <p>
 * Handlers can register themselves via the {@link #registerHandler(String, Class)} method. All handler classes must
 * have a constructor that takes a single {@link File} argument. This argument is the base name that should be used for
 * all tables created by it.
 * </p>
 * <p>
 * By default, {@link CsvFileCollection} is registered as the handler for .csv files.
 * </p>
 *  
 * @author Adam
 */
public class TableCollectionWriterFactory extends AbstractHandlerRegistry<String, ITableCollection> {

    /**
     * The singleton instance for this factory.
     */
    public static final @NonNull TableCollectionWriterFactory INSTANCE = new TableCollectionWriterFactory();
    
    /**
     * Only used for singleton instance.
     */
    private TableCollectionWriterFactory() {
        registerHandler("csv", CsvFileCollection.class);
    }
    
    /**
     * Creates an {@link ITableCollection} with the given base name. The handler is selected based on the file suffix.
     * 
     * @param baseName The base name of the collection to create.
     * @return An {@link ITableCollection} to write tables with the given base name.
     * 
     * @throws IOException If no handler is registered for the given suffix or if creating the collection fails.
     */
    public @NonNull ITableCollection createCollection(@NonNull File baseName) throws IOException {
        String suffix = TableCollectionReaderFactory.getSuffix(baseName);
        
        Class<? extends ITableCollection> handlerClass = getHandler(suffix);
        
        if (handlerClass == null) {
            throw new IOException("No handler for suffix " + suffix);
        }
        
        try {
            return  notNull(handlerClass.getConstructor(File.class).newInstance(baseName));
            
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IOException("Can't instantiate handler class", e);
        }
    }
    
    
}
