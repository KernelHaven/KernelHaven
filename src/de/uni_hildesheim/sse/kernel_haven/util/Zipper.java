package de.uni_hildesheim.sse.kernel_haven.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for creating and writing to zip-files.
 * 
 * @author kevin
 * @author moritz
 *
 */
public class Zipper {

    /**
     * The filesystem that is used to write files to.
     */
    private URI zipFsUri;

    /**
     * Instantiates a new BetterZipper with a target file that is used as the
     * target for writing to zip. Creates the zip-file if it does not exist.
     * 
     * @param file
     *            the zip file to write to. Must not be null.
     * @throws IOException
     *             unwanted.
     */
    public Zipper(File file) throws IOException {
        init(file);
    }

    /**
     * Initialization method.
     * 
     * @param file
     *            to write to.
     * 
     * @throws IOException
     *             unwanted.
     */
    private void init(File file) throws IOException {
        this.zipFsUri = URI.create("jar:" + file.toURI().normalize());

        // create empty zip file if target does not exist
        if (!file.exists()) {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
            zip.close();
        }

    }

    /**
     * Copies file from the filesystem to zip-file. If a folder is passed to
     * this function all of the contained files will be copied to the target
     * location as well.
     * 
     * @param externalFile
     *            file in the external filesystem from which to copy. Must not be
     *            null.
     * @param relativeZipPath
     *            target path in the zip file. Must not be null.
     *            
     * @throws NoSuchFileException If the externalFile does not exist.
     * @throws IOException
     *             thrown if fileaccess fails.
     */
    public void copyFileToZip(File externalFile, String relativeZipPath) throws NoSuchFileException, IOException {
        copySingleElementToZip(externalFile, relativeZipPath);
        if (externalFile.isDirectory()) {
            File[] files = externalFile.listFiles();
            for (File file : files) {
                copyFileToZip(file, relativeZipPath + "/" + file.getName());
            }
        }
    }

    /**
     * Copies single elements from the filesystem to the zip-file. If a folder
     * is passed to this method, only the empty folder without its content will
     * be copied to the zip-file.
     * 
     * @param externalFile
     *            - the file that should be written to the zip-file.
     * @param relativeZipPath
     *            - target path inside of the zip-File. .
     *             
     * @throws NoSuchFileException If the externalFile does not exist.
     * @throws IOException
     *             thrown if fileaccess fails.
     */
    private void copySingleElementToZip(File externalFile, String relativeZipPath)
            throws NoSuchFileException, IOException {

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        // This is the try-with-resources Syntax introduced with Java 7
        try (FileSystem zipfs = FileSystems.newFileSystem(this.zipFsUri, env)) {

            Path externalFilePath = externalFile.toPath();
            Path pathInZipfile = zipfs.getPath(relativeZipPath);
            Files.createDirectories(pathInZipfile.getParent());
            Files.copy(externalFilePath, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
