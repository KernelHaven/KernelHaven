/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import net.ssehub.kernel_haven.analysis.AbstractAnalysis;
import net.ssehub.kernel_haven.build_model.AbstractBuildModelExtractor;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.AbstractCodeModelExtractor;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Tests the {@link PipelineConfigurator} class.
 * 
 * @author Adam
 * @author Manu
 * @author Moritz
 * @author Johannes
 */
@SuppressWarnings("null")
public class PipelineConfiguratiorTest {

    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * VM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testVmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", "thisClassDoesNotExist");
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testVmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getVmExtractor(), instanceOf(DummyVmExtractor.class));
        
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyVmExtractor extends AbstractVariabilityModelExtractor {

        @Override
        protected void init(Configuration config) throws SetUpException {
        }

        @Override
        protected VariabilityModel runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return "DummyVmExtractor";
        }


    }


    /**
     * Tests whether instantiateExtractor correctly instantiates a class after loading jars
     * from a directory containing a packaged version of this class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testPluginVmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", "net.ssehub.kernel_haven.DummyPluginVmExtractor");
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("plugins_dir", "testdata/plugins");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.loadPlugins();
        configurator.instantiateExtractors();

        assertThat(configurator.getVmExtractor(), notNullValue());

        List<Method> methods = Arrays.asList(configurator.getVmExtractor().getClass().getDeclaredMethods());

        List<String> methodNames = new ArrayList<>();
        for (Method method : methods) {
            methodNames.add(method.getName());
        }

        assertThat(methodNames, hasItem(equalTo("runOnFile")));
        assertThat(methodNames, hasItem(equalTo("init")));
        assertThat(methodNames, hasItem(equalTo("getName")));
    }

    /**
     * Tests whether instantiateAnalysis correctly throws an exception if the
     * analysis is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testAnalysisWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", "thisClassDoesNotExist");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();
    }

    /**
     * Tests whether instantiateAnalysis correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testAnalysisInit() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", DummyAnalysis.class.getName());
        config.setProperty("output_dir", "not_used");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
        configurator.createProviders();
        configurator.instantiateAnalysis();

        assertThat(configurator.getAnalysis(), instanceOf(DummyAnalysis.class));
    }

    /**
     * Tests whether instantiateAnalysis correctly throws an
     * {@link SetUpException} if the constructor throws one.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testAnalysisSetUpException() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", DummyAnalysis.class.getName());
        config.setProperty("fail", "true");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();

        assertThat(configurator.getAnalysis(), instanceOf(DummyAnalysis.class));
    }
    
    /**
     * Tests if a class that does not implement IAnalysis correctly throws an exception.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testNoIAnalysis() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", WrongDummyAnalysis.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();
    }

    /**
     * A dummy analysis for testing purposes.
     */
    private static class DummyAnalysis extends AbstractAnalysis {

        /**
         * Creates a new dummy analysis.
         * 
         * @param config
         *            The configuration file.
         * 
         * @throws SetUpException
         *             If properties contains a key called "fail".
         */
        @SuppressWarnings("deprecation")
        public DummyAnalysis(@NonNull Configuration config) throws SetUpException {
            super(config);
            
            if (config.getProperty("fail") != null) {
                throw new SetUpException();
            }
        }

        @Override
        public void run() {

        }

    }
    
    /**
     * A dummy analysis that does not implement IAnalyis, so that a ClassCastException is provoked.
     */
    private static class WrongDummyAnalysis {
        
        /**
         * ctor.
         * @param config The user config.
         */
        @SuppressWarnings("unused")
        public WrongDummyAnalysis(Configuration config) {
        }
        
    }
    
    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * BM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testBmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", "thisClassDoesNotExist");
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testBmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getBmExtractor(), instanceOf(DummyBmExtractor.class));
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyBmExtractor extends AbstractBuildModelExtractor {

        @Override
        protected void init(Configuration config) throws SetUpException {
        }

        @Override
        protected BuildModel runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return "DummyBmExtractor";
        }


    }
    
    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * CM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testCmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", "thisClassDoesNotExist");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testCmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getCmExtractor(), instanceOf(DummyCmExtractor.class));
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyCmExtractor extends AbstractCodeModelExtractor {

        @Override
        protected void init(Configuration config) throws SetUpException {
        }

        @Override
        protected SourceFile<?> runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return "DummyCmExtrator";
        }


    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} does nothing when no preparation class is set.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testRunPreparationNoPreparations() throws SetUpException {
        Properties config = new Properties();
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        pipconf.runPreparation();
        
        // test that thread name didn't change
        assertThat(Thread.currentThread().getName(), is(threadName));
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} throws an exception if no config is set.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testRunPreparationThrowsIfNoConfig() throws SetUpException {
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.runPreparation();
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} correctly executes a single preparation.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testRunPreparationOnePreparations() throws SetUpException {
        DummyPreparation.executed = 0;
        DummyPreparation.throwException = false;
        
        Properties config = new Properties();
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".0", DummyPreparation.class.getName());
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        pipconf.runPreparation();
        
        assertThat(DummyPreparation.executed, is(1));
        
        // test that thread name didn't change
        assertThat(Thread.currentThread().getName(), is(threadName));
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} correctly executes multiple preparations.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testRunPreparationMultiplePreparations() throws SetUpException {
        DummyPreparation.executed = 0;
        DummyPreparation.throwException = false;
        
        Properties config = new Properties();
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".0", DummyPreparation.class.getName());
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".1", DummyPreparation.class.getName());
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".2", DummyPreparation.class.getName());
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".3", DummyPreparation.class.getName());
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        pipconf.runPreparation();
        
        assertThat(DummyPreparation.executed, is(4));
        
        // test that thread name didn't change
        assertThat(Thread.currentThread().getName(), is(threadName));
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} correctly handles exceptions thrown by the preparation.
     * 
     * @throws SetUpException wanted. 
     */
    @Test(expected = SetUpException.class)
    public void testRunPreparationThrowsException() throws SetUpException {
        DummyPreparation.executed = 0;
        DummyPreparation.throwException = true;
        
        Properties config = new Properties();
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".0", DummyPreparation.class.getName());
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        try {
            pipconf.runPreparation();
            
        } finally {
            // test that thread name didn't change
            assertThat(Thread.currentThread().getName(), is(threadName));
        }
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} throws an exception if the class doesn't exist.
     * 
     * @throws SetUpException wanted. 
     */
    @Test(expected = SetUpException.class)
    public void testRunPreparationInvalidClassName() throws SetUpException {
        DummyPreparation.executed = 0;
        DummyPreparation.throwException = true;
        
        Properties config = new Properties();
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".0", "doesnt.Exist");
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        try {
            pipconf.runPreparation();
            
        } finally {
            // test that thread name didn't change
            assertThat(Thread.currentThread().getName(), is(threadName));
        }
    }
    
    /**
     * Tests that {@link PipelineConfigurator#runPreparation()} throws an exception if the class doesn't implement
     * {@link IPreparation}.
     * 
     * @throws SetUpException wanted. 
     */
    @Test(expected = SetUpException.class)
    public void testRunPreparationDoesntImplement() throws SetUpException {
        DummyPreparation.executed = 0;
        DummyPreparation.throwException = true;
        
        Properties config = new Properties();
        config.setProperty(DefaultSettings.PREPARATION_CLASSES.getKey() + ".0", DummyCmExtractor.class.getName());
        
        PipelineConfigurator pipconf = new PipelineConfigurator();
        pipconf.init(new TestConfiguration(config));
        
        String threadName = Thread.currentThread().getName();
        
        try {
            pipconf.runPreparation();
            
        } finally {
            // test that thread name didn't change
            assertThat(Thread.currentThread().getName(), is(threadName));
        }
    }

    /**
     * A dummy implementation of {@link IPreparation}.
     */
    public static class DummyPreparation implements IPreparation {

        private static int executed;
        
        private static boolean throwException;
        
        @Override
        public void run(@NonNull Configuration config) throws SetUpException {
            executed++;
            
            if (throwException) {
                throw new SetUpException();
            }
        }
        
    }

}
