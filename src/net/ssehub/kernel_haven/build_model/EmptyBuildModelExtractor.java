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
package net.ssehub.kernel_haven.build_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A simple extractor that returns an empty build model.
 *
 * @author Adam
 */
public class EmptyBuildModelExtractor extends AbstractBuildModelExtractor {

    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
    }

    @Override
    protected @NonNull BuildModel runOnFile(@NonNull File target) throws ExtractorException {
        return new BuildModel();
    }

    @Override
    protected @NonNull String getName() {
        return "EmptyBuildModelExtractor";
    }

}
