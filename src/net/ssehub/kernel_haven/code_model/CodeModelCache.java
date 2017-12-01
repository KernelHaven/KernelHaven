package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.ZipArchive;
import net.ssehub.kernel_haven.util.io.csv.CsvReader;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;

/**
 * A cache for permanently saving (and reading) a code model to a (from a)
 * file.
 * 
 * @author Adam
 * @author Alice
 */
public class CodeModelCache extends AbstractCache<SourceFile> {
    
    private File cacheDir;
    
    private boolean compress;
    
    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     */
    public CodeModelCache(File cacheDir) {
        this.cacheDir = cacheDir;
    }
    
    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     * @param compress Whether the cache files should be written compressed. Already existing compressed cache files
     *      are always read, even if compression is turned off.
     */
    public CodeModelCache(File cacheDir, boolean compress) {
        this.cacheDir = cacheDir;
        this.compress = compress;
    }
    
    /**
     * Returns the path where the given source file should be cached.
     * 
     * @param path The path of the source file, relative to the source code tree.
     * @return The file where to cache.
     */
    private File getCacheFile(File path) {
        String name = path.getPath().replace(File.separatorChar, '.') + ".cache";
        return new File(cacheDir, name);
    }
    
    /**
     * Returns the path where the given source file should be cached, if compression is turned on.
     * 
     * @param path The path of the source file, relative to the source code tree.
     * @return The file where to cache.
     */
    private File getCompressedCacheFile(File path) {
        String name = path.getPath().replace(File.separatorChar, '.') + ".cache.zip";
        return new File(cacheDir, name);
    }
    
    /**
     * Writes the given {@link SourceFile} to the cache.
     * 
     * @param file The file to write to the cache. Must not be <code>null</code>.
     * @throws IOException If writing the cache file fails.
     */
    @Override
    public void write(SourceFile file) throws IOException {
        File cacheFile;
        if (compress) {
            // delete the uncompressed version, since this method is supposed to overwrite any previous cache
            getCacheFile(file.getPath()).delete();
            cacheFile = getCompressedCacheFile(file.getPath());
        } else {
            cacheFile = getCacheFile(file.getPath());
        }
        
        CsvWriter writer = null;
        ZipArchive archive = null;
        try {
            OutputStream fileStream;
            if (compress) {
                archive = new ZipArchive(cacheFile);
                fileStream = archive.getOutputStream(new File("cache"));
            } else {
                fileStream = new FileOutputStream(cacheFile);
            }
            writer = new CsvWriter(fileStream);

            for (CodeElement element : file) {
                serializeElement(element, 0, writer);
            }
            
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
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
    
    /**
     * Serializes a single element to the given writer. Recursively serialize the childreen
     * 
     * @param element The element to serialize.
     * @param level The nesting depth.
     * @param writer The writer to write to.
     * @throws IOException If writing fails.
     */
    private void serializeElement(CodeElement element, int level, CsvWriter writer) throws IOException {
        List<String> serialized = element.serializeCsv();
        
        String[] csvParts = new String[serialized.size() + 2];
        csvParts[0] = element.getClass().getName();
        csvParts[1] = Integer.toString(level);
        int i = 2;
        for (String part : serialized) {
            csvParts[i++] = part;
        }
        
        writer.writeRow(csvParts);
        
        for (CodeElement child : element.iterateNestedElements()) {
            serializeElement(child, level + 1, writer);
        }
    }
    
    /**
     * Reads the {@link SourceFile} for the given path from the cache.
     * 
     * @param path The path in the source code tree that should be read from the cache. Must not be <code>null</code>.
     * @return The {@link SourceFile} read from cache, or <code>null</code> if it was not in the cache.
     * 
     * @throws IOException If reading the cache fails.
     * @throws FormatException If the cache content is invalid.
     */
    @Override
    public SourceFile read(File path) throws IOException, FormatException {
        // always try uncompressed first, since its faster
        boolean compressed = false;
        File cacheFile = getCacheFile(path);
        File compressedCacheFile = getCompressedCacheFile(path);
        if (!cacheFile.exists() && compressedCacheFile.isFile()) {
            cacheFile = compressedCacheFile;
            compressed = true;
        }
        
        CsvReader reader = null;
        ZipArchive archive = null;
        SourceFile result = null;

        try {
            InputStream fileIn;
            if (compressed) {
                archive = new ZipArchive(cacheFile);
                fileIn = archive.getInputStream(new File("cache"));
            } else {
                fileIn = new FileInputStream(cacheFile);
            }
            reader = new CsvReader(fileIn);

            result = new SourceFile(path);

            VariableCache cache = new VariableCache();
            Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));

            Stack<CodeElement> nesting = new Stack<>();
            
            String[] csvParts;
            while ((csvParts = reader.readNextRow()) != null) {
                readLine(csvParts, nesting, result, parser);
                
            }

        } catch (NumberFormatException e) {
            throw new FormatException(e);
            
        } catch (ReflectiveOperationException e) {
            throw new FormatException(e);
            
        } catch (FileNotFoundException e) {
            // ignore, so that null is returned if cache is not present

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
            if (archive != null) {
                try {
                    archive.close();
                } catch (IOException e) {

                }
            }
        }
        
        return result;
    }

    /**
     * Reads a single line from the cache.
     * 
     * @param csvParts The read csv parts.
     * @param nesting The nesting of elements.
     * @param result The result source file.
     * @param parser The parser to parse formulas with.
     * 
     * @throws ReflectiveOperationException If invoking the createFromCsv method fails on the fully qualified classname.
     */
    private void readLine(String[] csvParts, Stack<CodeElement> nesting, SourceFile result, Parser<Formula> parser)
            throws ReflectiveOperationException {

        String className = csvParts[0];
        int level = Integer.parseInt(csvParts[1]);
        
        CodeElement created;
        try {
            @SuppressWarnings("unchecked")
            Class<? extends CodeElement> clazz = (Class<? extends CodeElement>) Class.forName(className);
            Method m = clazz.getMethod("createFromCsv", String[].class, Parser.class);
            String[] smallCsv = new String[csvParts.length - 2];
            System.arraycopy(csvParts, 2, smallCsv, 0, smallCsv.length);
            created = (CodeElement) m.invoke(null, (Object) smallCsv, parser);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new ReflectiveOperationException(e);
        }
        
        while (level < nesting.size()) {
            nesting.pop();
        }
        
        if (level == 0) {
            result.addElement(created);
        } else {
            nesting.peek().addNestedElement(created);
        }
        
        nesting.push(created);
    }

}
