/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

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
    public static final @NonNull Timestamp INSTANCE;
    
    private static final @NonNull DateTimeFormatter FILE_FORMAT;
    
    private static final @NonNull DateTimeFormatter NORMAL_FORMAT;
    
    static {
        // make sure to initialize formats before INSTANCE
        NORMAL_FORMAT = notNull(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        FILE_FORMAT = notNull(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        
        INSTANCE = new Timestamp();
    }
    
    /**
     * Time-stamp for normal output.
     */
    private @NonNull String timestamp;
    
    /**
     * Time-stamp for filenames (no spaces or colons).
     */
    private @NonNull String filestamp;
    
    /**
     * Creates a time-stamp for the current time and date.
     */
    public Timestamp() {
        timestamp = "will be initialized";
        filestamp = "will be initialized";
        setToNow();
    }
    
    /**
     * Updates this time-stamp to the current time and date. This is useful if the global {@link #INSTANCE} needs to be
     * "reset".
     */
    public void setToNow() {
        LocalDateTime now = LocalDateTime.now();
        timestamp = notNull(NORMAL_FORMAT.format(now));
        filestamp = notNull(FILE_FORMAT.format(now));
    }
    
    /**
     * Returns this time-stamp in the format 'yyyy-MM-dd HH:mm:ss'.
     * 
     * @return This time-stamp as a string.
     */
    public @NonNull String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns this time-stamp in the format 'yyy-MM-dd_HH-mm-ss'. This is safe to be used in filenames.
     * 
     * @return This time-stamp as a string to be used in time-stamps.
     */
    public @NonNull String getFileTimestamp() {
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
    public @NonNull String getFilename(@NonNull String prefix, @NonNull String suffix) {
        return prefix + '_' + filestamp + '.' + suffix;
    }
    
    @Override
    public @NonNull String toString() {
        return getTimestamp();
    }

}
