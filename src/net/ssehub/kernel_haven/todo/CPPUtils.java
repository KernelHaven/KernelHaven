package net.ssehub.kernel_haven.todo;

/**
 * Utility functions to handle CPP statements.
 * @author El-Sharkawy
 *
 */
public class CPPUtils {

    /**
     * Avoid instantiation.
     */
    private CPPUtils() {}
    
    /**
     * Checks if a line is a CPP <tt>if</tt> or <tt>elif</tt> line, but not a <tt>ifdef</tt> or <tt>ifndef</tt> line.
     * @param line The complete line to test.
     * @return <tt>true</tt> if the given line is a CPP <tt>if</tt> or <tt>elif</tt> line
     */
    public static boolean isIfOrElifStatement(String line) {
        boolean result = false;
        
        String trimmedLine = line.trim();
        result = (trimmedLine.startsWith("#if") || line.startsWith("#elif"))
                 && !trimmedLine.startsWith("#ifdef")
                 && !trimmedLine.startsWith("#ifndef");            
        
        return result;
    }
}
