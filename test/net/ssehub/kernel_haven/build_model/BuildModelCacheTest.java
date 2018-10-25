package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.ssehub.kernel_haven.build_model.BuildModel.KeyType;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.logic.True;

/**
 * Tests the build model cache.
 *
 * @author Adam
 * @author Kevin
 */
@SuppressWarnings("null")
public class BuildModelCacheTest {

    private File cacheDir;

    /**
     * Creates the cache directory for each test.
     */
    @Before
    public void setUp() {
        cacheDir = new File("testdata/tmp_cache");
        cacheDir.mkdir();
        assertThat(cacheDir.isDirectory(), is(true));
    }

    /**
     * Deletes the cache directory after each test.
     * 
     * @throws IOException
     *             unwanted.
     */
    @After
    public void tearDown() throws IOException {
        Util.deleteFolder(cacheDir);
    }

    /**
     * Caching test. Is Caching a BuildModel Object and comparing it to the cached one
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCaching() throws IOException, FormatException {
        BuildModel originalBm = new BuildModel();
        originalBm.add(new File("dir/file.c"), or("CONFIG_A", "CONFIG_A_MODULE"));
        originalBm.add(new File("dir/file2.c"), True.INSTANCE);
        

        JsonBuildModelCache cache = new JsonBuildModelCache(cacheDir);

        // write
        cache.write(originalBm);
        
        System.out.println(Util.readStream(new FileInputStream(new File(cacheDir, "bmCache.json"))));
        
        // read
        BuildModel readBm = cache.read(new File(""));

        // check if equal
        assertThat(readBm.getSize(), is(originalBm.getSize()));
        assertThat(readBm.getPc(new File("dir/file.c")), is(originalBm.getPc(new File("dir/file.c"))));
        
        assertThat(readBm.getPc(new File("dir/file2.c")), is(originalBm.getPc(new File("dir/file2.c"))));
    }

    /**
     * Tests if an invalid cache file correctly throws an
     * {@link FormatException} with an invalid CSV.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testMalFormCsv() throws FormatException, IOException {
        JsonBuildModelCache cache = new JsonBuildModelCache(new File("testdata/bmCaching/cache_json_malformed"));
        cache.read(new File(""));
    }
    
    /**
     * Tests if an invalid cache file correctly throws an
     * {@link FormatException} with an invalid formula.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testMalformedFormula() throws FormatException, IOException {
        JsonBuildModelCache cache = new JsonBuildModelCache(new File("testdata/bmCaching/cache_formula_malformed"));
        cache.read(new File(""));
    }
    
    /**
     * Tests if the cache correctly returns <code>null</code> on empty cache.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test()
    public void testEmptyCache() throws FormatException, IOException {
        JsonBuildModelCache cache = new JsonBuildModelCache(new File("testdata/bmCaching/cache3"));
        BuildModel bm = cache.read(new File(""));
        
        assertThat(bm, nullValue());
    }
    
    /**
     * Tests that the {@link KeyType} is cached correctly.
     * 
     * @throws IOException unwanted.
     * @throws FormatException unwanted.
     */
    @Test
    public void testKeyType() throws IOException, FormatException {
        JsonBuildModelCache cache = new JsonBuildModelCache(cacheDir);
        BuildModel originalBm = new BuildModel();
        
        originalBm.setKeyType(KeyType.DIRECTORY);
        cache.write(originalBm);
        BuildModel readBm = cache.read(new File(""));
        assertThat(readBm.getKeyType(), is(KeyType.DIRECTORY));
        
        originalBm.setKeyType(KeyType.FILE);
        cache.write(originalBm);
        readBm = cache.read(new File(""));
        assertThat(readBm.getKeyType(), is(KeyType.FILE));
        
        originalBm.setKeyType(KeyType.FILE_AND_DIRECTORY);
        cache.write(originalBm);
        readBm = cache.read(new File(""));
        assertThat(readBm.getKeyType(), is(KeyType.FILE_AND_DIRECTORY));
    }
    
}
