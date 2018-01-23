package net.ssehub.kernel_haven.build_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.logic.Disjunction;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the build model cache.
 *
 * @author Adam
 * @author Kevin
 */
public class BuildModelCacheTest {

    private File cacheDir;

    /**
     * Inits the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }

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
        originalBm.add(new File("dir/file.c"), new Disjunction(new Variable("CONFIG_A"),
                new Variable("CONFIG_A_MODULE")));
        originalBm.add(new File("dir/file2.c"), True.INSTANCE);
        

        BuildModelCache cache = new BuildModelCache(cacheDir);

        // write
        cache.write(originalBm);

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
        BuildModelCache cache = new BuildModelCache(new File("testdata/bmCaching/cache1"));
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
    public void testMalFormFormula() throws FormatException, IOException {
        BuildModelCache cache = new BuildModelCache(new File("testdata/bmCaching/cache2"));
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
        BuildModelCache cache = new BuildModelCache(new File("testdata/bmCaching/cache3"));
        BuildModel bm = cache.read(new File(""));
        
        assertThat(bm, nullValue());
    }
    
}
