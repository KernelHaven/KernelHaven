package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.ssehub.kernel_haven.code_model.ast.AllAstTests;
import net.ssehub.kernel_haven.code_model.ast.ISyntaxElement;
import net.ssehub.kernel_haven.code_model.simple_ast.SyntaxElement;
import net.ssehub.kernel_haven.code_model.simple_ast.SyntaxElementTypes;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the {@link JsonCodeModelCache}.
 *
 * @author Adam
 */
@SuppressWarnings("null")
public class JsonCodeModelCacheTest {

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
     * Writes and reads a code model consisting of {@link CodeBlock}s to the cache, and asserts that contents are equal.
     * 
     * @throws IOException unwanted.
     * @throws FormatException unwanted.
     */
    @Test
    public void testBlockCaching() throws IOException, FormatException {
        File location = new File("test.c");
        SourceFile<CodeBlock> originalSourceFile = new SourceFile<>(location);
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        CodeBlock block1 = new CodeBlock(1, 2, new File("file"), a, a);
        CodeBlock block2 = new CodeBlock(3, 15, new File("file"), new Negation(a), new Negation(a));
        CodeBlock block21 = new CodeBlock(4, 5, new File("file"), b, new Conjunction(b, new Negation(a)));
        block2.addNestedElement(block21);

        originalSourceFile.addElement(block1);
        originalSourceFile.addElement(block2);

        JsonCodeModelCache cache = new JsonCodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);
        
        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.hasNext(), is(false));
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
    public void testInvalidClass() throws FormatException, IOException {
        JsonCodeModelCache cache = new JsonCodeModelCache(new File("testdata/cmCaching/cache_invalid_class"));
        cache.read(new File("test.c"));
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
        JsonCodeModelCache cache = new JsonCodeModelCache(new File("testdata/cmCaching/cache_malformed_formula"));
        cache.read(new File("test.c"));
    }

    /**
     * Tests if the cache correctly returns <code>null</code> on empty cache.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testEmptyCache() throws FormatException, IOException {
        JsonCodeModelCache cache = new JsonCodeModelCache(new File("testdata/bmCaching/cache_empty"));
        SourceFile<?> result = cache.read(new File("test.c"));

        assertThat(result, nullValue());
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
    public void testInvalidJson() throws FormatException, IOException {
        JsonCodeModelCache cache = new JsonCodeModelCache(new File("testdata/cmCaching/cache_invalid_format"));
        cache.read(new File("test.c"));
    }

    /**
     * Tests the code model cache for SyntaxElements.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    @Ignore // TODO: not yet implemented
    public void testSyntaxElementCaching() throws IOException, FormatException {
        File location = new File("test.c");
        SourceFile<SyntaxElement> originalSourceFile = new SourceFile<>(location);
        Variable a = new Variable("A");
        Variable b = new Variable("B");

        SyntaxElement element1 = new SyntaxElement(SyntaxElementTypes.COMPOUND_STATEMENT, a, a);
        element1.setSourceFile(location);
        element1.setLineStart(1);
        element1.setLineEnd(1);

        SyntaxElement element11 = new SyntaxElement(SyntaxElementTypes.EXPR_STATEMENT, True.INSTANCE, a);
        element11.setSourceFile(location);
        element11.setLineStart(1);
        element11.setLineEnd(1);
        element1.addNestedElement(element11, "Statement");

        SyntaxElement element2 = new SyntaxElement(SyntaxElementTypes.FUNCTION_CALL, b, new Negation(b));
        element11.setSourceFile(location);
        element11.setLineStart(3);
        element11.setLineEnd(4);

        originalSourceFile.addElement(element1);
        originalSourceFile.addElement(element2);

        JsonCodeModelCache cache = new JsonCodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile<SyntaxElement> readSourceFile = cache.read(location).castTo(SyntaxElement.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<SyntaxElement> originalIt = originalSourceFile.iterator();
        Iterator<SyntaxElement> readIt = readSourceFile.iterator();

        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.hasNext(), is(false));
    }

    /**
     * Tests caching of compression is turned on.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCachingCompressed() throws IOException, FormatException {
        File location = new File("test.c");
        SourceFile<CodeBlock> originalSourceFile = new SourceFile<>(location);
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        CodeBlock block1 = new CodeBlock(1, 2, new File("file"), a, a);
        CodeBlock block2 = new CodeBlock(3, 15, new File("file"), new Negation(a), new Negation(a));
        CodeBlock block21 = new CodeBlock(4, 5, new File("file"), b, new Conjunction(b, new Negation(a)));
        block2.addNestedElement(block21);

        originalSourceFile.addElement(block1);
        originalSourceFile.addElement(block2);

        JsonCodeModelCache cache = new JsonCodeModelCache(cacheDir, true);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.hasNext(), is(false));
    }
    
    /**
     * Tests reading a compressed cache if compression is turned off.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCachingReadCompressed() throws IOException, FormatException {
        File location = new File("test.c");
        SourceFile<CodeBlock> originalSourceFile = new SourceFile<>(location);
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        CodeBlock block1 = new CodeBlock(1, 2, new File("file"), a, a);
        CodeBlock block2 = new CodeBlock(3, 15, new File("file"), new Negation(a), new Negation(a));
        CodeBlock block21 = new CodeBlock(4, 5, new File("file"), b, new Conjunction(b, new Negation(a)));
        block2.addNestedElement(block21);

        originalSourceFile.addElement(block1);
        originalSourceFile.addElement(block2);

        JsonCodeModelCache cache = new JsonCodeModelCache(new File("testdata/cmCaching/cache_compressed"), false);

        // don't write, the directory already contains the valid cache
        
        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.hasNext(), is(false));
    }
    
    /**
     * Writes and reads a code model consisting of {@link ISyntaxElement}s to the cache, and asserts that contents
     * are equal.
     * 
     * @throws IOException unwanted.
     * @throws FormatException unwanted.
     */
    @Test
    public void testAstCaching() throws IOException, FormatException {
        ISyntaxElement element = AllAstTests.createFullAst();

        File location = new File("test.c");
        SourceFile<ISyntaxElement> originalSourceFile = new SourceFile<>(location);
        originalSourceFile.addElement(element);

        JsonCodeModelCache cache = new JsonCodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);
        
        // read
        SourceFile<ISyntaxElement> readSourceFile = cache.read(location).castTo(ISyntaxElement.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<ISyntaxElement> originalIt = originalSourceFile.iterator();
        Iterator<ISyntaxElement> readIt = readSourceFile.iterator();
        
        assertThat(readIt.next(), is(originalIt.next()));
        assertThat(readIt.hasNext(), is(false));
    }

}
