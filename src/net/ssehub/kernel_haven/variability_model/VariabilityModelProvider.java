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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * The provider for the variability model. This class serves as an intermediate between the analysis and the
 * variability model extractor.
 *
 * @author Adam
 */
public class VariabilityModelProvider extends AbstractProvider<VariabilityModel> {

    @Override
    protected long getTimeout() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_TIMEOUT);
    }
    
    @Override
    protected @NonNull List<@NonNull File> getTargets() {
        List<@NonNull File> result = new ArrayList<>(1);
        result.add(config.getValue(DefaultSettings.SOURCE_TREE));
        return result;
    }

    @Override
    protected @NonNull AbstractCache<VariabilityModel> createCache() {
        return new JsonVariabilityModelCache(config.getValue(DefaultSettings.CACHE_DIR));
    }

    @Override
    public boolean readCache() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_READ);
    }

    @Override
    public boolean writeCache() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_WRITE);
    }

    @Override
    public int getNumberOfThreads() {
        return 1;
    }

}
