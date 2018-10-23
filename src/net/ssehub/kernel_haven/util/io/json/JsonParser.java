package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A parser to parse an input stream of JSON.
 * 
 * @see <a href="https://www.json.org/">https://www.json.org/</a>
 * 
 * @author Adam
 */
public class JsonParser implements Closeable {

    private @NonNull LineNumberReader in;
    
    private @Nullable Integer peek;

    /**
     * Creates a parser for the given input stream. Internally, the stream will be wrapped in a {@link BufferedReader}.
     * 
     * @param in The input stream.
     */
    public JsonParser(@NonNull Reader in) {
        this.in = new LineNumberReader(in);
    }
    
    /**
     * Closes the input data stream.
     */
    @Override
    public void close() throws IOException {
        in.close();
    }
    
    /**
     * Peeks at the next character to read. Subsequent calls to {@link #read()} will return exactly this value.
     * Multiple calls to {@link #peek()} without calls to {@link #read()} will return the same value (i.e. the stream
     * is <b>not</b> read multiple times).
     * 
     * @return The next character, as returned by {@link Reader#read()}.
     * 
     * @throws IOException If reading the input stream fails.
     */
    private int peek() throws IOException {
        if (peek == null) {
            peek = in.read();
        }
        return notNull(peek);
    }
    
    /**
     * Reads the next character. Resets {@link #peek()} (i.e. subsequent calls to {@link #peek()} will return a new
     * character).
     * 
     * @return The next character, as returned by {@link Reader#read()}.
     * 
     * @throws IOException If reading the input stream fails.
     */
    private int read() throws IOException {
        int result;
        if (peek != null) {
            result = peek;
            peek = null;
        } else {
            result = in.read();
        }
        
        return result;
    }
    
    /**
     * Checks if the given character is a JSON whitespace.
     * 
     * @param character The character to check.
     * 
     * @return Whether the character is a JSON whitespace.
     */
    private boolean isWhitespace(int character) {
        return character == '\t'
                || character == '\n'
                || character == '\r'
                || character == ' ';
    }
    
    /**
     * Reads the stream until no more whitespaces occur. After this method, the next {@link #read()} or {@link #peek()}
     * will not be a whitespace character.
     * 
     * @see #isWhitespace(int)
     * 
     * @throws IOException If reading the stream fails.
     */
    private void skipWhitespace() throws IOException {
        while (isWhitespace(peek())) {
            read();
        }
    }
    
    /**
     * Parses the stream to a {@link JsonElement}.
     * 
     * @return The parsed JSON.
     * 
     * @throws FormatException If the stream data is malformed.
     * @throws IOException If reading the input stream fails.
     */
    public @NonNull JsonElement parse() throws FormatException, IOException {
        JsonElement result = readElement();
        
        skipWhitespace();
        
        if (peek() != -1) {
            throw makeException("JSON element is over, but didn't reach EOF");
        }
        
        return result;
    }
    
    /**
     * Reads a single element from the stream.
     * 
     * @return The read element.
     * 
     * @throws FormatException If the element is malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonElement readElement() throws FormatException, IOException {
        skipWhitespace();
        
        JsonElement result;
        
        switch (peek()) {
        
        case '{':
            result = readObject();
            break;
        
        case '[':
            result = readList();
            break;
            
        case 't':
        case 'f':
            result = readBoolean();
            break;
            
        case 'n':
            result = readNull();
            break;
            
        case '"':
            result = readString();
            break;
            
        case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case '-':
            result = readNumber();
            break;
            
        default:
            throw makeException("Couldn't determine type: " + (char) peek());
        }
        
        return result;
    }
    
    /**
     * Reads a JSON object from the stream. The next character to read must be a '{'.
     * 
     * @return The read object.
     * 
     * @throws FormatException If the object (or any nested values) are malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonObject readObject() throws FormatException, IOException {
        read(); // read the '{'
        
        JsonObject result = new JsonObject();
        
        skipWhitespace();
        boolean expectingNext = peek() != '}';
        
        while (expectingNext) {
            expectingNext = false;
            
            skipWhitespace();
            
            if (peek() == '"') {
                String key = readString().getValue();
                
                skipWhitespace();
                
                int seperator = read();
                if (seperator != ':') {
                    throw makeException("Expecting ':' after key, got " + (char) seperator);
                }
                
                JsonElement value = readElement();
                result.putElement(key, value);
                
                skipWhitespace();
                if (peek() == ',') {
                    read();
                    expectingNext = true;
                }
                
            } else {
                throw makeException("Expecting key string, got " + (char) peek());
            }
        }
        
        skipWhitespace();
        
        int read = read();
        if (read != '}') {
            throw makeException("Expecting '}' at end of object, got " + (char) read);
        }
        
        return result;
    }
    
    /**
     * Reads a JSON list from the stream. The next character to read must be a '['.
     * 
     * @return The read list.
     * 
     * @throws FormatException If the list (or any nested values) are malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonList readList() throws FormatException, IOException {
        read(); // read the '['
        
        JsonList result = new JsonList();
        
        skipWhitespace();
        boolean expectingNext = peek() != ']';
        
        while (expectingNext) {
            expectingNext = false;
            
            skipWhitespace();
            
            JsonElement value = readElement();
            
            result.addElement(value);
            
            skipWhitespace();
            if (peek() == ',') {
                read();
                expectingNext = true;
            }
        }
        
        skipWhitespace();
        
        int read = read();
        if (read != ']') {
            throw makeException("Expecting ']' at end of list, got " + (char) read);
        }
        
        return result;
    }
    
    /**
     * Reads an JSON string from the stream. The next character to read must be a '"'.
     * 
     * @return The read string.
     * 
     * @throws FormatException If the string is malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonString readString() throws FormatException, IOException {
        read(); // read the '"'
        
        StringBuilder result = new StringBuilder();
        while (peek() != '\"' && peek() != -1) {
            int read = read();
            char unescaped;
            
            if (read == '\\') {
                read = read();
                
                switch (read) {
                case '"':
                case '\\':
                case '/':
                    unescaped = (char) read;
                    break;
                case 'b':
                    unescaped = '\b';
                    break;
                case 'n':
                    unescaped = '\n';
                    break;
                case 'r':
                    unescaped = '\r';
                    break;
                case 't':
                    unescaped = '\t';
                    break;
                case 'u':
                    StringBuilder hex = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        int hexChar = read();
                        if (!isHexDigit(hexChar)) {
                            throw makeException("Expected four hex digits after \\u, got '" + (char) hexChar + "'");
                        }
                        hex.append((char) hexChar);
                    }
                    // parseInt() won't throw a NumberFormatException, because we checked that only hex digits appear
                    unescaped = (char) Integer.parseInt(hex.toString(), 16);
                    break;
                    
                default:
                    throw makeException("Invalid escaped character '" + (char) read + "'");
                }
                
            } else {
                unescaped = (char) read;
            }
            
            result.append(unescaped);
        }
        
        int read = read();
        if (read != '"') {
            throw makeException("Expecting '\"' at end of string, got " + (char) read);
        }
        
        return new JsonString(notNull(result.toString()));
    }
    
    /**
     * Checks if the given character is a digit.
     * 
     * @param character The character to check.
     * 
     * @return Whether the character is a digit.
     */
    private boolean isDigit(int character) {
        return character >= '0' && character <= '9';
    }
    
    /**
     * Checks if the given character is a hexadecimal digit.
     * 
     * @param character The character to check.
     * 
     * @return Whether the character is a hexadecimal digit.
     */
    private boolean isHexDigit(int character) {
        // CHECKSTYLE:OFF // "boolean complexity" is too high...
        return (character >= '0' && character <= '9')
                || (character >= 'a' && character <= 'f')
                || (character >= 'A' && character <= 'F');
        // CHECKSTYLE:ON
    }
    
    /**
     * Reads an JSON number from the stream. The next character to read must be a digit or '-'.
     * 
     * @return The read number.
     * 
     * @throws FormatException If the number is malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonNumber readNumber() throws FormatException, IOException {
        Number result;
        
        StringBuilder intDigits = new StringBuilder();
        if (peek() == '-') {
            intDigits.append((char) read());
        }
        while (isDigit(peek())) {
            intDigits.append((char) read());
        }

        StringBuilder fracDigits = new StringBuilder();
        if (peek() == '.') {
            read(); // read '.'
            
            boolean foundOne = false;
            while (isDigit(peek())) {
                foundOne = true;
                fracDigits.append((char) read());
            }
            
            if (!foundOne) {
                throw makeException("Expected at least one digit after '.', got '" + (char) peek() + "'");
            }
        }
        
        StringBuilder expontentDigits = new StringBuilder();
        if (peek() == 'e' || peek() == 'E') {
            read(); // read the 'e'
            
            if (peek() == '-' || peek() == '+') {
                expontentDigits.append((char) read());
            }
            
            boolean foundOne = false;
            while (isDigit(peek())) {
                foundOne = true;
                expontentDigits.append((char) read());
            }
            
            if (!foundOne) {
                throw makeException("Expected at least one digit after 'E', got '" + (char) peek() + "'");
            }
        }
        
        try {
            if (fracDigits.length() == 0 && expontentDigits.length() == 0) {
                long l = Long.parseLong(intDigits.toString());
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                    result = (int) l;
                } else {
                    result = l;
                }
                
            } else {
                String toParse;
                if (expontentDigits.length() == 0) {
                    toParse = intDigits.toString() + '.' + fracDigits.toString();
                } else {
                    toParse = intDigits.toString() + '.' + fracDigits.toString() + 'E' + expontentDigits;
                }
                result = Double.parseDouble(toParse);
            }
        } catch (NumberFormatException e) {
            throw makeException("Can't parse number " + e.getMessage());
        }
        
        return new JsonNumber(result);
    }
    
    /**
     * Reads an JSON boolean from the stream. The next character to read must be 't' or 'f'.
     * 
     * @return The read boolean.
     * 
     * @throws FormatException If the boolean is malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonBoolean readBoolean() throws FormatException, IOException {
        JsonBoolean result;
        if (peek() == 't') {
            readAndAssert("true");
            result = JsonBoolean.TRUE;
            
        } else { // 'f'
            readAndAssert("false");
            result = JsonBoolean.FALSE;
        }
        
        return result;
    }
    
    /**
     * Reads an JSON null from the stream. The next characters to read must be "null".
     * 
     * @return The read null.
     * 
     * @throws FormatException If the null is malformed.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull JsonNull readNull() throws FormatException, IOException {
        readAndAssert("null");
        return JsonNull.INSTANCE;
    }
    
    /**
     * Reads the next characters from the stream and checks that they exactly match the given expected string.
     * 
     * @param expected The expected sequence of characters.
     * 
     * @throws FormatException If the read characters do not match the expected characters.
     * @throws IOException If reading the stream fails.
     */
    private void readAndAssert(@NonNull String expected) throws FormatException, IOException {
        for (int i = 0; i < expected.length(); i++) {
            int read = read();
            if (read != expected.charAt(i)) {
                throw makeException("Expected " + expected.charAt(i) + ", but got " + (char) read);
            }
        }
    }
    
    /**
     * Creates a {@link FormatException} with the given message. Adds information about the current line number.
     * 
     * @param message The exception message.
     * 
     * @return The created exception.
     */
    private FormatException makeException(@NonNull String message) {
        return new FormatException("Line " + (in.getLineNumber() + 1) + ": " + message);
    }
    
}
