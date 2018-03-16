package net.ssehub.kernel_haven.util.cpp;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Utility functions for string to number operations.
 * 
 * @author El-Sharkawy
 *
 */
public class NumberUtils {

    /**
     * Don't allow any instances.
     */
    private NumberUtils() {
    }

    /**
     * Checks if a given String is an integer value.
     * @param str The string to test.
     * @param radix the radix (usually 10, or 16 for hex values).
     * 
     * @return <tt>true</tt> if the String is an Integer.
     * @see <a href="https://stackoverflow.com/a/5439547">https://stackoverflow.com/a/5439547</a>
     */
    public static boolean isInteger(@NonNull String str, int radix) {
        boolean result = false;
        if (!str.isEmpty()) {
            result = true;
            for (int i = 0; i < str.length() && result; i++) {
                if (i == 0 && str.charAt(i) == '-') {
                    if (str.length() == 1) {
                        result = false;
                    }
                } else {
                    
                    if (Character.digit(str.charAt(i), radix) < 0) {
                        result = false;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Converts a string into a number (considering correct sub class, e.g., Long or Double).
     * 
     * @param str The string to convert.
     * @return A number or <tt>null</tt> if it could not be converted.
     */
    public static @Nullable Number convertToNumber(@NonNull String str) {
        Number result = null;
        
        if (isInteger(str, 10)) {
            // Convert normal long
            try {
                result = Long.valueOf(str, 10);
            } catch (NumberFormatException e) {
                // ignore, just return null
            }
        } else if (str.startsWith("0x") && isInteger(notNull(str.substring(2)), 16)) {
            // Convert hex value to long
            try {
                result = Long.valueOf(str.substring(2), 16);
            } catch (NumberFormatException e) {
                // ignore, just return null
            }
        } else {
            // Convert it into a double
            try {
                Double tmpResult = Double.valueOf(str);
                if ((tmpResult == Math.floor(tmpResult)) && !Double.isInfinite(tmpResult)) {
                    // Cast to Long if possible (e.g., if it ends with .0)
                    result = tmpResult.longValue();
                } else {
                    result = tmpResult;
                }
            } catch (NumberFormatException exc) {
                // Not critical, ignore
            }
        }
        
        return result;
    }

}
