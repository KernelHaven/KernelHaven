package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A factory for creating {@link IVariabilityVariableSerializer} for (sub-) classes of {@link VariabilityVariable}.
 * 
 * @author Adam
 */
public class VariabilityVariableSerializerFactory {

    /**
     * The global singleton instance.
     */
    public static final VariabilityVariableSerializerFactory INSTANCE = new VariabilityVariableSerializerFactory();
    
    static {
        INSTANCE.registerSerializer(notNull(VariabilityVariable.class.getName()),
                new VariabilityVariableSerializer());
        
        INSTANCE.registerSerializer(notNull(HierarchicalVariable.class.getName()),
                new HierarchicalVariableSerializer());
    }
    
    private @NonNull Map<@NonNull String, VariabilityVariableSerializer> registeredSerializers;
    
    /**
     * Don't allow any instances other than the singleton.
     */
    private VariabilityVariableSerializerFactory() {
        registeredSerializers = new HashMap<>();
    }
    
    /**
     * Registers a serializer for a given class name. Overrides any previously registered serializer for that class.
     * 
     * @param className The class name that the serializer handles.
     * @param serializer The serializer that handles the class name.
     */
    public void registerSerializer(@NonNull String className, @NonNull VariabilityVariableSerializer serializer) {
        
        registeredSerializers.put(className, serializer);
    }
    
    /**
     * Returns a serializer for the given class.
     * 
     * @param className The class name of the (sub-) class of {@link VariabilityVariable} to serialize.
     * 
     * @return A serializer for the given class.
     * 
     * @throws IllegalArgumentException If no such serializer has been registered.
     */
    public @NonNull VariabilityVariableSerializer getSerializer(@NonNull String className)
            throws IllegalArgumentException {
        
        VariabilityVariableSerializer serializer = registeredSerializers.get(className);
        
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer registered for class " + className);
        }
        
        return serializer;
    }
    
}
