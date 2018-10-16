package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;
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
 * A cache for permanently saving (and reading) a variability model to a (from
 * a) file.
 * 
 * @author Adam
 * @author Johannes
 */
public class VariabilityModelCache extends AbstractCache<VariabilityModel> {
    
    private static final @NonNull String HEADER = "KernelHaven Variability Model Cache";
    
    private static final int VERSION = 3;
    
    private static final @NonNull String DESCRIPTOR_SEPARATOR = "---Model Descriptor---";
    
    private static final @NonNull String VARIABLE_SEPARATOR = "---Variables---";
    
    private static final @NonNull String CONSTRAINT_FILE_SEPARATOR = "---Constraint File---";
    
    /**
     * The path where the cache should be written to.
     */
    private @NonNull File cacheFile;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     */
    public VariabilityModelCache(@NonNull File cacheDir) {
        cacheFile = new File(cacheDir, "vmCache");
    }

    /**
     * Writes the VariabilityModel to the cache.
     *
     * @param vm
     *            the VariabilityModel to be written Not Null.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    @Override
    public void write(@NonNull VariabilityModel vm) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(cacheFile))) {
            
            // write meta information
            out.write(HEADER + "\n");
            out.write("Version: " + VERSION + "\n");
            
            // serialize model descriptor
            out.write(DESCRIPTOR_SEPARATOR + "\n");
            VariabilityModelDescriptor descriptor = vm.getDescriptor();
            out.write(descriptor.getVariableType().toString() + "\n");
            out.write(descriptor.getConstraintFileType().toString() + "\n");
            
            StringBuilder attributeStr = new StringBuilder();
            for (Attribute attr : descriptor.getAttributes()) {
                attributeStr.append(attr.name()).append(",");
            }
            out.write(attributeStr.toString() + "\n");
            
            // serialize variables
            out.write(VARIABLE_SEPARATOR + "\n");
            @SuppressWarnings("resource") // out is closed by the try-with-resource block
            CsvWriter csvOut = new CsvWriter(out);
            for (VariabilityVariable vv : vm.getVariables()) {
                try {
                    String className = notNull(vv.getClass().getName());
                    
                    VariabilityVariableSerializer serializer
                            = VariabilityVariableSerializerFactory.INSTANCE.getSerializer(className);

                    csvOut.writeRow(className);
                    csvOut.writeRow(serializer.serialize(vv));
                    
                } catch (IllegalArgumentException e) {
                    throw new IOException("Can't serialize variable", e);
                }
            }
            
            // copy constraint file
            out.write(CONSTRAINT_FILE_SEPARATOR + "\n");
            try (FileReader constraintFile = new FileReader(vm.getConstraintModel())) {
                Util.copyStream(constraintFile, out);
            }
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
            if (version != 2 && version != VERSION) {
                throw new FormatException("Invalid cache version: " + version + "; we only support " + VERSION
                        + " and 2");
            }
            
            // deserialize model descriptor
            VariabilityModelDescriptor descriptor = readDescriptor(in, version);
            
            // TODO: removed null annotations because jacoco report fails with it
            Set</*@NonNull*/ VariabilityVariableSerializer> usedSerializers = new HashSet<>();
            @SuppressWarnings("null")
            Map</*@NonNull*/ String, VariabilityVariable> variables = readVariables(in, usedSerializers);
            
            File constraintCopy = File.createTempFile("constraintModel", "");
            constraintCopy.deleteOnExit();
            try (FileWriter out = new FileWriter(constraintCopy)) {
                Util.copyStream(in, out);
            }
            
            @SuppressWarnings("null") // TODO: null annotation missing, see above
            VariabilityModel r = new VariabilityModel(constraintCopy, variables);
            r.setDescriptor(descriptor);
            result = r;
            
            for (VariabilityVariableSerializer serializer : usedSerializers) {
                serializer.postProcess(result);
            }
            
        } catch (FileNotFoundException e) { // ignore, just return null
        }
        return result;
    }
    
    /**
     * Reads the {@link VariabilityModelDescriptor} from the given stream.
     * 
     * @param in The stream to read from.
     * @param version The version of the cache file.
     * 
     * @return The read {@link VariabilityModelDescriptor}.
     * 
     * @throws FormatException If the format is wrong.
     * @throws IOException If reading the stream fails.
     */
    private @NonNull VariabilityModelDescriptor readDescriptor(@NonNull LineNumberReader in, int version)
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
        
        if (version == 2) {
            // version 2 only had two booleans
            line = readOrThrow(in, "descriptor source locations");
            boolean hasSourceLocations = Boolean.parseBoolean(line);
            if (hasSourceLocations) {
                result.addAttribute(Attribute.SOURCE_LOCATIONS);
            }
            
            line = readOrThrow(in, "descriptor constraint usage");
            boolean hasConstraintUsage = Boolean.parseBoolean(line);
            if (hasConstraintUsage) {
                result.addAttribute(Attribute.CONSTRAINT_USAGE);
            }
            
        } else {
            line = readOrThrow(in, "descriptor attributes");
            
            if (!line.isEmpty()) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    result.addAttribute(Attribute.valueOf(part));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Reads the serialized variables. This ends once the {@link #CONSTRAINT_FILE_SEPARATOR} has been found.
     * 
     * @param in The reader to read the variables from.
     * @param usedSerializers A set that will be filled with all serializers that were used.
     * 
     * @return The read variables.
     * 
     * @throws IOException If reading the input stream fails.
     * @throws FormatException If the variables are malformed.
     */
    private @NonNull Map<@NonNull String, VariabilityVariable> readVariables(@NonNull LineNumberReader in,
            @NonNull Set<@NonNull VariabilityVariableSerializer> usedSerializers) throws IOException, FormatException {
        
        if (!readOrThrow(in, "variable header").equals(VARIABLE_SEPARATOR)) {
            throw new FormatException("Expected variable header (\"" + VARIABLE_SEPARATOR + "\") in line"
                    + in.getLineNumber());
        }
        
        @SuppressWarnings("resource") // we don't want to close in
        CsvReader csvIn = new CsvReader(in);
        
        Map<@NonNull String, VariabilityVariable> result = new HashMap<>();
        
        @NonNull String[] classNameRow = csvIn.readNextRow();
        while (classNameRow != null && classNameRow.length == 1 && !classNameRow[0].equals(CONSTRAINT_FILE_SEPARATOR)) {
            
            VariabilityVariableSerializer serializer;
            
            try {
                serializer = VariabilityVariableSerializerFactory.INSTANCE.getSerializer(classNameRow[0]);
                usedSerializers.add(serializer);
                
            } catch (IllegalArgumentException e) {
                throw new FormatException("Found no serializer for class name " + classNameRow[0] + " in line "
                        + csvIn.getLineNumber());
            }
            
            @NonNull String[] contentRow = csvIn.readNextRow();
            if (contentRow == null) {
                throw new FormatException("Expected serialized variable in line " + csvIn.getLineNumber());
            }
            try {
                VariabilityVariable read = serializer.deserialize(contentRow);
                result.put(read.getName(), read);
                
            } catch (FormatException e) {
                throw new FormatException("Can't deserialize variable in line " + csvIn.getLineNumber(), e);
            }
            
            classNameRow = csvIn.readNextRow();
        }
        
        if (classNameRow == null || classNameRow.length != 1) {
            throw new FormatException("Found invalid class name in line " + csvIn.getLineNumber());
        }
        
        return result;
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
