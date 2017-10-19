package net.ssehub.kernel_haven.code_model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for code_model package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CodeBlockTest.class,
    CodeModelCacheTest.class,
    CodeModelProviderTest.class,
    SyntaxElementCsvUtilTest.class,
    SyntaxElementTest.class,
    })
public class AllCodeModelTests {

}
