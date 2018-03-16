package net.ssehub.kernel_haven.util.cpp.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All tests for the parser package.
 *
 * @author Adam
 */
@RunWith(Suite.class)
@SuiteClasses({
    CppLexerTest.class,
    CppParserTest.class,
    CppParserScenarioTests.class,
    })
public class AllParserTests {

}
