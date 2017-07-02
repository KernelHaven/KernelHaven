package net.ssehub.kernel_haven.build_model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.BuildExtractorConfiguration;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;

/**
 * The provider for the build model. This class serves as an intermediate between the analysis and the build model
 * extractor.
 *
 * @author Adam
 */
public class BuildModelProvider extends AbstractProvider<BuildModel, BuildExtractorConfiguration> {

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
    public AbstractCache<BuildModel> createCache() {
        return new BuildModelCache(config.getCacheDir());
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
