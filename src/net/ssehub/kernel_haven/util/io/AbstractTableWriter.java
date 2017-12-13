package net.ssehub.kernel_haven.util.io;

import java.io.IOException;

/**
 * Implements {@link #writeRow(Object)}. Sub-classes don't have to worry about the annotation metadata, they only have
 * to implement {@link #writeRow(String...)}.
 *
 * @author Adam
 */
public abstract class AbstractTableWriter implements ITableWriter {

    private TableRowMetadata metadata;
    
    private boolean initialized;
    
    @Override
    public void writeRow(Object row) throws IOException, IllegalArgumentException {
        if (!initialized) {
            initialized = true;
            if (TableRowMetadata.isTableRow(row.getClass())) {
                metadata = new TableRowMetadata(row.getClass());
                writeHeader(metadata.getHeaders());
            }
        }
        
        if (metadata != null) {
            if (!metadata.isSameClass(row)) {
                throw new IllegalArgumentException("Incompatible type of row passed to writeRow(): "
                        + row.getClass().getName());
            }
            
            try {
                writeRow(metadata.getContent(row));
            } catch (ReflectiveOperationException e) {
                throw new IOException("Can't read field values", e);
            }
            
        } else {
            writeRow(new String[] {row.toString()});
        }
    }

}
