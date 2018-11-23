package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.IOException;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Implements {@link #writeObject(Object)}. Sub-classes don't have to worry about the annotation metadata, they
 * only have to implement {@link #writeRow(Object...)}.
 *
 * @author Adam
 */
public abstract class AbstractTableWriter implements ITableWriter {

    /**
     * The type of Object -> Columns translation to use.
     */
    private static enum Type {
        
        /**
         * Instances are marked with the {@link TableRow} annotation.
         */
        ANNOATION,
        
        /**
         * Instances implement the {@link ITableRow} interface.
         */
        INTERFACE,
        
        /**
         * Instances have no special markup; just use the {@link #toString()} method.
         */
        TOSTRING,
        
    }
    
    private TableRowMetadata metadata;
    
    private Type type;
    
    /**
     * <p>
     * Writes the header for the given table row metadata. This method is called, when it is determined that objects
     * using the {@link TableRow} annotation are passed to {@link #writeObject(Object)}. 
     * </p>
     * <p>
     * By default, this just calls {@link #writeHeader(Object...)} with {@link TableRowMetadata#getHeaders()}. Only
     * implementations that do something special (e.g. SQL tables with keys) will want to override this.
     * </p>
     * 
     * @param metadata The metadata that the header should be written for.
     * 
     * @throws IOException If writing the header fails.
     */
    @SuppressWarnings("null") // converting from @NonNull Object @NonNull [] to @Nullable Object ... creates a warning
    protected void writeAnnotationHeader(@NonNull TableRowMetadata metadata) throws IOException {
        writeHeader(metadata.getHeaders());
    }
    
    /**
     * <p>
     * Writes the content of the given object. This method is called for each object, when it is determined that
     * objects using the {@link TableRow} annotation are passed to {@link #writeObject(Object)}.
     * </p>
     * <p>
     * By default, this just calls {@link #writeRow(Object...)} with {@link TableRowMetadata#getContent(Object)}. Only
     * implementations that do something special (e.g. SQL tables with keys) will want to override this. 
     * </p>
     * 
     * @param metadata The metadata for the object that should be written.
     * @param object The object that should be written.
     * 
     * @throws IOException If writing the object fails.
     * @throws IllegalArgumentException If the metadata does not apply to the given object.
     */
    protected void writeAnnotationObject(@NonNull TableRowMetadata metadata, @NonNull Object object)
            throws IOException, IllegalArgumentException {
        
        if (!metadata.isSameClass(object)) {
            throw new IllegalArgumentException("Incompatible type of row passed to writeRow(): "
                    + object.getClass().getName());
        }
        try {
            writeRow(metadata.getContent(object));
        } catch (ReflectiveOperationException e) {
            throw new IOException("Can't read field values", e);
        }
    }
    
    @Override
    public void writeObject(@NonNull Object row) throws IOException, IllegalArgumentException {
        if (type == null) {
            if (TableRowMetadata.isTableRow(row.getClass())) {
                type = Type.ANNOATION;
                metadata = new TableRowMetadata(row.getClass());
                writeAnnotationHeader(metadata);
                
            } else if (row instanceof ITableRow) {
                type = Type.INTERFACE;
                writeHeader(((ITableRow) row).getHeader());
                
            } else {
                type = Type.TOSTRING;
            }
        }
        
        switch (type) {
        case ANNOATION:
            writeAnnotationObject(notNull(metadata), row);
            break;
            
        case INTERFACE:
            writeRow(((ITableRow) row).getContent());
            break;
            
        default:
            writeRow(new @Nullable Object[] {row.toString()});
            break;
        }
    }

}
