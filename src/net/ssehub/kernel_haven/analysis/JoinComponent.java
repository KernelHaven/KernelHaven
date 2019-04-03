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

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A component to join multiple pipelines created by a {@link SplitComponent}. This must be the main analysis component.
 * 
 * @author Adam
 */
public class JoinComponent extends AnalysisComponent<Void> {

    private @NonNull AnalysisComponent<?> @NonNull [] inputs;
    
    /**
     * Creates a {@link JoinComponent} for the given input components.
     * 
     * @param config The global configuration.
     * @param inputs The input components. Not <code>null</code>.
     */
    public JoinComponent(@NonNull Configuration config, @NonNull AnalysisComponent<?> /*@NonNull*/ ... inputs) {
        // TODO: commented out @NonNull annotation because checkstyle can't parse it
        super(config);
        this.inputs = notNull(inputs);
    }
    
    /**
     * Returns the input components to join.
     * 
     * @return The input components.
     */
    @NonNull AnalysisComponent<?> @NonNull [] getInputs() {
        return inputs;
    }

    @Override
    protected void execute() {
    }

    @Override
    public @NonNull String getResultName() {
        return "JoinComponent";
    }
    
    @Override
    boolean isInternalHelperComponent() {
        return true;
    }

}
