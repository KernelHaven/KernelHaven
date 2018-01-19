package net.ssehub.kernel_haven.util.io;

import java.io.IOException;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

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
    
    @Override
    public void writeObject(@NonNull Object row) throws IOException, IllegalArgumentException {
        if (type == null) {
            if (TableRowMetadata.isTableRow(row.getClass())) {
                type = Type.ANNOATION;
                metadata = new TableRowMetadata(row.getClass());
                writeHeader(metadata.getHeaders());
                
            } else if (row instanceof ITableRow) {
                type = Type.INTERFACE;
                writeHeader(((ITableRow) row).getHeader());
                
            } else {
                type = Type.TOSTRING;
            }
        }
        
        switch (type) {
        case ANNOATION:
            if (!metadata.isSameClass(row)) {
                throw new IllegalArgumentException("Incompatible type of row passed to writeRow(): "
                        + row.getClass().getName());
            }
            try {
                writeRow(metadata.getContent(row));
            } catch (ReflectiveOperationException e) {
                throw new IOException("Can't read field values", e);
            }
            break;
            
        case INTERFACE:
            writeRow(((ITableRow) row).getContent());
            break;
            
        default:
            writeRow(new Object[] {row.toString()});
            break;
        }
    }

}
