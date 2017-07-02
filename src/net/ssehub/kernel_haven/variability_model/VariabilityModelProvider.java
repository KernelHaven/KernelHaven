package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;

/**
 * The provider for the variability model. This class serves as an intermediate between the analysis and the
 * variability model extractor.
 *
 * @author Adam
 */
public class VariabilityModelProvider extends AbstractProvider<VariabilityModel, VariabilityExtractorConfiguration> {

    @Override
    protected long getTimeout() {
        return config.getProviderTimeout();
    }
    
    @Override
    protected List<File> getTargets() {
        List<File> result = new ArrayList<>(1);
        result.add(config.getSourceTree());
        return result;
    }

    @Override
    protected AbstractCache<VariabilityModel> createCache() {
        return new VariabilityModelCache(config.getCacheDir());
    }

    @Override
    public boolean readCache() {
        return config.isCacheRead();
    }

    @Override
    public boolean writeCache() {
        return config.isCacheWrite();
    }

    @Override
    public int getNumberOfThreads() {
        return 1;
    }

}