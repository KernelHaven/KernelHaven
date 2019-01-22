package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Wrapper for accessing files inside a zip archive.
 * 
 * @author Adam
 */
public class ZipArchive implements Closeable {
    
    private FileSystem archive;
    
    /**
     * Opens a zip archive. If the archive does not exist, then it is created as an empty archive.
     * 
     * @param location The location of the archive file.
     * 
     * @throws IOException If creating the empty archive fails.
     */
    public ZipArchive(@NonNull File location) throws IOException {
        URI uri = URI.create("jar:" + location.toURI().normalize());

        // create empty zip file if target does not exist
        if (!location.isFile()) {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(location));
            zip.close();
        }
        
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        archive = FileSystems.newFileSystem(uri, env);
    }
    
    /**
     * Checks, whether the archive contains the given file.
     * 
     * @param file The path of the file in the archive.
     * @return <code>true</code> if the file exists and is a file, <code>false</code> otherwise.
     * 
     * @throws IOException If reading the zip archive fails.
     */
    public boolean containsFile(@NonNull File file) {
        Path inZip = archive.getPath(file.getPath());
        return Files.isRegularFile(inZip);
    }
    
    /**
     * Returns the set of all files contained in the archive.
     * 
     * @return A set of files in the archive; paths are relative to the archive root.
     * 
     * @throws IOException If collecting the filenames fails. 
     */
    public @NonNull Set<File> listFiles() throws IOException {
        Set<File> result = Files.walk(archive.getPath("/"))
                // we only want files, not directories
                .filter((path) -> Files.isRegularFile(path))
                // convert Path to File (and remove leading '/')
                .map((path) -> new File(path.toString().substring(1)))
                // collect into set
                .collect(Collectors.toSet());
        
        return notNull(result);
    }
    
    /**
     * Reads the contents of a file in the archive.
     * 
     * @param file The path of the file in the archive.
     * @return The content of the file.
     * 
     * @throws FileNotFoundException If the archive does not contain the given file.
     * @throws IOException If reading the file fails.
     */
    public @NonNull String readFile(@NonNull File file) throws FileNotFoundException, IOException {
        InputStream in = getInputStream(file);
        String content = Util.readStream(in);
        in.close();
        
        return content;
    }
    
    /**
     * Retrieves an input stream to directly read from a file in the archive.
     * The stream must be closed by the caller.
     * 
     * @param file The path of the file in the archive.
     * @return An {@link InputStream} for the contents of the file.
     * 
     * @throws IOException If opening the input stream fails.
     */
    public @NonNull InputStream getInputStream(@NonNull File file) throws IOException {
        if (!containsFile(file)) {
            throw new FileNotFoundException("Archive does not contain file " + file);
        }
        
        Path inZip = archive.getPath(file.getPath());
        
        return notNull(Files.newInputStream(inZip));
    }
    
    /**
     * Returns the (uncompressed) size of the given file in the archive.
     * 
     * @param file The path of the file in the archive.
     * @return The size of the file in bytes.
     * 
     * @throws IOException If reading the file size fails.
     */
    public long getSize(@NonNull File file) throws IOException {
        if (!containsFile(file)) {
            throw new FileNotFoundException("Archive does not contain file " + file);
        }

        Path inZip = archive.getPath(file.getPath());
        
        return Files.size(inZip);
    }
    
    /**
     * Retrieves an {@link OutputStream} to write (or overwrite) the specified
     * file in the archive. The caller must close the stream.
     * 
     * @param file The path of the file in the archive.
     * @return A stream to write to the specified file.
     * 
     * @throws IOException If creating the stream fails.
     */
    public @NonNull OutputStream getOutputStream(@NonNull File file) throws IOException {
        Path inZip = archive.getPath(file.getPath());
        if (inZip.getParent() != null) {
            Files.createDirectories(inZip.getParent());
        }
        
        return notNull(Files.newOutputStream(inZip, StandardOpenOption.CREATE));
    }
    
    /**
     * Writes, or overwrites the given file in the archive.
     * 
     * @param file The path of the file in the archive.
     * @param content The new content of the file.
     * 
     * @throws IOException If writing the file fails.
     */
    public void writeFile(@NonNull File file, @NonNull String content) throws IOException {
        OutputStream out = getOutputStream(file);
        out.write(content.getBytes(StandardCharsets.UTF_8));
        out.close();
    }
    
    /**
     * Removes the given file from the archive.
     * 
     * @param file The path of the file in the archive.
     * 
     * @throws FileNotFoundException If the given file does not exist.
     * @throws IOException If an IO error occurs.
     */
    public void deleteFile(@NonNull File file) throws FileNotFoundException, IOException {
        if (!containsFile(file)) {
            throw new FileNotFoundException("Archive does not contain file " + file);
        }
        
        Path inZip = archive.getPath(file.getPath());
        Files.delete(inZip);
    }
    
    /**
     * Copies or overwrites the given file with the contents of <code>toCopy</code>.
     * 
     * @param file The path of the file in the archive.
     * @param toCopy The "real" file outside the archive to read the content of.
     * 
     * @throws FileNotFoundException If <code>toCopy</code> is not found.
     * @throws IOException If reading or writing the files fails.
     */
    public void copyFileToArchive(@NonNull File file, @NonNull File toCopy) throws FileNotFoundException, IOException {
        Path inZip = archive.getPath(file.getPath());
        if (inZip.getParent() != null) {
            Files.createDirectories(inZip.getParent());
        }
        
        Files.copy(toCopy.toPath(), inZip, StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Extracts the given file from the archive into the given target file.
     * The file in the archive remains unchanged.
     * 
     * @param file The path of the file in the archive.
     * @param target The "real" target file outside the archive. This file will
     *      be overwritten or created with the content from the archive file.
     *      
     * @throws FileNotFoundException If the given file in the archive does not exist.
     * @throws IOException If reading or writing the files fails.
     */
    public void extract(@NonNull File file, @NonNull File target) throws FileNotFoundException, IOException {
        Path inZip = archive.getPath(file.getPath());
        Files.copy(inZip, target.toPath());
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }
    
}
