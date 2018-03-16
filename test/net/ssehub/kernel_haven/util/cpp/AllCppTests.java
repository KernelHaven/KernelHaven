package net.ssehub.kernel_haven.util.cpp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.util.cpp.parser.AllParserTests;

/**
 * All tests in the cpp package.
 *
 * @author Adam
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllParserTests.class,
    
    NumberUtilsTest.class,
    })
public class AllCppTests {

}
