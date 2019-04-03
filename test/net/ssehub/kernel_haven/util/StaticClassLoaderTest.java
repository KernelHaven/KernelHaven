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
package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;

/**
 * Tests the {@link StaticClassLoader}.
 *
 * @author Adam
 */
public class StaticClassLoaderTest {

    /**
     * Contains fully qualified class names of relevant test classes that have been loaded. They add themselves to this
     * set in their init method.
     */
    private static Set<String> testClassesLoaded = new HashSet<>();

    /**
     * Tells this test cases that the init block of a test class has been loaded.
     * 
     * @param className The class name that was loaded.
     */
    public static void testClassLoaded(String className) {
        testClassesLoaded.add(className);
    }
    
    /**
     * Tests that net.ssehub.kernel_haven.util.StaticallyLoadedClass1 is loaded by the loadClasses.txt in the current
     * package.
     * 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testClassLoadedFromThisPackage() throws SetUpException {
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass1"), is(false));
        
        // execute
        StaticClassLoader.loadClasses(new TestConfiguration(new Properties()));
        
        // verify
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass1"), is(true));
    }
    
    /**
     * Tests that net.ssehub.kernel_haven.util.StaticallyLoadedClass2 is loaded by the loadClasses.txt in a jar located
     * in the testdata folder.
     * 
     * @throws MalformedURLException unwanted. 
     * @throws SecurityException unwanted.
     * @throws ReflectiveOperationException unwanted. 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testClassLoadedFromJar()
            throws MalformedURLException, ReflectiveOperationException, SecurityException, SetUpException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass2"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/validClass.jar"));
        
        // execute
        StaticClassLoader.loadClasses(new TestConfiguration(new Properties()));
        
        // verify
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass2"), is(true));
    }
    
    /**
     * Tests that net.ssehub.kernel_haven.util.StaticallyLoadedClass3 is loaded, although it has no initialization
     * method. This checks whether the static block was executed.
     * 
     * @throws MalformedURLException unwanted. 
     * @throws SecurityException unwanted.
     * @throws ReflectiveOperationException unwanted. 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testClassWithOnlyStaticBlock()
            throws MalformedURLException, ReflectiveOperationException, SecurityException, SetUpException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass3"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/class3.jar"));
        
        // execute
        StaticClassLoader.loadClasses(new TestConfiguration(new Properties()));
        
        // verify
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass3"), is(true));
    }
    
    /**
     * Tests an loadClasses.txt with an invalid class name.
     * 
     * @throws MalformedURLException unwanted. 
     * @throws SecurityException unwanted.
     * @throws ReflectiveOperationException unwanted. 
     * @throws SetUpException unwanted.
     */
    @Test
    public void testInvalidClassName()
            throws MalformedURLException, ReflectiveOperationException, SecurityException, SetUpException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.DoesntExist"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/invalidClassName.jar"));
        
        // execute
        StaticClassLoader.loadClasses(new TestConfiguration(new Properties()));
        
        // verify
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.DoesntExist"), is(false)); // still false
    }
    
    /**
     * Loads the given jar into the current class loader.
     * 
     * @param jar The jar file to load.
     * 
     * @throws MalformedURLException unwanted.
     * @throws ReflectiveOperationException unwanted.
     * @throws SecurityException unwanted.
     */
    private void loadJar(File jar) throws MalformedURLException, ReflectiveOperationException, SecurityException {
        assertThat(jar.isFile(), is(true));
        
        URL url = jar.toURI().toURL();
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

        method.setAccessible(true);
        method.invoke(classLoader, url);
    }
    
}
