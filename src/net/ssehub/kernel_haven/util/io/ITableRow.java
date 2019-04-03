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
package net.ssehub.kernel_haven.util.io;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Interface for types that represent table rows. This means, that an instance of the class represents a single row
 * in a table-like structure.
 * 
 * @author Adam
 */
public interface ITableRow {

    /**
     * Returns headers (descriptions) for the different columns of this table row. It is recommended that the size
     * of this arrays is the same as {@link #getContent()}. This method will (most likely) be only called for the first
     * row of a table; thus, it should be descriptive for the complete table, instead of just this single row.
     * 
     * @return An array of strings that describes the columns of this table row. Never <code>null</code>.
     *      <code>null</code> elements inside this array will be converted into empty strings ("").
     *      Non-<code>null</code> elements will be converted via their {@link Object#toString()} method.
     */
    public @Nullable Object @NonNull [] getHeader();
    
    /**
     * Returns the column values for this row.
     * 
     * @return An array containing the column values for this row. Never <code>null</code>. <code>null</code> elements
     *      inside this array will be converted into empty strings (""). Non-<code>null</code> elements will be
     *      converted via their {@link Object#toString()} method.
     */
    public @Nullable Object @NonNull [] getContent();
    
}
