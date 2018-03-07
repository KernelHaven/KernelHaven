package net.ssehub.kernel_haven.util.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for util.logic package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    FormulaTest.class,
    ParserTest.class,
    SubFormulaCheckerTest.class,
    VariableFinderTest.class,
    DisjunctionQueueTests.class,
    FormulaEvaluatorTest.class,
    FormulaSimplifierTest.class,
    })
public class AllLogicTests {

}
