package net.ssehub.kernel_haven.util;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * This tool is able to check the existence of auxiliary tools which are packed inside a JAr and shall also be unpacked.
 * It further checks if the extracted tool is up to date and extracts the packed version if necessary.
 * @author El-Sharkawy
 *
 */
public class PreparationTool {
    
    private File destination;
    private String executable;
    private String sourceInJar;

    /**
     * Should be called as part of the constructor of the sub class.
     * @param destination The destination, where to extract the packed tool (must not be <tt>null</tt>, and the
     *     parent folder must exist). Note: This resource will be deleted if the tool needs to be unpacked/updated.
     * @param executable The executable of the packed tool, may be the same as the destination or <tt>null</tt> if
     *     the packed resource has no executable. Should be relative to <tt>destination</tt>, please use only slashes
     *     as file separators to be platform independent.
     * @param sourceInJar The destination inside the JAR archive, which shall be unpacked.
     */
    protected void init(@NonNull File destination, @Nullable String executable, @NonNull String sourceInJar) {
        this.destination = destination;
        this.executable = executable;
        this.sourceInJar = sourceInJar;
    }

    /**
     * Checks if the unpacked resource exists and is up to date. If this is not up to date or does not exist, it will
     * be unpacked. Existing resources will be deleted and overwritten.
     * @throws SetUpException If at the specified location could not be written (either non existent or outdated data
     * could not be wiped).
     */
    public void prepare() throws SetUpException {
        if (!isPrepared()) {
            if (destination.exists()) {
                try {
                    Util.deleteFolder(destination);
                } catch (IOException e) {
                    throw new SetUpException("Destination already exists and could not be deleted: "
                        + destination.getAbsolutePath() + ", cause: " + e.getMessage());
                }
            } else {
                destination.getParentFile().mkdirs();
            }
            
            File tmp = null;
            ZipArchive archive = null;
            try {
                tmp = Util.extractJarResourceToTemporaryFile(sourceInJar);
                archive = new ZipArchive(tmp);
                destination.mkdirs();
                for (File f : archive.listFiles()) {
                    File target = new File(destination, f.getPath());
                    target.getParentFile().mkdirs();
                    archive.extract(f, target);
                }
                
                if (null != executable) {
                    File exec = new File(destination, executable);
                    if (!exec.exists()) {
                        throw new SetUpException("Specified executable does not exist after extraction of all "
                                + "resources: " + exec.getAbsolutePath());
                    }
                    exec.setExecutable(true);
                }
            } catch (IOException e) {
                throw new SetUpException("Could not extract specified ressource: \"" + sourceInJar + "\", "
                        + "cause: " + e.getMessage());
            } finally {
                if (tmp != null) {
                    if (tmp.isDirectory()) {
                        try {
                            Util.deleteFolder(tmp);
                        } catch (IOException e) {
                        }
                    } else {
                        tmp.delete();
                    }
                }
                if (archive != null) {
                    try {
                        archive.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
    
//    /**
//     * Deleted the specified resource, will also delete a folder with all of its nested files.
//     * @param file A file or folder to delete.
//     * @return <tt>true</tt> if it was successful, <tt>false</tt> if for some reason the resource could not be deleted
//     * completely.
//     */
//    private boolean deleteQuitetely(File file) {
//        boolean result = true;
//        
//        // Recursive part
//        if (file.isDirectory()) {
//            for (File nestedFile : file.listFiles()) {
//                result &= deleteQuitetely(nestedFile);
//            }
//        }
//        
//        // Delete file or empty folder
//        result &= file.delete();
//        
//        return result;
//    }

    /**
     * Checks if an packed tool is was extracted and is up to date.
     * @return <tt>true</tt> if it exist and is up to date, <tt>false</tt> it needs to be extracted (again).
     */
    protected boolean isPrepared() {
        return isExistent() && !isOutdated();
    }

    /**
     * Checks if an extracted tool is outdated and have to be replaced by an update (does not check if the extracted
     * tool exists).
     * 
     * @return <tt>true</tt> if the extracted tool is outdated and shall be overwritten.
     * @see #isExistent()
     */
    protected boolean isOutdated() {
        // TODO SE: Add default algorithm, e.g., compare JAR and resource date or integrate build date into JAR
        return false;
    }

    /**
     * Checks if the packed tool was already extracted before.
     * 
     * @return <tt>true</tt> if it was already extracted, <tt>false</tt> if it still needs to be extracted.
     */
    protected boolean isExistent() {
        return destination.exists();
    }
    
}
