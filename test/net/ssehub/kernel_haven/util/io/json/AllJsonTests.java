package net.ssehub.kernel_haven.util.io.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for util.io.json package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    JsonParserTest.class,
    ParameterizedJsonParserTest.class,
    ParameterizedJsonParserNegativeTest.class,
    })
public class AllJsonTests {

}
