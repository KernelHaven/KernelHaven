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

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.test_utils.TestAnalysisComponentProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A helper class to make {@link TestAnalysisComponentProvider} an internal helper component.
 * 
 * @author Adam
 *
 * @param <T> The output type of this {@link AnalysisComponent}.
 */
public abstract class AnalysisTestHelperWrapper<T> extends AnalysisComponent<T> {

    /**
     * Creates this {@link AnalysisComponent} with the given configuration.
     * 
     * @param config The pipeline configuration.
     */
    public AnalysisTestHelperWrapper(@NonNull Configuration config) {
        super(config);
    }

    @Override
    boolean isInternalHelperComponent() {
        return true;
    }
    
}
