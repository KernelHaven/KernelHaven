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
package net.ssehub.kernel_haven.analysis;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A component that collects all results from the previous component into one list.
 * 
 * @param <T> The type of result to collect into one list.
 * 
 * @author Adam
 */
public class ListCollectorComponent<T> extends AnalysisComponent<List<T>> {

    private @NonNull AnalysisComponent<T> previousComponent;
    
    /**
     * Creates anew {@link ListCollectorComponent} for the given previous component.
     * 
     * @param config The global configuration.
     * @param previousComponent The previous component
     */
    public ListCollectorComponent(@NonNull Configuration config, @NonNull AnalysisComponent<T> previousComponent) {
        super(config);
        this.previousComponent = previousComponent;
    }

    @Override
    protected void execute() {
        List<T> collected = new LinkedList<>();
        
        T result;
        while ((result = previousComponent.getNextResult()) != null) {
            collected.add(result);
        }
        
        addResult(collected);
    }
    
    @Override
    public @NonNull String getResultName() {
        return previousComponent.getResultName() + " List";
    }

}
