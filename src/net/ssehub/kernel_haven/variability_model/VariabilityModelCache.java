package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.io.csv.CsvReader;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;

/**
 * A cache for permanently saving (and reading) a variability model to a (from
 * a) file.
 * 
 * @author Adam
 * @author Johannes
 */
public class VariabilityModelCache extends AbstractCache<VariabilityModel> {
    
    /**
     * The path where the CNF File should be stored.
     */
    private File constraintCache;

    /**
     * The path where the VariableSet should be stored.
     */
    private File variablesCacheFile;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     */
    public VariabilityModelCache(File cacheDir) {
        variablesCacheFile = new File(cacheDir, "vmCache.variables");
        constraintCache = new File(cacheDir, "vmCache.constraints");
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
    public void write(VariabilityModel vm) throws IOException {
        // Write ConstraintModel
        Util.copyFile(vm.getConstraintModel(), constraintCache);
        
        // Write Set
        try (CsvWriter writer = new CsvWriter(new FileOutputStream(variablesCacheFile))) {
            for (VariabilityVariable vv : vm.getVariables()) {
                
                List<String> serialized = vv.serializeCsv();
                
                Object[] csvParts = new String[serialized.size() + 1];
                csvParts[0] = vv.getClass().getName();
                int i = 1;
                for (String part : serialized) {
                    csvParts[i++] = part;
                }
                writer.writeRow(csvParts);

            }
        }
    }

    /**
     * Reads the VariabilityModel from the cache.
     * 
     * @return the variability model or <code>null</code> if the cache is not present.
     * @throws FormatException
     *             if the file is not correctly formatted as csv or the data is
     *             invalid.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    @Override
    public VariabilityModel read(File target) throws FormatException, IOException {
        VariabilityModel vm = null;
        
        // Generate VariabilityVariables
        CsvReader reader = null;

        try {
            if (!constraintCache.isFile()) {
                // throw this so we return null
                throw new FileNotFoundException();
            }
            
            Set<VariabilityVariable> variables = new HashSet<>();

            reader = new CsvReader(new FileInputStream(variablesCacheFile));
            String[] csv;
            while ((csv = reader.readNextRow()) != null) {
                String className = csv[0];
                @SuppressWarnings("unchecked")
                Class<? extends VariabilityVariable> clazz = (Class<? extends VariabilityVariable>) Class
                        .forName(className);
                Method m = clazz.getMethod("createFromCsv", String[].class);
                String[] smallCsv = new String[csv.length - 1];
                System.arraycopy(csv, 1, smallCsv, 0, smallCsv.length);
                variables.add((VariabilityVariable) m.invoke(null, (Object) smallCsv));
            }

            File constraintCopy = File.createTempFile("constraintModel", "");
            constraintCopy.deleteOnExit();
            Util.copyFile(constraintCache, constraintCopy);
            
            vm = new VariabilityModel(constraintCopy, variables);
        } catch (ReflectiveOperationException | ClassCastException | IllegalArgumentException e) {
            throw new FormatException(e);
            
        } catch (FileNotFoundException e) {
            // this means that the cache is not present, so we just return null


            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return vm;
    }

}
