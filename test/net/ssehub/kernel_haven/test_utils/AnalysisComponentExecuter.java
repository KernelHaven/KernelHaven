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
package net.ssehub.kernel_haven.test_utils;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.analysis.AnalysisComponent;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

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
     * <p>
     * <b>Example usage</b>
     * <p>
     * Assuming that DummComponent is an AnalysisComponent, which returns Doubles and takes two input components,
     * one for Strings and one for Integers, and looks roughly like this:
     * <pre>
     * public class DummyCoponent extends AnalysisComponent&lt;Double&gt; {
     *     public DummyCopmonent(Configuration config, AnalysisComponent&lt;String&gt; input1,
     *             AnalysisComponent&lt;Integer&gt; input2) {
     *         ...
     *     }
     *     ...
     * }
     * </pre>
     * 
     * A call to {@link #executeComponent(Class, Configuration, Object[][])} for this class would look like this:
     * <pre>
     * List&lt;Double&gt; result = AnalysisComponentExecuter.executeComponent(
     *         DummyComponent.class, null,
     *         new Object[] {
     *             "Some", "Values", "Here"
     *         },
     *         new Object[] {
     *             1, 2, 3
     *         }
     * );
     * </pre>
     * 
     * This would create two {@link TestAnalysisComponentProvider}, one for the three string values and one for the
     * three int values. It would then instantiate DummyComponent with an empty configuration and these two input
     * components. After this, it executes the DummyComponent and returns all the results as an array.
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
    public static <T> @NonNull List<T> executeComponent(Class<?> componentClass, Configuration config,
            Object[]... inputs) {
        
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
                    notNull(componentClass.getConstructor(parameterTypes).newInstance(parameterValues));
            
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
