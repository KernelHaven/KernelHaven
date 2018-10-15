package net.ssehub.kernel_haven.variability_model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A cache for permanently saving (and reading) a variability model to a (from
 * a) file.
 * 
 * @author Adam
 * @author Johannes
 */
public class VariabilityModelCache extends AbstractCache<VariabilityModel> {
    
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
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            out.writeObject(vm);
            try (FileInputStream constraintFile = new FileInputStream(vm.getConstraintModel())) {
                out.writeObject(Util.readStream(constraintFile));
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
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(cacheFile))) {
            
            try {
                result = (VariabilityModel) in.readObject();
                
                File constraintCopy = File.createTempFile("constraintModel", "");
                constraintCopy.deleteOnExit();
                BufferedWriter out = new BufferedWriter(new FileWriter(constraintCopy));
                out.write((String) in.readObject());
                out.close();
                
                result.setConstraintModel(constraintCopy);
                
            } catch (ClassCastException | ClassNotFoundException e) {
                throw new FormatException(e);
            }
            
        } catch (StreamCorruptedException e) {
            throw new FormatException(e);
            
        } catch (FileNotFoundException e) { // ignore, just return null
        }
        return result;
    }
    
}
