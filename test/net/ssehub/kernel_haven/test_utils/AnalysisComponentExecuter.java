package net.ssehub.kernel_haven.test_utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.analysis.AnalysisComponent;
import net.ssehub.kernel_haven.config.Configuration;

/**
 * Contains a helper method to execute analysis components.
 * 
 * @author Adam
 */
public class AnalysisComponentExecuter {

    /**
     * Don't allow any instances.
     */
    private AnalysisComponentExecuter() {
    }

    /**
     * Creates and executes the given {@link AnalysisComponent} class.
     * 
     * @param componentClass The component class to create and execute.
     * @param config The {@link Configuration} for the component. <code>null</code> if no special settings are needed.
     * @param inputs The input values for the component. The first dimension represent the different
     *      {@link AnalysisComponent} inputs; the second dimension represent that values that each input component
     *      should create.
     *      
     * @param <T> The output type of the analysis component.
     *      
     * @return The list of results that the component created.
     */
    public static <T> List<T> executeComponent(Class<?> componentClass, Configuration config, Object[]... inputs) {
        
        // create an empty configuration is none is passed to us
        if (config == null) {
            try {
                config = new TestConfiguration(new Properties());
            } catch (SetUpException e) {
                throw new RuntimeException("Can't create test configuration", e);
            }
        }
        
        // create input components based on the inputs parameter
        AnalysisComponent<?>[] inputComponents = new AnalysisComponent[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            try {
                inputComponents[i] = new TestAnalysisComponentProvider<>(inputs[i]);
            } catch (SetUpException e) {
                throw new RuntimeException("Can't create test input component", e);
            }
        }
        
        // create an array with the parameter types, to get the right constructor
        Class<?>[] parameterTypes = new Class[1 + inputComponents.length];
        parameterTypes[0] = Configuration.class;
        for (int i = 0; i < inputComponents.length; i++) {
            parameterTypes[i + 1] = AnalysisComponent.class;
        }
        
        // create an array with the parameter values, to pass to the consturctor
        Object[] parameterValues = new Object[1 + inputComponents.length];
        parameterValues[0] = config;
        for (int i = 0; i < inputComponents.length; i++) {
            parameterValues[i + 1] = inputComponents[i];
        }
        
        List<T> result = new LinkedList<>();
        
        try {
            // instantiate the component
            @SuppressWarnings("unchecked")
            AnalysisComponent<T> component = (AnalysisComponent<T>)
                    componentClass.getConstructor(parameterTypes).newInstance(parameterValues);
            
            // execute the component
            T r;
            while ((r = component.getNextResult()) != null) {
                result.add(r);
            }
            
        } catch (ReflectiveOperationException | ClassCastException | SecurityException e) {
            throw new RuntimeException("Can't instantiate analysis component", e);
        }
        
        return result;
    }
    
}
