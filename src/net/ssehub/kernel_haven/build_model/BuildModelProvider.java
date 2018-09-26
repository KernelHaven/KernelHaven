package net.ssehub.kernel_haven.build_model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * The provider for the build model. This class serves as an intermediate between the analysis and the build model
 * extractor.
 *
 * @author Adam
 */
public class BuildModelProvider extends AbstractProvider<BuildModel> {

    @Override
    protected long getTimeout() {
        return config.getValue(DefaultSettings.BUILD_PROVIDER_TIMEOUT);
    }
    
    @Override
    protected @NonNull List<@NonNull File> getTargets() {
        List<@NonNull File> result = new ArrayList<>(1);
        result.add(config.getValue(DefaultSettings.SOURCE_TREE));
        return result;
    }

    @Override
    public @NonNull AbstractCache<BuildModel> createCache() {
        return new BuildModelCache(config.getValue(DefaultSettings.CACHE_DIR));
    }

    @Override
    public boolean readCache() {
        return config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_READ);
    }

    @Override
    public boolean writeCache() {
        return config.getValue(DefaultSettings.BUILD_PROVIDER_CACHE_WRITE);
    }

    @Override
    public int getNumberOfThreads() {
        return 1;
    }

}
