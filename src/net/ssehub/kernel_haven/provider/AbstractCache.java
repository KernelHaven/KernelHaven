package net.ssehub.kernel_haven.provider;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.util.FormatException;

/**
 * A cache for the result of an extractor.
 *
 * @param <ResultType> The result type of the extractor that should be stored.
 *
 * @author Adam
 */
public abstract class AbstractCache<ResultType> {

    /**
     * Read the result for the given target from cache.
     * 
     * @param target The target to read the result for.
     * @return The cached result, or <code>null</code> if not found in the cache.
     * 
     * @throws FormatException If the cache has an invalid format.
     * @throws IOException if reading the cache files fails.
     */
    public abstract ResultType read(File target) throws FormatException, IOException;
    
    /**
     * Writes the given result from the extractor to the cache.
     * 
     * @param result The result to write.
     * 
     * @throws IOException If writing the cache files fails.
     */
    public abstract void write(ResultType result) throws IOException;
    
}
