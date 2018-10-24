package net.ssehub.kernel_haven.variability_model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.Attribute;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.ConstraintFileType;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.VariableType;

/**
 * Tests the variability model cache.
 *
 * @author Johannes
 * @author Kevin
 * @author Moritz
 * @author Adam
 * @author Marvin
 */
@SuppressWarnings("null")
public class VariabilityModelCacheTest {

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
     * A simple tristate for testing.
     */
    private static class TristateVariable extends VariabilityVariable {

        private int moduleNumber;

        /**
         * Creates a new variable.
         * 
         * @param name
         *            The name of the new variable. Must not be null.
         * @param type The type. Will be ignored and replaced with "tristate".
         */
        public TristateVariable(String name, String type) {
            super(name, "tristate");
        }
        
        /**
         * Creates a new variable.
         * 
         * @param name
         *            The name of the new variable. Must not be null.
         * @param dimacsNumber
         *            The number that this variable has in the DIMACS
         *            representation of the variability model.
         * @param moduleNumber
         *            The number that the module part of this variable has in
         *            the DIMACS representation of the variability model.
         */
        public TristateVariable(String name, int dimacsNumber, int moduleNumber) {
            super(name, "tristate", dimacsNumber);
            this.moduleNumber = moduleNumber;
        }
        
        @Override
        protected @NonNull JsonObject toJson() {
            JsonObject result = super.toJson();
            
            result.putElement("dimacsModuleNumber", new JsonNumber(moduleNumber));
            
            return result;
        }
        
        @Override
        protected void setJsonData(@NonNull JsonObject data, Map<@NonNull String, VariabilityVariable> vars)
                throws FormatException {
            super.setJsonData(data, vars);
            
            this.moduleNumber = data.getInt("dimacsModuleNumber");
        }

        @Override
        public void getDimacsMapping(Map<Integer, String> mapping) {
            mapping.put(getDimacsNumber(), getName());
            mapping.put(moduleNumber, getName() + "_MODULE");
        }
        
        @Override
        public String toString() {
            return "TristateVariable [name=" + getName() + ", type=" + getType() + ", dimacsNumber=" + getDimacsNumber()
                    + ", moduleNumber=" + moduleNumber + ", codeLocations="
                    + (getSourceLocations() == null ? "null" : getSourceLocations().toString()) + "]";
        }

        @Override
        public int hashCode() {
            return super.hashCode() + new Integer(moduleNumber).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = super.equals(obj);
            if (result) {
                if (obj instanceof TristateVariable) {
                    TristateVariable other = (TristateVariable) obj;
                    result = other.moduleNumber == this.moduleNumber;

                } else {
                    result = false;
                }
            }
            return result;
        }

    }
    
    /**
     * Test serializing, and deserializing the result.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCaching() throws IOException, FormatException {
        File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
        Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();

        TristateVariable alpha = new TristateVariable("ALPHA", 1, 2);
        alpha.addLocation(new SourceLocation(new File("foxtrot/unicorn/charlie/kilo.kconfig"), 12));
        alpha.addLocation(new SourceLocation(new File("fruchtkernhafen.kconfig"), 42));
        set.add(alpha);

        set.add(new TristateVariable("BETA", 5, 4));

        VariabilityVariable gamma = new VariabilityVariable("GAMMA", "bool", 3);
        gamma.addLocation(new SourceLocation(new File("baumstaemmer.kconfig"), 14));
        set.add(gamma);
        VariabilityModel originalVm = new VariabilityModel(dimacsFile, set);

        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(cacheDir);

        // write
        cache.write(originalVm);
        
        // read
        VariabilityModel readVm = cache.read(new File(""));

        // check if constraint model is equal
        BufferedReader r1 = new BufferedReader(new FileReader(readVm.getConstraintModel()));
        BufferedReader r2 = new BufferedReader(new FileReader(originalVm.getConstraintModel()));

        String newLine;
        String originalLine;
        while ((newLine = r1.readLine()) != null) {
            originalLine = r2.readLine();
            assertThat(originalLine, notNullValue());

            assertThat(newLine, is(originalLine));
        }
        assertThat(r1.readLine(), nullValue());

        r1.close();
        r2.close();

        // check if variables are equal
        assertThat(readVm.getVariables(), is(originalVm.getVariables()));
    }

    /**
     * Tests three cases first: if the code location path and the line number
     * matches the given path in the cache with a normal variable. second: if
     * the code location path, line number matches the given path in the cache
     * with a tristate variable. third : if the code location and line number is
     * null (as it is in the cache).
     * 
     * @throws FormatException
     *             if a wrong format appears.
     * @throws IOException
     *             if a generally io exception appears.
     */
    @Test
    public void testCodeLocationRead() throws FormatException, IOException {

        File cacheLocation = new File("testdata/vmCaching/cache_codelocation/");
        assertThat(cacheLocation.getAbsolutePath() + " does not exist ", cacheLocation.exists(), is(true));
        assertThat(cacheLocation.getAbsolutePath() + " does is no directory", cacheLocation.isDirectory(), is(true));
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(cacheLocation);
        VariabilityModel varmodel = cache.read(new File(""));
        assertThat(varmodel, notNullValue());
        Map<String, VariabilityVariable> varMap = varmodel.getVariableMap();

        List<SourceLocation> locationsGamma = varMap.get("GAMMA").getSourceLocations();

        assertThat(locationsGamma.size(), is(2));
        assertThat(locationsGamma.get(0).getLineNumber(), is(15));
        assertThat(locationsGamma.get(0).getSource(), equalTo(new File("path/to/code/code.c")));
        assertThat(locationsGamma.get(1).getLineNumber(), is(11));
        assertThat(locationsGamma.get(1).getSource(), equalTo(new File("path/to/code/code2.c")));

        varMap.get("ALPHA").getSourceLocations();
        List<SourceLocation> locationAlpha = varMap.get("ALPHA").getSourceLocations();
        assertThat(locationAlpha.get(0).getSource(), equalTo(new File("path/to/code/code3.c")));
        assertThat(locationAlpha.get(0).getLineNumber(), is(42));
        List<SourceLocation> locationsBeta = varMap.get("BETA").getSourceLocations();
        assertThat(locationsBeta, nullValue());
    }

    /**
     * Tests if invalid class names are correctly thrown as
     * {@link FormatException}.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidClassName() throws FormatException, IOException {
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(
                new File("testdata/vmCaching/cache_invalid_class"));
        cache.read(new File(""));
    }

    /**
     * Tests if the VariabilityVariable correctly throws an
     * {@link FormatException} with an invalid csv file.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testMalFormCSV() throws FormatException, IOException {
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(
                new File("testdata/vmCaching/cache_invalid_json"));
        cache.read(new File(""));
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
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(new File("testdata/vmCaching/cache_empty"));
        VariabilityModel vm = cache.read(new File(""));

        assertThat(vm, nullValue());
    }

    /**
     * Tests if invalid codelocation correctly result in exceptions when read.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidCodeLocation() throws FormatException, IOException {
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(
                new File("testdata/vmCaching/cache_codelocation_invalid"));
        VariabilityModel vm = cache.read(new File(""));

        assertThat(vm, nullValue());
    }
    
    /**
     * Tests that the {@link VariabilityModelDescriptor} is (de-)serialized correctly.
     * 
     * @throws FormatException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testDescriptor() throws FormatException, IOException {
        File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
        
        Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();
        set.add(new VariabilityVariable("A", "bool"));
        
        VariabilityModel originalVm = new VariabilityModel(dimacsFile, set);
        originalVm.getDescriptor().setConstraintFileType(ConstraintFileType.DIMACS);
        originalVm.getDescriptor().setVariableType(VariableType.FINITE_INTEGER);
        originalVm.getDescriptor().addAttribute(Attribute.CONSTRAINT_USAGE);
        originalVm.getDescriptor().addAttribute(Attribute.SOURCE_LOCATIONS);
        
        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(cacheDir);

        // write
        cache.write(originalVm);
        
        // read
        VariabilityModel readVm = cache.read(new File(""));
        
        assertThat(readVm.getDescriptor().getConstraintFileType(), is(ConstraintFileType.DIMACS));
        assertThat(readVm.getDescriptor().getVariableType(), is(VariableType.FINITE_INTEGER));
        assertThat(readVm.getDescriptor().hasAttribute(Attribute.CONSTRAINT_USAGE), is(true));
        assertThat(readVm.getDescriptor().hasAttribute(Attribute.SOURCE_LOCATIONS), is(true));
        assertThat(readVm.getDescriptor().hasAttribute(Attribute.HIERARCHICAL), is(false));
        
        Set<Attribute> expectedAttrs = new HashSet<>();
        expectedAttrs.add(Attribute.CONSTRAINT_USAGE);
        expectedAttrs.add(Attribute.SOURCE_LOCATIONS);
        assertThat(readVm.getDescriptor().getAttributes(), is(expectedAttrs));
    }
    
    /**
     * Tests caching with a {@link VariabilityModel} that contains {@link HierarchicalVariable}s.
     * 
     * @throws FormatException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testHierarchical() throws FormatException, IOException {
        File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
        
        Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();
        HierarchicalVariable a = new HierarchicalVariable("A", "bool");
        HierarchicalVariable b = new HierarchicalVariable("B", "bool");
        HierarchicalVariable c = new HierarchicalVariable("C", "bool");
        HierarchicalVariable d = new HierarchicalVariable("D", "bool");
        
        b.setParent(a);
        c.setParent(b);
        d.setParent(a);
        
        set.add(a);
        set.add(b);
        set.add(c);
        set.add(d);
        
        VariabilityModel originalVm = new VariabilityModel(dimacsFile, set);
        originalVm.getDescriptor().setConstraintFileType(ConstraintFileType.DIMACS);
        originalVm.getDescriptor().addAttribute(Attribute.HIERARCHICAL);

        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(cacheDir);
        
        // write
        cache.write(originalVm);
        
        // read
        VariabilityModel readVm = cache.read(new File(""));
        
        HierarchicalVariable readA = (HierarchicalVariable) readVm.getVariableMap().get("A");
        HierarchicalVariable readB = (HierarchicalVariable) readVm.getVariableMap().get("B");
        HierarchicalVariable readC = (HierarchicalVariable) readVm.getVariableMap().get("C");
        HierarchicalVariable readD = (HierarchicalVariable) readVm.getVariableMap().get("D");
        
        assertThat(readA, notNullValue());
        assertThat(readB, notNullValue());
        assertThat(readC, notNullValue());
        assertThat(readD, notNullValue());
        assertThat(readVm.getVariableMap().size(), is(4));
        
        assertThat(readA.getName(), is("A"));
        assertThat(readA.getType(), is("bool"));
        assertThat(readA.getParent(), nullValue());
        assertThat(readA.getNestingDepth(), is(0));
        assertThat(readA.getChildren(), is(set(readB, readD)));
        
        assertThat(readB.getName(), is("B"));
        assertThat(readB.getType(), is("bool"));
        assertThat(readB.getParent(), sameInstance(readA));
        assertThat(readB.getNestingDepth(), is(1));
        assertThat(readB.getChildren(), is(set(readC)));
        
        assertThat(readC.getName(), is("C"));
        assertThat(readC.getType(), is("bool"));
        assertThat(readC.getParent(), sameInstance(readB));
        assertThat(readC.getNestingDepth(), is(2));
        assertThat(readC.getChildren(), is(set()));
        
        assertThat(readD.getName(), is("D"));
        assertThat(readD.getType(), is("bool"));
        assertThat(readD.getParent(), sameInstance(readA));
        assertThat(readD.getNestingDepth(), is(1));
        assertThat(readD.getChildren(), is(set()));
    }
    
    /**
     * Tests that constraint usage is (de-)serialized properly.
     * 
     * @throws FormatException unwanted.
     * @throws IOException unwanted.
     */
    @Test
    public void testConstraintUsage() throws FormatException, IOException {
        File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");

        Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();
        VariabilityVariable a = new VariabilityVariable("A", "bool");
        VariabilityVariable b = new VariabilityVariable("B", "bool");
        VariabilityVariable c = new VariabilityVariable("C", "bool");
        VariabilityVariable d = new VariabilityVariable("D", "bool");
        
        a.setUsedInConstraintsOfOtherVariables(set(b, c));
        b.setUsedInConstraintsOfOtherVariables(set(d));
        d.setUsedInConstraintsOfOtherVariables(set(c));
        
        b.setVariablesUsedInConstraints(set(a));
        c.setVariablesUsedInConstraints(set(a, d));
        d.setVariablesUsedInConstraints(set(b));
        
        set.add(a);
        set.add(b);
        set.add(c);
        set.add(d);
        
        VariabilityModel originalVm = new VariabilityModel(dimacsFile, set);
        originalVm.getDescriptor().setConstraintFileType(ConstraintFileType.DIMACS);
        originalVm.getDescriptor().addAttribute(Attribute.CONSTRAINT_USAGE);

        JsonVariabilityModelCache cache = new JsonVariabilityModelCache(cacheDir);
        
        // write
        cache.write(originalVm);
        
        // read
        VariabilityModel readVm = cache.read(new File(""));
        
        VariabilityVariable readA = readVm.getVariableMap().get("A");
        VariabilityVariable readB = readVm.getVariableMap().get("B");
        VariabilityVariable readC = readVm.getVariableMap().get("C");
        VariabilityVariable readD = readVm.getVariableMap().get("D");
        
        assertThat(readA, notNullValue());
        assertThat(readB, notNullValue());
        assertThat(readC, notNullValue());
        assertThat(readD, notNullValue());
        assertThat(readVm.getVariableMap().size(), is(4));
        
        assertThat(readA.getName(), is("A"));
        assertThat(readA.getType(), is("bool"));
        assertThat(readA.getVariablesUsedInConstraints(), nullValue());
        assertThat(readA.getUsedInConstraintsOfOtherVariables(), is(set(readB, readC)));
        
        assertThat(readB.getName(), is("B"));
        assertThat(readB.getType(), is("bool"));
        assertThat(readB.getVariablesUsedInConstraints(), is(set(readA)));
        assertThat(readB.getUsedInConstraintsOfOtherVariables(), is(set(readD)));
        
        assertThat(readC.getName(), is("C"));
        assertThat(readC.getType(), is("bool"));
        assertThat(readC.getVariablesUsedInConstraints(), is(set(readA, readD)));
        assertThat(readC.getUsedInConstraintsOfOtherVariables(), nullValue());
        
        assertThat(readD.getName(), is("D"));
        assertThat(readD.getType(), is("bool"));
        assertThat(readD.getVariablesUsedInConstraints(), is(set(readB)));
        assertThat(readD.getUsedInConstraintsOfOtherVariables(), is(set(readC)));
    }

    /**
     * Creates a set from varargs.
     * 
     * @param ts The varargs.
     * @param <T> The type of varargs.
     * 
     * @return A set with the varargs.
     */
    @SafeVarargs
    private static <T> Set<T> set(T... ts) {
        Set<T> set = new HashSet<>();
        for (T t : ts) {
            set.add(t);
        }
        return set;
    }
    
}
