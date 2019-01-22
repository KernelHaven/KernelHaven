package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.ZipArchive;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonParser;
import net.ssehub.kernel_haven.util.io.json.JsonPrettyPrinter;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A cache for saving (and reading) a code model to a files, using JSON as
 * serialization.
 * 
 * @author Adam
 */
public class JsonCodeModelCache extends AbstractCache<SourceFile<?>> {

    private static final int VERSION = 2;
    
    private @NonNull File cacheDir;

    private boolean compress;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     */
    public JsonCodeModelCache(@NonNull File cacheDir) {
        this.cacheDir = cacheDir;
    }

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     * @param compress
     *            Whether the cache files should be written compressed. Already
     *            existing compressed cache files are always read, even if
     *            compression is turned off.
     */
    public JsonCodeModelCache(@NonNull File cacheDir, boolean compress) {
        this.cacheDir = cacheDir;
        this.compress = compress;
    }

    /**
     * Returns the path where the given source file should be cached.
     * 
     * @param path
     *            The path of the source file, relative to the source code tree.
     * @return The file where to cache.
     */
    private @NonNull File getCacheFile(@NonNull File path) {
        String name = path.getPath().replace(File.separatorChar, '.') + ".json";
        return new File(cacheDir, name);
    }

    /**
     * Returns the path where the given source file should be cached, if
     * compression is turned on.
     * 
     * @param path
     *            The path of the source file, relative to the source code tree.
     * @return The file where to cache.
     */
    private @NonNull File getCompressedCacheFile(@NonNull File path) {
        String name = path.getPath().replace(File.separatorChar, '.') + ".json.zip";
        return new File(cacheDir, name);
    }

    /**
     * Writes the given {@link SourceFile} to the cache.
     * 
     * @param file
     *            The file to write to the cache. Must not be <code>null</code>.
     * @throws IOException
     *             If writing the cache file fails.
     */
    @Override
    public void write(@NonNull SourceFile<?> file) throws IOException {
        File cacheFile;
        if (compress) {
            // delete the uncompressed version, since this method is supposed to
            // overwrite any previous cache
            getCacheFile(file.getPath()).delete();
            cacheFile = getCompressedCacheFile(file.getPath());
        } else {
            cacheFile = getCacheFile(file.getPath());
        }

        JsonElement json = serialize(file);
        
        if (compress) {
            try (ZipArchive archive = new ZipArchive(cacheFile);
                    OutputStream out = archive.getOutputStream(new File("cache.json"))) {
                
                out.write(json.accept(new JsonPrettyPrinter()).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            try (OutputStream out = new FileOutputStream(cacheFile)) {
                out.write(json.accept(new JsonPrettyPrinter()).getBytes(StandardCharsets.UTF_8));
            }
        }
    }
    
    /**
     * Holds the data necessary for a serialization run. This is encapsulated in a nested object, so that the
     * {@link JsonCodeModelCache} itself is stateless.
     */
    private final class SerializeData {
        
        private @NonNull Map<IdentityWrapper<CodeElement<?>>, Integer> idMapping;
        
        private int nextId;
        
        /**
         * Creates a new {@link SerializeData} instance. This instance should be used for once round of serialization
         */
        public SerializeData() {
            this.idMapping = new HashMap<>();
            this.nextId = 1;
        }
        
        /**
         * Returns the ID for the given {@link CodeElement} instance. Each instance gets an ID based on object identity.
         * 
         * @param element The instance to get the ID for.
         * 
         * @return The ID for the given instance.
         */
        public int getId(@NonNull CodeElement<?> element) {
            IdentityWrapper<CodeElement<?>> wrapper = new IdentityWrapper<>(element);
            Integer result = idMapping.get(wrapper);
            if (result == null) {
                result = nextId++;
                idMapping.put(wrapper, result);
            }
            return result;
        }
        
        /**
         * Serializes the given {@link CodeElement} to JSON.
         * 
         * @param element The element to serialize.
         * 
         * @return The element serialized as JSON.
         */
        public @NonNull JsonElement serialize(@NonNull CodeElement<?> element) {
            JsonObject result = new JsonObject();
            
            result.putElement("class", new JsonString(notNull(element.getClass().getName())));
            result.putElement("id", new JsonNumber(getId(element)));
            
            element.serializeToJson(result, this::serialize, this::getId);
            
            if (element.getNestedElementCount() > 0) {
                JsonList nestedJson = new JsonList();
                for (CodeElement<?> nested : element) {
                    nestedJson.addElement(serialize(nested));
                }
                
                result.putElement("nested", nestedJson);
            }
            
            return result;
        }
        
    }
    
    /**
     * Serializes the given {@link SourceFile} to JSON.
     * 
     * @param sourceFile The source file to serialize.
     * 
     * @return The source file serialized as JSON.
     */
    private @NonNull JsonElement serialize(@NonNull SourceFile<?> sourceFile) {
        JsonObject result = new JsonObject();
        
        result.putElement("version", new JsonNumber(VERSION));
        result.putElement("path", new JsonString(notNull(
                sourceFile.getPath().getPath().replace(File.separatorChar, '/'))));
        
        JsonList elements = new JsonList();
        
        SerializeData data = new SerializeData();
        for (CodeElement<?> element : sourceFile) {
            elements.addElement(data.serialize(element));
        }
        
        result.putElement("elements", elements);
        
        return result;
    }
    
    /**
     * Reads the {@link SourceFile} for the given path from the cache.
     * 
     * @param path
     *            The path in the source code tree that should be read from the
     *            cache. Must not be <code>null</code>.
     * @return The {@link SourceFile} read from cache, or <code>null</code> if
     *         it was not in the cache.
     * 
     * @throws IOException
     *             If reading the cache fails.
     * @throws FormatException
     *             If the cache content is invalid.
     */
    @Override
    public @Nullable SourceFile<?> read(@NonNull File path) throws IOException, FormatException {
        // always try uncompressed first, since its faster
        boolean compressed = false;
        File cacheFile = getCacheFile(path);
        File compressedCacheFile = getCompressedCacheFile(path);
        if (!cacheFile.exists() && compressedCacheFile.isFile()) {
            cacheFile = compressedCacheFile;
            compressed = true;
        }
        
        SourceFile<CodeElement<?>> result = null;
        try {
            JsonElement json;
            if (compressed) {
                try (ZipArchive archive = new ZipArchive(cacheFile);
                        JsonParser parser = new JsonParser(
                                new InputStreamReader(archive.getInputStream(new File("cache.json"))))) {
                    
                    json = parser.parse();
                }
            } else {
                try (JsonParser parser = new JsonParser(cacheFile)) {
                    json = parser.parse();
                }
            }
            
            result = deserialize(json);
            
        } catch (FileNotFoundException e) {
            // ignore, so that null is returned if cache is not present
        }

        return result;
    }
    
    /**
     * Functional interface that may throw an exception.
     *
     * @param <T> The input type.
     * @param <R> The output type.
     * @param <E> The exception type.
     */
    @FunctionalInterface
    public interface CheckedFunction<T, R, E extends Throwable> {
        
        /**
         * Applies this function to the given argument.
         * 
         * @param arg The argument to this function.
         * 
         * @return The result of this function.
         * 
         * @throws E An exception that this function may throw.
         */
        public R apply(T arg) throws E;
        
    }
    
    /**
     * Holds the data necessary for a de-serialization run. This is encapsulated in a nested object, so that the
     * {@link JsonCodeModelCache} itself is stateless.
     */
    private final class DeserializeData {
        
        private @NonNull Map<Integer, IdentityWrapper<CodeElement<?>>> idMapping;
        
        /**
         * Creates a new object for de-serialization.
         */
        public DeserializeData() {
            idMapping = new HashMap<>();
        }
        
        /**
         * Deserializes the given JSON back to a {@link CodeElement}.
         * 
         * @param element The JSON to de-serialize.
         * 
         * @return The resulting {@link CodeElement}.
         * 
         * @throws FormatException If the JSON does not contain the expected data.
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public @NonNull CodeElement<?> deserialize(@NonNull JsonElement element) throws FormatException {
            if (!(element instanceof JsonObject)) {
                throw new FormatException("Expected JsonObject, but got " + element.getClass().getSimpleName());
            }
            
            JsonObject json = (JsonObject) element;
            CodeElement result;
            
            String className = json.getString("class");
            try {
                Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
                
                Constructor<?> ctor = clazz.getDeclaredConstructor(JsonObject.class, CheckedFunction.class);
                ctor.setAccessible(true);
                CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction
                    = this::deserialize;
                result = notNull((CodeElement) ctor.newInstance(json, deserializeFunction));
                
            } catch (NoSuchMethodException e) {
                throw new FormatException(className + " does not implement a constructor with (JsonObject, Function) "
                        + "parameters for de-serialization", e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof FormatException) {
                    throw (FormatException) e.getCause();
                }
                throw new FormatException("Can't instantiate " + className, e);
                
            } catch (ReflectiveOperationException e) {
                throw new FormatException("Can't instantiate " + className, e);
            }
            
            int id = json.getInt("id");
            idMapping.put(id, new IdentityWrapper<>(result));
            
            if (json.getElement("nested") != null) {
                for (JsonElement nested : json.getList("nested")) {
                    result.addNestedElement(deserialize(nested));
                }
            }
            
            return result;
        }
        
        /**
         * Iterates over all de-serialized elements and calls their {@link CodeElement#resolveIds(Map)} methods.
         * This is the second step of de-serialization.
         * @throws FormatException If any of the {@link CodeElement#resolveIds(Map)} methods throws a
         *      {@link FormatException}.
         */
        public void resolveIds() throws FormatException {
            Map<Integer, CodeElement<?>> mapping = new HashMap<>((int) (idMapping.size() * 1.25));
            for (Map.Entry<Integer, IdentityWrapper<CodeElement<?>>> element : idMapping.entrySet()) {
                mapping.put(element.getKey(), element.getValue().getData());
            }
            mapping = Collections.unmodifiableMap(mapping);
            
            for (IdentityWrapper<CodeElement<?>> element : idMapping.values()) {
                element.getData().resolveIds(mapping);
            }
        }
        
    }
    
    /**
     * Deserializes the given JSON back to a {@link SourceFile}.
     * 
     * @param json The JSON data to deserialize.
     * 
     * @return The deserialized {@link SourceFile}.
     * 
     * @throws FormatException If the JSON does not contain the expected data.
     */
    private @NonNull SourceFile<CodeElement<?>> deserialize(@NonNull JsonElement json) throws FormatException {
        if (!(json instanceof JsonObject)) {
            throw new FormatException("Expected JsonObject, but got " + json.getClass().getSimpleName());
        }
        
        JsonObject jsonObj = (JsonObject) json;
        
        if (jsonObj.getInt("version") != VERSION) {
            throw new FormatException("Unsupported version: got " + jsonObj.getInt("version")
                    + ", but expected " + VERSION);
        }
        
        File path = new File(jsonObj.getString("path"));
        
        SourceFile<CodeElement<?>> result = new SourceFile<>(path);
        
        DeserializeData data = new DeserializeData();
        for (JsonElement nested : jsonObj.getList("elements")) {
            result.addElement(data.deserialize(nested));
        }
        
        data.resolveIds();
        
        return result;
    }

}
