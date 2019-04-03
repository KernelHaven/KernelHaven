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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A simple variability model extractor which returns an empty variability model.
 *
 * @author Adam
 */
public class EmptyVariabilityModelExtractor extends AbstractVariabilityModelExtractor {

    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
    }

    @Override
    protected @Nullable VariabilityModel runOnFile(@NonNull File target) throws ExtractorException {
        try {
            File constraintFile = notNull(File.createTempFile("empty_constraint_model", ".txt"));
            constraintFile.deleteOnExit();
            return new VariabilityModel(constraintFile, new HashMap<>());
        } catch (IOException e) {
            throw new ExtractorException(e);
        }
    }

    @Override
    protected @NonNull String getName() {
        return "EmptyVariabilityModelExtractor";
    }

}
