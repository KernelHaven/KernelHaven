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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.io.csv.CsvReader;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.Attribute;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.ConstraintFileType;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.VariableType;

/**
 * A cache for permanently saving (and reading) a variability model to a file.
 * 
 * @deprecated This is the old implementation, kept for converting old caches to the new format. Use
 * {@link JsonVariabilityModelCache} for the new version.
 * 
 * @author Adam
 */
@Deprecated
public class OldVariabilityModelCache extends AbstractCache<VariabilityModel> {
    
    private static final @NonNull String HEADER = "KernelHaven Variability Model Cache";
    
    private static final int VERSION = 4;
    
    private static final @NonNull String DESCRIPTOR_SEPARATOR = "---Model Descriptor---";
    
    private static final @NonNull String VARIABLE_SEPARATOR = "---Variables---";
    
    private static final @NonNull String VARIABLE_DATA_SEPARATOR = "---Variable Data---";
    
    private static final @NonNull String CONSTRAINT_FILE_SEPARATOR = "---Constraint File---";
    
    /**
     * The path where the cache should be written to.
     */
    private @NonNull File cacheFile;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir The directory where to store the cache files. This must be a directory, and we must be able to
     *      read and write to it.
     */
    public OldVariabilityModelCache(@NonNull File cacheDir) {
        cacheFile = new File(cacheDir, "vmCache");
    }

    /**
     * Writes the given VariabilityModel to the cache.
     *
     * @param vm The VariabilityModel to be written.
     * 
     * @throws IOException If writing the cache file fails.
     */
    @Override
    public void write(@NonNull VariabilityModel vm) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(cacheFile))) {
            
            // write meta information
            out.write(HEADER + "\n");
            out.write("Version: " + VERSION + "\n");
            
            // serialize model descriptor
            writeDescriptor(out, vm.getDescriptor());
            
            // serialize variables
            writeVariables(out, vm);
            
            // copy constraint file
            out.write(CONSTRAINT_FILE_SEPARATOR + "\n");
            try (FileReader constraintFile = new FileReader(vm.getConstraintModel())) {
                Util.copyStream(constraintFile, out);
            }
        }
    }
    
    /**
     * Writes the given {@link VariabilityModelDescriptor} to the given output stream.
     * 
     * @param out The output stream to write to.
     * @param descriptor The descriptor to serialize.
     * 
     * @throws IOException If writing to the stream fails.
     */
    private void writeDescriptor(@NonNull BufferedWriter out, @NonNull VariabilityModelDescriptor descriptor)
            throws IOException {
        
        out.write(DESCRIPTOR_SEPARATOR + "\n");
        
        out.write(descriptor.getVariableType().toString() + "\n");
        out.write(descriptor.getConstraintFileType().toString() + "\n");
        
        StringBuilder attributeStr = new StringBuilder();
        for (Attribute attr : descriptor.getAttributes()) {
            attributeStr.append(attr.name()).append(",");
        }
        // remove trailing ","
        if (attributeStr.length() > 0) {
            attributeStr.deleteCharAt(attributeStr.length() - 1);
        }
        
        out.write(attributeStr.toString() + "\n");
    }
    
    /**
     * Serializes the variables in the {@link VariabilityModel}.
     * 
     * @param out The output stream to write to.
     * @param vm The variability model to serialize.
     * 
     * @throws IOException If writing to the output stream fails.
     */
    private void writeVariables(@NonNull BufferedWriter out, @NonNull VariabilityModel vm) throws IOException {
        
        @SuppressWarnings("resource") // out is closed later on
        CsvWriter csvOut = new CsvWriter(out);
        
        /*
         * Step 1: Write all variables with type and name
         */
        out.write(VARIABLE_SEPARATOR + "\n");
        for (VariabilityVariable vv : vm.getVariables()) {
            String className = notNull(vv.getClass().getName());
            
            csvOut.writeRow(className, vv.getName(), vv.getType(), vv.getDimacsNumber());
        }
        
        /*
         * Step 2: Write extra data that is stored for specific types
         */
        out.write(VARIABLE_DATA_SEPARATOR + "\n");
        for (VariabilityVariable vv : vm.getVariables()) {
            List<@NonNull String> data = vv.getSerializationData();
            data.add(0, vv.getName());
            csvOut.writeRow(data.toArray(new @Nullable Object[0]));
        }
    }

    /**
     * Reads the VariabilityModel from the cache.
     * 
     * @return the variability model or <code>null</code> if the cache is not present.
     * @throws FormatException
     *             if the file is not correctly formatted as CSV or the data is
     *             invalid.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    @Override
    public @Nullable VariabilityModel read(@NonNull File target) throws FormatException, IOException {
        VariabilityModel result = null;
        try (LineNumberReader in = new LineNumberReader(new FileReader(cacheFile))) {
            String line;
            // read meta information
            line = readOrThrow(in, "header");
            if (!HEADER.equals(line)) {
                throw new FormatException("Found invalid header in line " + in.getLineNumber() + "; expected: "
                        + HEADER);
            }
            line = readOrThrow(in, "version");
            if (!line.startsWith("Version: ") || line.length() <= "Version: ".length()) {
                throw new FormatException("Expected \"Version: <version>\" in line " + in.getLineNumber());
            }
            int version;
            try {
                version = Integer.parseInt(line.substring("Version: ".length()));
            } catch (NumberFormatException e) {
                throw new FormatException("Can't parse version number: " + line.substring("Version: ".length()));
            }
            if (version != VERSION) {
                throw new FormatException("Invalid cache version: " + version + "; we only support " + VERSION);
            }
            
            // deserialize model descriptor
            VariabilityModelDescriptor descriptor = readDescriptor(in);
            
            // TODO: removed null annotations because jacoco report fails with it
            Map</*@NonNull*/ String, VariabilityVariable> variables = readVariables(in);
            
            File constraintCopy = File.createTempFile("constraintModel", "");
            constraintCopy.deleteOnExit();
            try (FileWriter out = new FileWriter(constraintCopy)) {
                Util.copyStream(in, out);
            }
            
            @SuppressWarnings("null") // TODO: null annotation missing, see above
            VariabilityModel r = new VariabilityModel(constraintCopy, variables);
            r.setDescriptor(descriptor);
            result = r;
            
        } catch (FileNotFoundException e) { // ignore, just return null
        }
        return result;
    }
    
    /**
     * Reads the {@link VariabilityModelDescriptor} from the given stream.
     * 
     * @param in The stream to read from.
     * 
     * @return The read {@link VariabilityModelDescriptor}.
     * 
     * @throws FormatException If the format is wrong.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull VariabilityModelDescriptor readDescriptor(@NonNull LineNumberReader in)
            throws FormatException, IOException {
        
        String line;
        VariabilityModelDescriptor result = new VariabilityModelDescriptor();
        
        if (!readOrThrow(in, "descriptor header").equals(DESCRIPTOR_SEPARATOR)) {
            throw new FormatException("Expected model descriptor header (\"" + DESCRIPTOR_SEPARATOR + "\") in line"
                    + in.getLineNumber());
        }
        
        line = readOrThrow(in, "descriptor variable type");
        VariableType variableType;
        try {
            variableType = VariableType.valueOf(line);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Can't read descriptor variable type in line" + in.getLineNumber(), e);
        }
        result.setVariableType(variableType);
        
        line = readOrThrow(in, "descriptor constraint file type");
        ConstraintFileType constraintFileType;
        try {
            constraintFileType = ConstraintFileType.valueOf(line);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Can't read descriptor constraint file type in line "
                    + in.getLineNumber(), e);
        }
        result.setConstraintFileType(constraintFileType);
        
        line = readOrThrow(in, "descriptor attributes");
        if (!line.isEmpty()) {
            String[] parts = line.split(",");
            for (String part : parts) {
                result.addAttribute(Attribute.valueOf(part));
            }
        }
        
        return result;
    }
    
    /**
     * Reads the serialized variables. This ends once the {@link #CONSTRAINT_FILE_SEPARATOR} has been found.
     * 
     * @param in The reader to read the variables from.
     * 
     * @return The read variables.
     * 
     * @throws IOException If reading the input stream fails.
     * @throws FormatException If the variables are malformed.
     */
    private @NonNull Map<@NonNull String, VariabilityVariable> readVariables(@NonNull LineNumberReader in)
            throws IOException, FormatException {
        
        /*
         * 1: Read variable class, name, type, dimacs number
         */
        
        if (!readOrThrow(in, "variable header").equals(VARIABLE_SEPARATOR)) {
            throw new FormatException("Expected variable header (\"" + VARIABLE_SEPARATOR + "\") in line"
                    + in.getLineNumber());
        }
        
        CsvReader csvIn = new CsvReader(in);
        
        Map<@NonNull String, VariabilityVariable> result = readVariablesRaw(in, csvIn);
        
        /*
         * 2: Read extra serialization data for variables
         */
        
        readVariableData(in, csvIn, result);
        
        return result;
    }

    /**
     * Reads the variable classes, names, types and DIMACS numbers from the given stream. This reads until
     * {@value #VARIABLE_DATA_SEPARATOR} is encountered.
     * 
     * @param in The stream to read from.
     * @param csvIn The same stream as a {@link CsvReader}.
     * 
     * @return A {@link Map} of all read variables, with their name as key.
     * 
     * @throws IOException If reading the stream fails.
     * @throws FormatException If the data is malformed.
     */
    private @NonNull Map<@NonNull String, VariabilityVariable> readVariablesRaw(LineNumberReader in, CsvReader csvIn)
            throws IOException, FormatException {
        Map<@NonNull String, VariabilityVariable> result = new HashMap<>();
        
        @NonNull String[] row;
        while ((row = csvIn.readNextRow()) != null) {
            if (row.length == 1 && row[0].equals(VARIABLE_DATA_SEPARATOR)) {
                break;
            }
            
            if (row.length != 4) {
                throw new FormatException("Found invalid row in line " + in.getLineNumber()
                    + "; expected class;name;type;dimacs");
            }
            
            int dimacsNumber;
            try {
                dimacsNumber = Integer.parseInt(row[3]);
            } catch (NumberFormatException e) {
                throw new FormatException(e);
            }
            
            try {
                @SuppressWarnings("unchecked")
                Class<? extends VariabilityVariable> clazz =
                        (Class<? extends VariabilityVariable>) ClassLoader.getSystemClassLoader().loadClass(row[0]);
                
                VariabilityVariable var = clazz.getConstructor(String.class, String.class)
                        .newInstance(row[1], row[2]);
                var.setDimacsNumber(dimacsNumber);
                
                result.put(row[1], var);
                
            } catch (ClassCastException | ReflectiveOperationException e) {
                throw new FormatException("Can't instantiate class " + row[0], e);
            }
        }
        
        if (row == null) {
            throw new FormatException("Expected variable data header (" + VARIABLE_DATA_SEPARATOR + "), but got EOF");
        }
        return result;
    }
    
    /**
     * Reads the (extra) serialization data for the variables. This reads until
     * {@value #CONSTRAINT_FILE_SEPARATOR} is encountered.
     * 
     * @param in The stream to read from.
     * @param csvIn The same stream as a {@link CsvReader}.
     * @param result The variables to set the data for.
     * 
     * @throws IOException If reading the stream fails.
     * @throws FormatException If the data is malformed.
     */
    private void readVariableData(LineNumberReader in, CsvReader csvIn,
            Map<@NonNull String, VariabilityVariable> result) throws IOException, FormatException {
        
        Set<@NonNull String> variablesLeft = new HashSet<>(result.keySet());
        
        @NonNull String[] row;
        while ((row = csvIn.readNextRow()) != null) {
            if (row.length == 1 && row[0].equals(CONSTRAINT_FILE_SEPARATOR)) {
                break;
            }
            
            if (row.length < 1) {
                throw new FormatException("Found invalid row in line " + in.getLineNumber()
                    + ", expected at least one column with variable name");
            }
            
            VariabilityVariable var = result.get(row[0]);
            if (var == null) {
                throw new FormatException("Found extra data for variable " + row[0]
                        + ", but this variable doesn't exist");
            }
            
            List<@NonNull String> data = new LinkedList<>();
            data.addAll(Arrays.asList(row));
            data.remove(0); // remove variable name
            
            try {
                var.setSerializationData(data, result);
                
            } catch (FormatException e) {
                throw new FormatException("Exception while deserializing extra data in line " + in.getLineNumber(), e);
            }
                
            variablesLeft.remove(var.getName());
        }
        
        if (row == null) {
            throw new FormatException("Expected constraint file header (" + CONSTRAINT_FILE_SEPARATOR
                    + "), but got EOF");
        }
        
        if (!variablesLeft.isEmpty()) {
            throw new FormatException("Missing serialization data for " + variablesLeft);
        }
    }

    /**
     * Reads a line from the given reader. Throws a {@link FormatException} if the end of the stream has been reached.
     * 
     * @param in The reader to read from.
     * @param expected What we are trying to read. Used for error message when end of stream has been reached.
     * 
     * @return The read line.
     * 
     * @throws FormatException If the end of the stream has been reached.
     * @throws IOException If reading the input stream fails.
     */
    private static @NonNull String readOrThrow(@NonNull LineNumberReader in, @NonNull String expected)
            throws FormatException, IOException {
        
        String line = in.readLine();
        
        if (line == null) {
            throw new FormatException("Expected " + expected + " in line " + in.getLineNumber()
            + ", but was end of file");
        }
        
        return line;
    }

}
