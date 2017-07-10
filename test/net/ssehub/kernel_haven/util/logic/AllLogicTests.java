package net.ssehub.kernel_haven.util.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The Suite for the logic package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    FormulaTest.class, ParserTest.class, SubFormulaCheckerTest.class
    })
public class AllLogicTests {

}
