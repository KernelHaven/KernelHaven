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
package net.ssehub.kernel_haven.provider;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

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
    public abstract @Nullable ResultType read(@NonNull File target) throws FormatException, IOException;
    
    /**
     * Writes the given result from the extractor to the cache.
     * 
     * @param result The result to write.
     * 
     * @throws IOException If writing the cache files fails.
     */
    public abstract void write(@NonNull ResultType result) throws IOException;
    
}
