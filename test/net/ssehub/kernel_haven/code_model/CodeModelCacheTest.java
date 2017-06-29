package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.Variable;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;

/**
 * Tests the code model cache.
 *
 * @author Adam
 * @author Alice
 */
public class CodeModelCacheTest {

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
     * A block implementation for test cases.
     */
    public static class PseudoBlock extends Block {

        private List<Block> children;

        private int lineStart;

        private int lineEnd;

        private Formula condition;

        private Formula presenceCondition;

        /**
         * Creates a new Block.
         * 
         * @param lineStart
         *            The starting line of this block.
         * @param lineEnd
         *            The end line of this block.
         * @param condition
         *            The immediate condition of this block.
         * @param presenceCondition
         *            The pc. Must not be <code>null</code>.
         */
        public PseudoBlock(int lineStart, int lineEnd, Formula condition, Formula presenceCondition) {
            children = new LinkedList<>();
            this.lineStart = lineStart;
            this.lineEnd = lineEnd;
            this.condition = condition;
            this.presenceCondition = presenceCondition;
        }

        @Override
        public Iterator<Block> iterator() {
            return children.iterator();
        }

        @Override
        public int getNestedBlockCount() {
            return children.size();
        }

        @Override
        public int getLineStart() {
            return lineStart;
        }

        @Override
        public int getLineEnd() {
            return lineEnd;
        }

        @Override
        public Formula getCondition() {
            return condition;
        }

        @Override
        public Formula getPresenceCondition() {
            return presenceCondition;
        }

        @Override
        public void addChild(Block block) {
            this.children.add(block);
        }
        
        @Override
        public List<String> serializeCsv() {
            List<String> result = new ArrayList<>(4);
            
            result.add(lineStart + "");
            result.add(lineEnd + "");
            result.add(condition == null ? "null" : condition.toString());
            result.add(presenceCondition.toString());
            
            return result;
        }
        
        /**
         * Deserializes the given CSV into a block.
         * 
         * @param csv The csv.
         * @param parser The parser to parse boolean formulas.
         * @return The deserialized block.
         * 
         * @throws FormatException If the CSV is malformed.
         */
        public static PseudoBlock createFromCsv(String[] csv, Parser<Formula> parser) throws FormatException {
            if (csv.length != 4) {
                throw new FormatException("Invalid CSV");
            }
            
            int lineStart = Integer.parseInt(csv[0]);
            int lineEnd = Integer.parseInt(csv[1]);
            Formula condition = null;
            if (!csv[2].equals("null")) {
                try {
                    condition = parser.parse(csv[2]);
                } catch (ExpressionFormatException e) {
                    throw new FormatException(e);
                }
            }
            
            Formula presenceCondition;
            try {
                presenceCondition = parser.parse(csv[3]);
            } catch (ExpressionFormatException e) {
                throw new FormatException(e);
            }
            
            return new PseudoBlock(lineStart, lineEnd, condition, presenceCondition);
        }
        
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
        File location = new File("test.c");
        SourceFile originalSourceFile = new SourceFile(location);
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        PseudoBlock block1 = new PseudoBlock(1, 2, a, a);
        PseudoBlock block2 = new PseudoBlock(3, 15, new Negation(a), new Negation(a));
        PseudoBlock block21 = new PseudoBlock(4, 5, b, new Conjunction(b, new Negation(a)));
        block2.addChild(block21);
        
        originalSourceFile.addBlock(block1);
        originalSourceFile.addBlock(block2);
        

        CodeModelCache cache = new CodeModelCache(cacheDir);

        // write
        cache.write(originalSourceFile);

        // read
        SourceFile readSourceFile = cache.read(location);

        // check if equal
        assertThat(readSourceFile.getPath(), is(originalSourceFile.getPath()));
        assertThat(readSourceFile.getTopBlockCount(), is(originalSourceFile.getTopBlockCount()));
        
        Iterator<Block> originalIt = originalSourceFile.iterator();
        Iterator<Block> readIt = readSourceFile.iterator();
        
        assertBlockEqual(readIt.next(), originalIt.next());
        assertBlockEqual(readIt.next(), originalIt.next());
        assertThat(readIt.hasNext(), is(false));
    }
    
    /**
     * Asserts that both blocks are equal. Recursively checks child blocks, too.
     * 
     * @param actual The actual value.
     * @param expected The expected value.
     */
    private void assertBlockEqual(Block actual, Block expected) {
        assertThat(actual.getClass(), is((Object) expected.getClass()));
        assertThat(actual.getLineStart(), is(expected.getLineStart()));
        assertThat(actual.getLineEnd(), is(expected.getLineEnd()));
        assertThat(actual.getCondition(), is(expected.getCondition()));
        assertThat(actual.getPresenceCondition(), is(expected.getPresenceCondition()));
        assertThat(actual.getNestedBlockCount(), is(expected.getNestedBlockCount()));
        
        Iterator<Block> actualIt = actual.iterator();
        Iterator<Block> expectedIt = expected.iterator();
        
        while (expectedIt.hasNext()) {
            assertThat(actualIt.hasNext(), is(true));
            
            Block actualChild = actualIt.next();
            Block expectedChild = expectedIt.next();
            assertBlockEqual(actualChild, expectedChild);
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
        CodeModelCache cache = new CodeModelCache(new File("testdata/cmCaching/cache1"));
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
        CodeModelCache cache = new CodeModelCache(new File("testdata/cmCaching/cache2"));
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
        CodeModelCache cache = new CodeModelCache(new File("testdata/bmCaching/cache3"));
        SourceFile result = cache.read(new File("test.c"));
        
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
        CodeModelCache cache = new CodeModelCache(new File("testdata/cmCaching/cache4"));
        cache.read(new File("test.c"));
    }
    
}
