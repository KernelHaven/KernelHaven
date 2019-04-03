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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Marks an (getter of an) attribute inside a class marked via the {@link TableRow} as a single column element. The
 * {@link #toString()} method of the return value will be used to fill the field in the table. The marked getter must
 * return a value that a {@link #toString()} can be called on, and must not have any parameters. If it returns null,
 * then an empty string ("") is inserted into the table at that position. The method marked by this annotation must be
 * public, otherwise it is ignored.
 *
 * @author Adam
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface TableElement {
    
    /**
     * Defines the position of this column in the table. The first column has the index 0.
     * 
     * @return The index of the column that this field should be placed in. Must not be negative. All fields in a given
     *      {@link TableRow} class must form a coherent interval, starting from 0.
     */
    int index();
    
    /**
     * The name of this field.
     * 
     * @return The name of this field, to be used in the table header.
     */
    @NonNull String name() default "";

}
