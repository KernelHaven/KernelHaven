package net.ssehub.kernel_haven.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A time-stamp for creating filenames that want to identify which execution created them.
 * The global singleton instance  ensures that all time-stamps originating from one execution are equal.
 *  
 * @author Adam
 */
public class Timestamp {
    
    /**
     * Global time-stamp to ensure that files created by one execution all get the same time-stamp.
     */
    public static final Timestamp INSTANCE = new Timestamp();
    
    /**
     * Time-stamp for normal output.
     */
    private String timestamp;
    
    /**
     * Time-stamp for filenames (no spaces or colons).
     */
    private String filestamp;
    
    /**
     * Creates a time-stamp for the current time and date.
     */
    public Timestamp() {
        DateTimeFormatter file = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        DateTimeFormatter normal = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        timestamp = normal.format(now);
        filestamp = file.format(now);
    }
    
    /**
     * Returns this time-stamp in the format 'yyyy-MM-dd HH:mm:ss'.
     * 
     * @return This time-stamp as a string.
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns this time-stamp in the format 'yyy-MM-dd_HH-mm-ss'. This is safe to be used in filenames.
     * 
     * @return This time-stamp as a string to be used in time-stamps.
     */
    public String getFileTimestamp() {
        return filestamp;
    }
    
    /**
     * Creates a filename containing this time-stamp. It will have the format
     * <code>&lt;prefix&gt;_&lt;time-stamp&gt;.&lt;suffix&gt;</code>
     * The time-stamp is safe for filenames (no spaces, no colons).
     * 
     * @param prefix The prefix for the filename.
     * @param suffix The suffix to be appended after a '.'.
     * @return A filename containing this time-stamp.
     */
    public String getFilename(String prefix, String suffix) {
        return prefix + '_' + filestamp + '.' + suffix;
    }
    
    @Override
    public String toString() {
        return getTimestamp();
    }

}
