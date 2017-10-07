package net.ssehub.kernel_haven.util.io.csv;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for util.io.csv package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CsvFileCollectionTest.class,
    CsvReaderTest.class,
    CsvWriterTest.class,
})
public class AllCsvTests {

}
