/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonParser;
import net.ssehub.kernel_haven.util.io.json.JsonPrettyPrinter;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.Attribute;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.ConstraintFileType;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.VariableType;

/**
 * A cache for permanently saving (and reading) a {@link VariabilityModel} to a file. Uses JSON for data representation.
 * 
 * @author Adam
 */
public class JsonVariabilityModelCache extends AbstractCache<VariabilityModel> {

    private static final int VERSION = 5;
    
    private @NonNull File cacheFile;
    
    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir The directory where to store the cache files. This must be a directory, and we must be able to
     *      read and write to it.
     */
    public JsonVariabilityModelCache(@NonNull File cacheDir) {
        this.cacheFile = new File(cacheDir, "vmCache.json");
    }
    
    @Override
    public @Nullable VariabilityModel read(@NonNull File target) throws FormatException, IOException {
        JsonObject data = null;
        VariabilityModel result = null;
        
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
            
            VariabilityModelDescriptor descriptor = readDescriptor(data.getObject("descriptor"));
            // TODO: removed null annotations because jacoco report fails with it
            Map</*@NonNull*/ String, VariabilityVariable> vars = readVariables(data.getList("variables"));
            
            File constraintCopy = File.createTempFile("constraintModel", "");
            constraintCopy.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(constraintCopy)) {
                Util.copyStream(new ByteArrayInputStream(data.getString("constraintModel").getBytes()), out);
            }
            
            @SuppressWarnings("null") // TODO: null annotation missing, see above
            VariabilityModel tmp = new VariabilityModel(constraintCopy, vars);
            tmp.setDescriptor(descriptor);
            result = tmp;
        }
        
        return result;
    }

    /**
     * Reads the {@link VariabilityModelDescriptor} from the given DIMACS.
     * 
     * @param data The JSON to read.
     * 
     * @return The read {@link VariabilityModelDescriptor}.
     * 
     * @throws FormatException If reading the {@link VariabilityModelDescriptor} fails.
     */
    private @NonNull VariabilityModelDescriptor readDescriptor(@NonNull JsonObject data) throws FormatException {
        VariabilityModelDescriptor desriptor = new VariabilityModelDescriptor();

        try {
            desriptor.setVariableType(VariableType.valueOf(data.getString("variableType")));
            desriptor.setConstraintFileType(ConstraintFileType.valueOf(data.getString("constraintFileType")));
            
            for (JsonElement element : data.getList("attributes")) {
                if (!(element instanceof JsonString)) {
                    throw new FormatException("Expected JsonString, but got " + element.getClass().getSimpleName());
                }
                
                desriptor.addAttribute(Attribute.valueOf(((JsonString) element).getValue()));
            }
            
        } catch (IllegalArgumentException e) {
            throw new FormatException(e);
        }
        
        return desriptor;
    }
    
    /**
     * Reads and initializes the variables stored in the given JSON object.
     * 
     * @param data The JSON to read from.
     * 
     * @return The read and initialized variables; a mapping of variable name -&gt; variable.
     * 
     * @throws FormatException If reading the variables fails.
     */
    private @NonNull Map<@NonNull String, VariabilityVariable> readVariables(@NonNull JsonList data)
            throws FormatException {
        
        Map<@NonNull String, VariabilityVariable> vars = new HashMap<>();
        
        /*
         * Round 1: Instantiate all variables; this sets name and type
         */
        
        for (JsonElement element : data) {
            if (!(element instanceof JsonObject)) {
                throw new FormatException("Expected variable JsonObject, but got "
                        + element.getClass().getSimpleName());
            }
            JsonObject obj = (JsonObject) element;
            
            try {
                @SuppressWarnings("unchecked")
                Class<? extends VariabilityVariable> clazz =
                        (Class<? extends VariabilityVariable>) ClassLoader.getSystemClassLoader().loadClass(
                                obj.getString("class"));
                
                VariabilityVariable var = notNull(clazz.getConstructor(String.class, String.class)
                        .newInstance(obj.getString("name"), obj.getString("type")));
                
                vars.put(obj.getString("name"), var);
                
            } catch (InvocationTargetException e) {
                throw new FormatException(e.getTargetException());
                
            } catch (ReflectiveOperationException e) {
                throw new FormatException(e);
            } 
            
        }
        
        /*
         * Round 2: Pass through all instantiated variables and call setJsonData
         */
        
        for (JsonElement element : data) {
            if (!(element instanceof JsonObject)) {
                throw new FormatException("Expected variable JsonObject, but got "
                        + element.getClass().getSimpleName());
            }
            JsonObject obj = (JsonObject) element;
            
            VariabilityVariable var = vars.get(obj.getString("name"));
            var.setJsonData(obj, vars);
        }
        
        return vars;
    }

    @Override
    public void write(@NonNull VariabilityModel result) throws IOException {
        JsonObject mainJson = new JsonObject();
        
        mainJson.putElement("version", new JsonNumber(VERSION));
        mainJson.putElement("descriptor", descriptorToJson(result.getDescriptor()));
        mainJson.putElement("variables", variablesToJson(result.getVariables()));
        mainJson.putElement("constraintModel",
                new JsonString(Util.readStream(new FileInputStream(result.getConstraintModel()))));
        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(cacheFile))) {
            out.write(mainJson.accept(new JsonPrettyPrinter()));
        }
    }
    
    /**
     * Turns the {@link VariabilityModelDescriptor} into a {@link JsonObject}.
     * 
     * @param descriptor The descriptor to convert.
     * 
     * @return The JSON representation of the descriptor.
     */
    private @NonNull JsonElement descriptorToJson(@NonNull VariabilityModelDescriptor descriptor) {
        JsonObject result = new JsonObject();
        
        result.putElement("variableType", new JsonString(notNull(descriptor.getVariableType().name())));
        result.putElement("constraintFileType", new JsonString(notNull(descriptor.getConstraintFileType().name())));
        
        JsonList attributes = new JsonList();
        for (Attribute a : descriptor.getAttributes()) {
            attributes.addElement(new JsonString(notNull(a.name())));
        }
        result.putElement("attributes", attributes);
        
        return result;
    }
    
    /**
     * Turns the given {@link VariabilityVariable}s into a {@link JsonList} of {@link JsonObject}s.
     * 
     * @param variables The variables to convert.
     * 
     * @return The JSON representation of the variables.
     */
    private @NonNull JsonElement variablesToJson(@NonNull Set<@NonNull VariabilityVariable> variables) {
        List<@NonNull VariabilityVariable> sorted = new ArrayList<>(variables);
        sorted.sort((v1, v2) -> v1.getName().compareTo(v2.getName()));
        
        JsonList result = new JsonList();
        
        for (VariabilityVariable var : sorted) {
            JsonObject json = var.toJson();
            json.putElement("class", new JsonString(notNull(var.getClass().getName())));
            result.addElement(json);
        }
        
        return result;
    }

}
