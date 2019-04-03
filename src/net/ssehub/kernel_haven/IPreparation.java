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
package net.ssehub.kernel_haven;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A preparation that is executed before the analysis or extractors start. This preparation is created after the
 * providers and extractors are set up, but before the analysis is instantiated. This preparation may start extractors.
 * 
 * <p>TODO: this will change in the future.</p>
 *
 * @author Adam
 */
public interface IPreparation {

    /**
     * Executes this preparation.
     * 
     * @param config The global pipeline configuration.
     * 
     * @throws SetUpException If the preparation fails.
     */
    public void run(@NonNull Configuration config) throws SetUpException;
    
}
