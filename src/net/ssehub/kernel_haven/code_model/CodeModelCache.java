package net.ssehub.kernel_haven.code_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Stack;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.ZipArchive;
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
    
    public static final String CACHE_DELIMITER = ";";
    
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
        
        BufferedWriter writer = null;
        ZipArchive archive = null;
        try {
            Writer fileWriter;
            if (compress) {
                archive = new ZipArchive(cacheFile);
                fileWriter = new OutputStreamWriter(archive.getOutputStream(new File("cache")));
            } else {
                fileWriter = new FileWriter(cacheFile);
            }
            writer = new BufferedWriter(fileWriter);

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
    private void serializeElement(CodeElement element, int level, BufferedWriter writer) throws IOException {
        writer.write(element.getClass().getName() + CACHE_DELIMITER + level);
        
        for (String part : element.serializeCsv()) {
            writer.write(CACHE_DELIMITER + part);
        }
        writer.write("\n");
        
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
        if (!cacheFile.exists()) {
            cacheFile = getCompressedCacheFile(path);
            compressed = true;
        }
        
        BufferedReader reader = null;
        ZipArchive archive = null;
        SourceFile result = null;

        try {
            Reader fileReader;
            if (compressed) {
                archive = new ZipArchive(cacheFile);
                fileReader = new InputStreamReader(archive.getInputStream(new File("cache")));
            } else {
                fileReader = new FileReader(cacheFile);
            }
            reader = new BufferedReader(fileReader);

            result = new SourceFile(path);

            VariableCache cache = new VariableCache();
            Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));

            Stack<CodeElement> nesting = new Stack<>();
            
            String line;
            while ((line = reader.readLine()) != null) {
                readLine(line, nesting, result, parser);
                
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
     * @param line The line to read.
     * @param nesting The nesting of elements.
     * @param result The result source file.
     * @param parser The parser to parse formulas with.
     * 
     * @throws ReflectiveOperationException If invoking the createFromCsv method fails on the fully qualified classname.
     */
    private void readLine(String line, Stack<CodeElement> nesting, SourceFile result, Parser<Formula> parser)
            throws ReflectiveOperationException {
        String[] csvParts = line.split(CACHE_DELIMITER);

        String className = csvParts[0];
        int level = Integer.parseInt(csvParts[1]);
        
        @SuppressWarnings("unchecked")
        Class<? extends CodeElement> clazz = (Class<? extends CodeElement>) Class.forName(className);
        Method m = clazz.getMethod("createFromCsv", String[].class, Parser.class);
        String[] smallCsv = new String[csvParts.length - 2];
        System.arraycopy(csvParts, 2, smallCsv, 0, smallCsv.length);
        CodeElement created = (CodeElement) m.invoke(null, (Object) smallCsv, parser);
        
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
