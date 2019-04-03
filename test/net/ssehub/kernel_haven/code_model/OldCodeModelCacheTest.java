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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.ssehub.kernel_haven.code_model.simple_ast.SyntaxElement;
import net.ssehub.kernel_haven.code_model.simple_ast.SyntaxElementTypes;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the code model cache.
 *
 * @author Adam
 * @author Alice
 */
@SuppressWarnings({"null", "deprecation"})
public class OldCodeModelCacheTest {

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
     * Caching test. Is Caching a BuildModel Object and comparing it to the
     * cached one
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCaching() throws IOException, FormatException {
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

        OldCodeModelCache cache = new OldCodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertElementEqual(readIt.next(), originalIt.next());
        assertElementEqual(readIt.next(), originalIt.next());
        assertThat(readIt.hasNext(), is(false));
    }

    /**
     * Asserts that both elements are equal. Recursively checks child elements, too.
     * 
     * @param actual
     *            The actual value.
     * @param expected
     *            The expected value.
     */
    private void assertElementEqual(CodeElement<CodeBlock> actual, CodeElement<CodeBlock> expected) {
        assertThat(actual.getClass(), is((Object) expected.getClass()));
        assertThat(actual.getLineStart(), is(expected.getLineStart()));
        assertThat(actual.getLineEnd(), is(expected.getLineEnd()));
        assertThat(actual.getCondition(), is(expected.getCondition()));
        assertThat(actual.getPresenceCondition(), is(expected.getPresenceCondition()));
        assertThat(actual.getNestedElementCount(), is(expected.getNestedElementCount()));

        Iterator<CodeBlock> actualIt = actual.iterator();
        Iterator<CodeBlock> expectedIt = expected.iterator();

        while (expectedIt.hasNext()) {
            assertThat(actualIt.hasNext(), is(true));

            CodeBlock actualChild = actualIt.next();
            CodeBlock expectedChild = expectedIt.next();
            assertElementEqual(actualChild, expectedChild);
        }
        assertThat(actualIt.hasNext(), is(false));
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
        OldCodeModelCache cache = new OldCodeModelCache(new File("testdata/cmCaching/cache_invalid_class"));
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
        OldCodeModelCache cache = new OldCodeModelCache(new File("testdata/cmCaching/cache_malformed_formula"));
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
    @Test()
    public void testEmptyCache() throws FormatException, IOException {
        OldCodeModelCache cache = new OldCodeModelCache(new File("testdata/bmCaching/cache_empty"));
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
    public void testInvalidCsv() throws FormatException, IOException {
        OldCodeModelCache cache = new OldCodeModelCache(new File("testdata/cmCaching/cache_invalid_format"));
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

        OldCodeModelCache cache = new OldCodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile<SyntaxElement> readSourceFile = cache.read(location).castTo(SyntaxElement.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<SyntaxElement> originalIt = originalSourceFile.iterator();
        Iterator<SyntaxElement> readIt = readSourceFile.iterator();

        assertSyntaxElementEqual(readIt.next(), originalIt.next());
        assertSyntaxElementEqual(readIt.next(), originalIt.next());
        assertThat(readIt.hasNext(), is(false));
    }

    /**
     * Asserts that both syntax elements are equal. Recursively checks child
     * elements, too.
     * 
     * @param actual
     *            The actual value.
     * @param expected
     *            The expected value.
     */
    public static void assertSyntaxElementEqual(SyntaxElement actual, SyntaxElement expected) {
        assertThat(actual.getClass(), is((Object) expected.getClass()));
        assertThat(actual.getSourceFile().getPath(), is(expected.getSourceFile().getPath()));
        assertThat(actual.getLineStart(), is(expected.getLineStart()));
        assertThat(actual.getLineEnd(), is(expected.getLineEnd()));
        assertThat(actual.getCondition(), is(expected.getCondition()));
        assertThat(actual.getPresenceCondition(), is(expected.getPresenceCondition()));
        assertThat(actual.getNestedElementCount(), is(expected.getNestedElementCount()));

        assertThat(actual.getType().toString(), is(expected.getType().toString()));
        for (int i = 0; i < actual.getNestedElementCount(); i++) {
            assertThat(actual.getRelation(i), is(expected.getRelation(i)));
        }

        Iterator<SyntaxElement> actualIt = actual.iterator();
        Iterator<SyntaxElement> expectedIt = expected.iterator();

        while (expectedIt.hasNext()) {
            assertThat(actualIt.hasNext(), is(true));

            SyntaxElement actualChild = actualIt.next();
            SyntaxElement expectedChild = expectedIt.next();
            assertSyntaxElementEqual(actualChild, expectedChild);
        }
        assertThat(actualIt.hasNext(), is(false));
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

        OldCodeModelCache cache = new OldCodeModelCache(cacheDir, true);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertElementEqual(readIt.next(), originalIt.next());
        assertElementEqual(readIt.next(), originalIt.next());
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

        OldCodeModelCache cache = new OldCodeModelCache(new File("testdata/cmCaching/cache_compressed"), false);

        // don't write, the directory already contains the valid cache
        
        // read
        SourceFile<CodeBlock> readSourceFile = cache.read(location).castTo(CodeBlock.class);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopElementCount(), is(originalSourceFile.getTopElementCount()));

        Iterator<CodeBlock> originalIt = originalSourceFile.iterator();
        Iterator<CodeBlock> readIt = readSourceFile.iterator();

        assertElementEqual(readIt.next(), originalIt.next());
        assertElementEqual(readIt.next(), originalIt.next());
        assertThat(readIt.hasNext(), is(false));
    }

}
