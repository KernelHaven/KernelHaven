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

import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.analysis.AnalysisTestHelperWrapper;
import net.ssehub.kernel_haven.util.BlockingQueue;

/**
 * An analysis component that provides static data. Useful as input for testing other components.
 * 
 * @param <T> The type of data to create.
 * 
 * @author Adam
 */
@SuppressWarnings("null")
public class TestAnalysisComponentProvider<T> extends AnalysisTestHelperWrapper<T> {

    private BlockingQueue<T> data;
    
    private String name;
    
    /**
     * Creates a new instance with the given data.
     * 
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    public TestAnalysisComponentProvider(Iterable<T> data) throws SetUpException {
        super(new TestConfiguration(new Properties()));
        this.data = new BlockingQueue<>();
        for (T t : data) {
            this.data.add(t);
        }
        this.data.end();
        this.name = "TestComponent";
    }
    
    /**
     * Creates a new instance with the given data.
     * 
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    @SafeVarargs
    public TestAnalysisComponentProvider(T... data) throws SetUpException {
        this("TestComponent", data);
    }
    
    /**
     * Creates a new instance with the given data.
     * 
     * @param name The name that {@link #getResultName()} should return.
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    @SafeVarargs
    public TestAnalysisComponentProvider(String name, T... data) throws SetUpException {
        super(new TestConfiguration(new Properties()));
        this.data = new BlockingQueue<>();
        for (T t : data) {
            this.data.add(t);
        }
        this.data.end();
        this.name = name;
    }
        @Override
    protected void execute() {
        T element;
        while ((element = this.data.get()) != null) {
            addResult(element);
        }
    }

    @Override
    public String getResultName() {
        return name;
    }
    
}
