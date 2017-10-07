package net.ssehub.kernel_haven.util.io.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.io.TableRowMetadata;

/**
 * A writer for writing tables as CSV files.
 *
 * @author Adam
 */
public class CsvWriter implements ITableWriter {
    
    public static final char DEFAULT_SEPARATOR = ';';

    private OutputStream out;
    
    private char separator;
    
    private TableRowMetadata metadata;
    
    private boolean initialized;
    
    /**
     * Creates a {@link CsvWriter} for the given output stream. Uses the {@link #DEFAULT_SEPARATOR}.
     * 
     * @param out The output stream to write the CSV to. This object will close this stream once it is closed.
     */
    public CsvWriter(OutputStream out) {
        this(out, DEFAULT_SEPARATOR);
    }
    
    /**
     * Creates a {@link CsvWriter} for the given output stream.
     * 
     * @param out The output stream to write the CSV to. This object will close this stream once it is closed.
     * @param separator The separator character to use.
     */
    public CsvWriter(OutputStream out, char separator) {
        this.out = out;
        this.separator = separator;
    }
    
    @Override
    public void close() throws IOException {
        out.close();
    }
    
    /**
     * Escapes the given field content. Adds " around, the while field, if a char that needs to be escaped is used
     * inside of it. In this case, also escapes any " characters with an additional ".
     * <br />
     * Chars that need to be escaped are:
     * <ul>
     *      <li>\n</li>
     *      <li>"</li>
     *      <li>separator</li>
     * </ul>
     * 
     * @param field The field value to escape.
     * @return The escaped field value.
     */
    private String escape(String field) {
        boolean mustBeEscaped = false;
        for (char c : field.toCharArray()) {
            if (c == separator || c == '\n'  || c == '"') {
                mustBeEscaped = true;
                break;
            }
        }
        
        if (mustBeEscaped) {
            StringBuilder str = new StringBuilder(field);
            // escape any " with an additional "
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '"') {
                    str.insert(i, '"');
                    i++; // don't visit the escaped char again
                }
            }
            str.insert(0, '"').append('"');
            field = str.toString();
        }
        return field;
    }
    
    /**
     * Writes a single line with the given fields. The fields will be separated by {@link #separator}. A \n will be
     * added at the end of the line.
     * 
     * @param fields The fields to write. Will be escaped.
     * 
     * @throws IOException If writing to the output stream fails.
     */
    private void writeLine(String... fields) throws IOException {
        StringBuffer line = new StringBuffer();
        for (String field : fields) {
            line.append(escape(field)).append(separator);
        }
        if (line.length() > 0) {
            // remove trailing separator
            line.delete(line.length() - 1, line.length());
        }
        line.append('\n');
        
        out.write(line.toString().getBytes(Charset.forName("UTF-8")));
    }
    
    @Override
    public void writeRow(Object row) throws IOException {
        if (!initialized) {
            initialized = true;
            if (TableRowMetadata.isTableRow(row.getClass())) {
                metadata = new TableRowMetadata(row.getClass());
                writeLine(metadata.getHeaders());
            }
        }
        
        if (metadata != null) {
            if (!metadata.isSameClass(row)) {
                throw new IllegalArgumentException("Incompatible type of row passed to writeRow(): "
                        + row.getClass().getName());
            }
            
            try {
                writeLine(metadata.getContent(row));
            } catch (ReflectiveOperationException e) {
                throw new IOException("Can't read field values", e);
            }
            
        } else {
            writeLine(row.toString());
        }
    }
    
    @Override
    public void writeRow(String... fields) throws IOException {
        initialized = true;
        writeLine(fields);
    }

}
