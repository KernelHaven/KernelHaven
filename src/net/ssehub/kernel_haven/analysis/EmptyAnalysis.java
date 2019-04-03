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

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * An empty analysis which does nothing but start all extractors. Logs extractor output and exceptions.
 *
 * @author Adam
 */
public class EmptyAnalysis extends AbstractAnalysis {

    /**
     * Creates a new empty analysis.
     * 
     * @param config The configuration.
     */
    public EmptyAnalysis(@NonNull Configuration config) {
        super(config);
    }
    
    @Override
    public void run() {
        try {
            cmProvider.start();
            bmProvider.start();
            vmProvider.start();
            
            // variability
            VariabilityModel vm = vmProvider.getResult();
            if (vm != null) {
                LOGGER.logInfo("Got a variability model with " + vm.getVariables().size() + " variables");
            } else {
                LOGGER.logInfo("Got no variability model");
            }
            ExtractorException vmExc = vmProvider.getException();
            if (vmExc != null) {
                LOGGER.logExceptionInfo("Got an exception from the variability model extractor", vmExc);
            }
            
            // build
            BuildModel bm = bmProvider.getResult();
            if (bm != null) {
                LOGGER.logInfo("Got a build model with " + bm.getSize() + " files");
            } else {
                LOGGER.logInfo("Got no build model");
            }
            ExtractorException bmExc = bmProvider.getException();
            if (bmExc != null) {
                LOGGER.logExceptionInfo("Got an exception from the build model extractor", bmExc);
            }
            
            // code
            int numCm = 0;
            SourceFile<?> result;
            do {
                result = cmProvider.getNextResult();
                if (result != null) {
                    numCm++;
                }
            } while (result != null);
            LOGGER.logInfo("Got " + numCm + " source files in the code model");
            
            ExtractorException cmExc;
            do {
                cmExc = cmProvider.getNextException();
                if (cmExc != null) {
                    LOGGER.logExceptionInfo("Got an exception from the code model extractor", cmExc);
                }
            } while (cmExc != null);
            
        } catch (SetUpException e) {
            LOGGER.logException("Exception while starting extractors", e);
        }
    }

}
