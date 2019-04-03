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
package net.ssehub.kernel_haven.code_model;

import java.io.File;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A simple code model extractor that returns empty source files for each target to run on.
 *
 * @author Adam
 */
public class EmptyCodeModelExtractor extends AbstractCodeModelExtractor {

    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
    }

    @Override
    protected @Nullable SourceFile<CodeBlock> runOnFile(@NonNull File target) throws ExtractorException {
        return new SourceFile<CodeBlock>(target);
    }

    @Override
    protected @NonNull String getName() {
        return "EmptyCodeModelExtractor";
    }

}
