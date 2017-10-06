package net.ssehub.kernel_haven.util.io.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import net.ssehub.kernel_haven.util.io.ITableWriter;
import net.ssehub.kernel_haven.util.io.TableElement;
import net.ssehub.kernel_haven.util.io.TableRow;

/**
 * A writer for writing tables as CSV files.
 *
 * @author Adam
 */
public class CsvWriter implements ITableWriter {
    
    public static final char DEFAULT_SEPARATOR = ';';

    private OutputStream out;
    
    private char separator;
    
    private Class<?> rowClass;
    
    private Field[] fields;

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
     * Escapes the given field content. Adds " around, the while field, if the separator or newline char is used inside
     * of it. In this case, also escapes any " characters with an additional ".
     * 
     * @param field The field value to escape.
     * @return The espaced field value.
     */
    private String escape(String field) {
        boolean mustBeEscaped = false;
        for (char c : field.toCharArray()) {
            if (c == separator || c == '\n') {
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
        // remove trailing separator
        line.delete(line.length() - 1, line.length());
        line.append('\n');
        
        out.write(line.toString().getBytes(Charset.forName("UTF-8")));
    }
    
    /**
     * Writes the header line, based on the annotations found in the given instance of a row. This also creates the
     * {@link #fields} array.
     * 
     * @param row The row to find the header names in.
     * 
     * @throws IOException If writing the line fails.
     */
    private void writeHeader(Object row) throws IOException {
        Map<Integer, Field> fields = new TreeMap<>();
        
        Class<?> clazz = row.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            TableElement annotation = field.getAnnotation(TableElement.class);
            if (annotation != null) {
                field.setAccessible(true);
                fields.put(annotation.index(), field);
            }
        }

        this.fields = new Field[fields.size()];
        String[] headers = new String[fields.size()];
        int index = 0;
        for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
            // we silently ignore if index != entry.getKey(); the order is correct, anways 
            this.fields[index] = entry.getValue();
            headers[index] = entry.getValue().getAnnotation(TableElement.class).name();
            index++;
        }
        writeLine(headers);
    }
    
    /**
     * Writes a single row, based on the annotations found in the object.
     * 
     * @param row The row to write.
     * 
     * @throws IOException If writing the row fails.
     */
    private void writeAnnotatedRow(Object row) throws IOException {
        String[] values = new String[fields.length];
        
        try {
            int index = 0;
            for (Field field : fields) {
                values[index++] = field.get(row).toString();
            }
            
        } catch (ReflectiveOperationException e) {
            // shouldn't happen
            throw new IOException("Can't access field value", e);
        }
        
        writeLine(values);
    }

    @Override
    public void writeRow(Object row) throws IOException {
        Class<?> rowClass = row.getClass();
        if (this.rowClass == null) {
            this.rowClass = rowClass;
        }
        
        if (this.rowClass != rowClass) {
            throw new IllegalArgumentException("Different types of row passed to writeRow(): "
                    + this.rowClass.getName() + " and " + rowClass.getName());
        }
        
        TableRow annotation = rowClass.getAnnotation(TableRow.class);
        if (annotation != null) {
            if (fields == null) {
                writeHeader(row);
            }
            writeAnnotatedRow(row);
            
        } else {
            writeLine(row.toString());
        }
    }

}
