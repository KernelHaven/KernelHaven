package net.ssehub.kernel_haven.util.io.csv;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.io.ITableReader;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A reader for reading CSV files. This reader expects fields to be escaped as defined in
 * <a href="https://tools.ietf.org/html/rfc4180">RFC4180</a>. The behavior for malformed escapes is based on
 * the behavior observed in LibreOffice Calc. Examples of malformed escapes and how they are handled:
 * <ul>
 *      <li>A single " appearing in the middle of an unescaped field: The single " is interpreted as a normal content
 *          character</li>
 *      <li>A double "" appearing in the middle of an unescaped field: The double "" are interpreted as normal content
 *          characters.</li>
 *      <li>A " appearing at the end of an unescaped field: The " is interpreted as a normal content character.</li>
 *      <li>A " appearing at the start of a field, but not at the end: The field is not considered to not have ended.
 *          All characters, until a " at the end position of a field is encountered, are intepreted as normal content
 *          characters (including all line breaks, separator characters and "; double "" are still escaped to a single
 *          ")-. For example <code>value 1;"value 2; value 3</code> has 2 fields: "value 1" and "value 2; value3"</li>
 * </ul>
 * This reader violates RFC4180 in that it considers any of the following sequences to be line-breaks: \r, \n, \r\n.
 * Additionally, this reader assumes that the default field separator is a semicolon (;) character.
 *
 * @author Adam
 */
public class CsvReader implements ITableReader {

    private @NonNull Reader in;
    
    private char separator;
    
    private Integer peeked;
    
    private boolean isEnd;
    
    private int currentLineNumber;
    
    /**
     * Creates a new {@link CsvReader} for the given input stream. Uses {@link CsvWriter#DEFAULT_SEPARATOR}.
     * 
     * @param in The input stream to read the CSV data from.
     */
    public CsvReader(@NonNull InputStream in) {
        this(in, CsvWriter.DEFAULT_SEPARATOR);
    }
    
    /**
     * Creates a new {@link CsvReader} for the given input stream.
     * 
     * @param in The input stream to read the CSV data from.
     * @param separator The separator character to use.
     */
    public CsvReader(@NonNull InputStream in, char separator) {
        this (new InputStreamReader(in, Charset.forName("UTF-8")), separator);
    }
    
    /**
     * Creates a new {@link CsvReader} for the given reader. Uses {@link CsvWriter#DEFAULT_SEPARATOR}.
     * 
     * @param in The reader to read the CSV data from.
     */
    public CsvReader(@NonNull Reader in) {
        this(in, CsvWriter.DEFAULT_SEPARATOR);
    }
    
    /**
     * Creates a new {@link CsvReader} for the given reader.
     * 
     * @param in The reader to read the CSV data from.
     * @param separator The separator character to use.
     */
    public CsvReader(@NonNull Reader in, char separator) {
        this.in = in;
        this.separator = separator;
        
    }
    
    @Override
    public void close() throws IOException {
        in.close();
    }
    
    /**
     * Reads the next element from the stream. This method must be used, so that {@link #peek()} correctly functions.
     * If the end of stream is detected, this method sets {@link #isEnd} to true.
     * 
     * @return The read character. -1 if end of stream is reached.
     * 
     * @throws IOException If reading the stream fails.
     */
    private int read() throws IOException {
        int result;
        if (peeked != null) {
            result = peeked;
            peeked = null;
        } else {
            result = in.read();
        }
        if (result == -1) {
            isEnd = true;
        }
        return result;
    }
    
    /**
     * Peeks at the next character. The next {@link #read()} call will return the same character. {@link #read()} must
     * be called at least once before calling {@link #peek()} again.
     * 
     * @return The next character that will be read. -1 if next character will be end of stream.
     * 
     * @throws IOException If reading the stream fails.
     * @throws IllegalStateException If {@link #peek()} is called twice, without {@link #read()} in between.
     */
    private int peek() throws IOException {
        if (peeked != null) {
            throw new IllegalStateException("Cannot peek while already storing peeked character");
        }
        peeked = in.read();
        return peeked;
    }
    
    /**
     * Removes the escaping " from a given field. The same string is returned, if field is not escaped. Double ""
     * are escaped to only one ".
     * 
     * @param field The field to un-escape.
     * @return The un-escaped field.
     */
    private @NonNull String unescape(@NonNull String field) {
        StringBuilder escaped = new StringBuilder();
        
        if (field.isEmpty() || field.charAt(0) != '"') {
            escaped.append(field);
        } else {
            for (int i = 1; i < field.length(); i++) {
                char c = field.charAt(i);
                
                if (c == '"' && i == field.length() - 1) {
                    // trailing " means escaped sequence ended
                    break;
                } else if (c == '"' && field.charAt(i + 1) == '"') {
                    // double "" mean insert one "
                    // move i to next character, so that only one " is added
                    i++;
                }
                
                escaped.append(c);
            }
        }
        
        return notNull(escaped.toString());
    }
    
    /**
     * Reads and parses a single line of CSV data. Splits at separator character. Considers (and un-escapes)
     * escaped values.
     * 
     * @return The fields found in the CSV.
     * 
     * @throws IOException If reading the stream fails.
     */
    // CHECKSTYLE:OFF // TODO: this method is too long.
    private @NonNull String @Nullable [] readLine() throws IOException {
    // CHECKSTYLE:ON
        List<@NonNull String> parts = new LinkedList<>();
        
        // whether we are currently inside an escape sequence
        // an escaped sequence starts with a " and ends with a "
        // the start " must be the first character of the field
        // the end " must be the last character of a field
        boolean inEscaped = false;
        
        // only relevant if inEscaped = true
        // whether the last read character was a "
        // used to detect the edge case that an escaped " is in front of a delimiter (e.g. "";  )
        boolean wasQuote = false;
        
        // whether the last character was \r
        // only used to detect \r\n in escaped text
        boolean wasCarriageReturn = false;
        
        // contains characters of the current field
        // new characters are added until a (unescaped) separator is found
        // when a (unescaped) separator is found, the contents of this contain the previous field
        StringBuilder currentElement = new StringBuilder();
        
        // break; will be called once the one of line (or stream) is reached
        while (true) {
            char c = (char) read();
            if (isEnd) {
                currentLineNumber++; // increase for last line
                break;
            }
            
            if (c != '"') {
                // wasQuote is only relevant to detect double quotes ("")
                wasQuote = false;
            }
            
            if ((c == '\n' && !wasCarriageReturn) || c == '\r') {
                currentLineNumber++;
            }
            wasCarriageReturn = (c == '\r');
            
            if (c == separator && !inEscaped) {
                // we found an unescaped separator
                parts.add(notNull(currentElement.toString()));
                currentElement.setLength(0);
                // jump back to start, to not add the separator to the next field
                continue;
                
            } else if (c == '"') {
                if (!inEscaped && currentElement.length() == 0) {
                    // we found a " at the beginning of a field -> the field is escaped
                    inEscaped = true;
                    
                } else if (inEscaped && !wasQuote) {
                    // check if we are at the end of a field, by peeking at the next character
                    int peek = peek();
                    if (peek == -1 || peek == separator || peek == '\n' || peek == '\r') {
                        // we found a " at the end of a field -> escaping ended
                        inEscaped = false;
                        // the next iteration will read() the end of field, so we don't have to do anything
                        
                    } else {
                        // we just found a " in the middle of an escaped field
                        wasQuote = true;
                    }
                } else if (inEscaped) {
                    // we are the " after another "
                    wasQuote = false;
                }
                
            } else if ((c == '\n' || c == '\r') && !inEscaped) {
                // check if line-break is \r\n
                if (c == '\r') {
                    int next = peek();
                    if (next == '\n') {
                        // if \r is followed by \n, read the \n so that it doesn't re-appear in the next iteration.
                        read();
                    }
                }
                // a non-escaped end of line -> we are done with this row
                break;
            }
            currentElement.append(c);
        }
        
        @NonNull String @Nullable [] result;
        if (parts.isEmpty() && currentElement.length() == 0 && peek() == -1) {
            // ignore last line in file, if its empty
            // we know that we are at the last line, if we didn't find any fields
            //   (parts.empty() && currentElement.empty()) and the next char will be the end of stream
            result = null;
            isEnd = true;
            currentLineNumber--;
            
        } else {
            // add the last field (which we didn't find a separator for, because it was ended with a \n)
            parts.add(notNull(currentElement.toString()));
            
            result = notNull(parts.toArray(new @NonNull String[0]));
            for (int i = 0; i < result.length; i++) {
                result[i] = unescape(result[i]);
            }
        }
        return result;
    }
    
    @Override
    public @NonNull String @Nullable [] readNextRow() throws IOException {
        @NonNull String @Nullable [] result = null;
        
        if (!isEnd) {
            result = readLine();
        }
        
        return result;
    }
    
    @Override
    public int getLineNumber() {
        return currentLineNumber;
    }

}
