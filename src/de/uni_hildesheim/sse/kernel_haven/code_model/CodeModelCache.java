package de.uni_hildesheim.sse.kernel_haven.code_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

import de.uni_hildesheim.sse.kernel_haven.util.FormatException;
import de.uni_hildesheim.sse.kernel_haven.util.Logger;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Formula;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.Parser;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.VariableCache;

/**
 * A cache for permanently saving (and reading) a code model to a (from a)
 * file.
 * 
 * @author Adam
 * @author Alice
 */
public class CodeModelCache {
    
    public static final String CACHE_DELIMITER = ";";
    
    private static final Logger LOGGER = Logger.get();
    
    private File cacheDir;

    
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
     * Writes the given {@link SourceFile} to the cache.
     * 
     * @param file The file to write to the cache. Must not be <code>null</code>.
     * @throws IOException If writing the cache file fails.
     */
    public void write(SourceFile file) throws IOException {
        File cacheFile = getCacheFile(file.getPath());
        
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));

            for (Block block : file) {
                serializeBlock(block, 0, writer);
            }
            
            LOGGER.logDebug("Code cache for file " + file.getPath() + " successfully written");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    /**
     * Serializes a single block to the given writer. Recursively serialize the childreen
     * 
     * @param block The block to serialize.
     * @param level The nesting depth.
     * @param writer The writer to write to.
     * @throws IOException If writing fails.
     */
    private void serializeBlock(Block block, int level, BufferedWriter writer) throws IOException {
        writer.write(block.getClass().getName() + CACHE_DELIMITER + level);
        
        for (String part : block.serializeCsv()) {
            writer.write(CACHE_DELIMITER + part);
        }
        writer.write("\n");
        
        for (Block child : block) {
            serializeBlock(child, level + 1, writer);
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
    @SuppressWarnings("unchecked")
    public SourceFile read(File path) throws IOException, FormatException {
        File cacheFile = getCacheFile(path);
        
        BufferedReader reader = null;
        SourceFile result = null;

        try {
            reader = new BufferedReader(new FileReader(cacheFile));

            result = new SourceFile(path);

            VariableCache cache = new VariableCache();
            Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));

            Stack<Block> nesting = new Stack<>();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] csvParts = line.split(CACHE_DELIMITER);

                String className = csvParts[0];
                int level = Integer.parseInt(csvParts[1]);
                
                Class<? extends Block> clazz = (Class<? extends Block>) Class.forName(className);
                Method m = clazz.getMethod("createFromCsv", String[].class, Parser.class);
                String[] smallCsv = new String[csvParts.length - 2];
                System.arraycopy(csvParts, 2, smallCsv, 0, smallCsv.length);
                Block created = (Block) m.invoke(null, (Object) smallCsv, parser);
                
                while (level < nesting.size()) {
                    nesting.pop();
                }
                
                if (level == 0) {
                    result.addBlock(created);
                } else {
                    nesting.peek().addChild(created);
                }
                
                nesting.push(created);
                
            }
            LOGGER.logDebug("Cache reading of code file " + path + " successful");

        } catch (NumberFormatException e) {
            throw new FormatException(e);
            
        } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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
        }
        
        if (result == null) {
            LOGGER.logDebug("Code cache does not contain " + path);
        }

        return result;
    }

}
