package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ssehub.kernel_haven.build_model.BuildModelDescriptor.KeyType;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonParser;
import net.ssehub.kernel_haven.util.io.json.JsonPrettyPrinter;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A cache for writing (and reading) a {@link BuildModel} to a file, using JSON.
 *
 * @author Adam
 */
public class JsonBuildModelCache extends AbstractCache<BuildModel> {

    private static final int VERSION = 2;
    
    private @NonNull File cacheFile;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir The directory where to store the cache files. This must be a directory, and we must be able to
     *      read and write to it.
     */
    public JsonBuildModelCache(@NonNull File cacheDir) {
        cacheFile = new File(cacheDir, "bmCache.json");
    }
    
    @Override
    public @Nullable BuildModel read(@NonNull File target) throws FormatException, IOException {
        JsonObject data = null;
        BuildModel result = null;
        
        try (JsonParser parser = new JsonParser(cacheFile)) {
            
            JsonElement parsed = parser.parse();
            if (!(parsed instanceof JsonObject)) {
                throw new FormatException("Expected JSON object, got " + parsed.getClass().getSimpleName());
            }
            
            data = (JsonObject) parsed;
            
        } catch (FileNotFoundException e) {
            // ignore and return null
        }
        
        if (data != null) {
            if (data.getInt("version") != VERSION) {
                throw new FormatException("Got invalid version " + data.getInt("version") + ", we only support "
                        + VERSION);
            }
            
            result = new BuildModel();
            
            result.setDescriptor(jsonToDescriptor(data.getObject("descriptor")));
            
            jsonToPcs(data.getObject("presenceConditions"), result);
        }
        
        return result;
    }
    
    /**
     * Reads the {@link BuildModelDescriptor} from the given JSON.
     * 
     * @param json The JSON object that stores the {@link BuildModelDescriptor}.
     * 
     * @return The converted {@link BuildModelDescriptor}.
     * 
     * @throws FormatException If the JSON is malformed.
     */
    private @NonNull BuildModelDescriptor jsonToDescriptor(@NonNull JsonObject json) throws FormatException {
        BuildModelDescriptor result = new BuildModelDescriptor();
        
        try {
            result.setKeyType(KeyType.valueOf(json.getString("keyType")));
        } catch (IllegalArgumentException e) {
            throw new FormatException(e);
        }
        
        return result;
    }
    
    /**
     * Converts the given JSON object back into presence conditions.
     * 
     * @param json The JSON to convert.
     * @param result The {@link BuildModel} to add the result to.
     * 
     * @throws FormatException If JSON is malformed.
     */
    private void jsonToPcs(@NonNull JsonObject json, @NonNull BuildModel result) throws FormatException {
        VariableCache cache = new VariableCache();
        Parser</*@NonNull*/ Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));
        /*
         * TODO: commented out annotations
         * The commented out annotations trigger a bug in the javac compiler:
         * https://bugs.openjdk.java.net/browse/JDK-8144185
         * 
         * This causes the instrumentation of jacoco (for test coverage) to fail:
         * https://github.com/jacoco/jacoco/issues/585
         */
        
        for (Map.Entry<String, JsonElement> entry : json) {
            if (!(entry.getValue() instanceof JsonString)) {
                throw new FormatException("Expected JsonString, but got "
                        + entry.getValue().getClass().getSimpleName());
            }
            
            String pcStr = ((JsonString) entry.getValue()).getValue();
            
            Formula pc;
            try {
                pc = notNull(parser.parse(pcStr));
            } catch (ExpressionFormatException e) {
                throw new FormatException(e);
            }
            
            result.add(new File(entry.getKey()), pc);
        }
    }

    @Override
    public void write(@NonNull BuildModel bm) throws IOException {
        JsonObject mainJson = new JsonObject();
        
        mainJson.putElement("version", new JsonNumber(VERSION));
        
        mainJson.putElement("descriptor", descriptorToJson(bm.getDescriptor()));
        mainJson.putElement("presenceConditions", pcsToJson(bm));
        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(cacheFile))) {
            out.write(mainJson.accept(new JsonPrettyPrinter()));
        }
    }
    
    /**
     * Converts the given {@link BuildModelDescriptor} to a JSON object.
     * 
     * @param descriptor The descriptor to convert.
     * 
     * @return A JSON representation of the descriptor.
     */
    private @NonNull JsonObject descriptorToJson(@NonNull BuildModelDescriptor descriptor) {
        JsonObject result = new JsonObject();
        
        result.putElement("keyType", new JsonString(notNull(descriptor.getKeyType().name())));
        
        return result;
    }
    
    /**
     * Converts the presence conditions for all files to a JSON object.
     * 
     * @param bm The {@link BuildModel} to read the PCs from.
     * 
     * @return A JSON representation of the PCs.
     */
    private @NonNull JsonObject pcsToJson(@NonNull BuildModel bm) {
        JsonObject result = new JsonObject();
        
        List<@NonNull File> files = new ArrayList<>(bm.getSize());
        bm.forEach(files::add);
        
        files.stream()
                .sorted((f1, f2) -> f1.getPath().replace(File.separatorChar, '/').compareTo(
                        f2.getPath().replace(File.separatorChar, '/')))
                .forEach((file) -> {
                    result.putElement(notNull(file.getPath().replace(File.separatorChar, '/')),
                            new JsonString(notNull(bm.getPcDirect(file)).toString()));
                });
        
        return result;
    }

}
