package net.ssehub.kernel_haven.code_model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.code_model.ast.AllAstTests;
import net.ssehub.kernel_haven.code_model.simple_ast.SyntaxElementTest;

/**
 * Tests for code_model package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllAstTests.class,
    
    CodeBlockTest.class,
    CodeModelCacheTest.class,
    JsonCodeModelCacheTest.class,
    CodeModelProviderTest.class,
    SyntaxElementCsvUtilTest.class,
    SyntaxElementTest.class,
    })
public class AllCodeModelTests {

}
