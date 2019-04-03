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
package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Storage class for the source location. Stores sourcefile as well as
 * linennumber. This is used for representing the location of fragments of code.
 * 
 * @author marvin
 * @author moritz
 *
 *
 */
public class SourceLocation {
    
    private @NonNull File source;
    
    private int lineNumber;

    /**
     * Constructor for source location.
     * 
     * @param source
     *            the sourcefile containing the codefragment. This is relative to the source tree.
     * @param lineNumber
     *            the line in the sourcefile
     */
    public SourceLocation(@NonNull File source, int lineNumber) {
        this.source = source;
        this.lineNumber = lineNumber;
    }

    /**
     * Gets a File-object representing the sourcefile.
     * 
     * @return source the file source where the code is found. Relative to the source tree.
     */

    public @NonNull File getSource() {
        return source;
    }

    /**
     * Gets the line number of the codefragment.
     * 
     * 
     * @return lineNumber the number where the code is found.
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Creates a JSON representation of this object.
     * 
     * @return A JSON of this object.
     */
    @NonNull JsonElement toJson() {
        JsonObject result = new JsonObject();
        
        result.putElement("file", new JsonString(notNull(source.getPath().replace(File.separatorChar, '/'))));
        result.putElement("line", new JsonNumber(lineNumber));
        
        return result;
    }
    
    /**
     * Creates a {@link SourceLocation} from the given JSON.
     * 
     * @param obj The JSON object to convert.
     * 
     * @return The read {@link SourceLocation}.
     * 
     * @throws FormatException If the JSON has an invalid structure.
     */
    static @NonNull SourceLocation fromJson(@NonNull JsonObject obj) throws FormatException {
        if (obj.getSize() != 2) {
            throw new FormatException("Expected JsonObject with exactly 2 entries, but got " + obj.getSize());
        }
        return new SourceLocation(new File(obj.getString("file")), obj.getInt("line"));
    }

    @Override
    public int hashCode() {
        return source.hashCode() + new Integer(lineNumber).hashCode();
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean success = false;

        if (obj instanceof SourceLocation) {
            SourceLocation other = (SourceLocation) obj;
            success = other.lineNumber == this.lineNumber && other.source.equals(this.source);
        }

        return success;
    }

    @Override
    public @NonNull String toString() {
        return source.getPath() + ":" + lineNumber;
    }

}
