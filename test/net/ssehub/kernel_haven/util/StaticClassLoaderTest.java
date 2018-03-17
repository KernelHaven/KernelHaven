package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

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
     */
    @Test
    public void testClassLoadedFromThisPackage() {
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass1"), is(false));
        
        // execute
        StaticClassLoader.loadClasses();
        
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
     */
    @Test
    public void testClassLoadedFromJar()
            throws MalformedURLException, ReflectiveOperationException, SecurityException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass2"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/validClass.jar"));
        
        // execute
        StaticClassLoader.loadClasses();
        
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
     */
    @Test
    public void testClassWithOnlyStaticBlock()
            throws MalformedURLException, ReflectiveOperationException, SecurityException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass3"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/class3.jar"));
        
        // execute
        StaticClassLoader.loadClasses();
        
        // verify
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.StaticallyLoadedClass3"), is(true));
    }
    
    /**
     * Tests an loadClasses.txt with an invalid class name.
     * 
     * @throws MalformedURLException unwanted. 
     * @throws SecurityException unwanted.
     * @throws ReflectiveOperationException unwanted. 
     */
    @Test
    public void testInvalidClassName()
            throws MalformedURLException, ReflectiveOperationException, SecurityException {
        
        // precondition: clear any previously registered classes
        testClassesLoaded.clear();
        assertThat(testClassesLoaded.contains("net.ssehub.kernel_haven.util.DoesntExist"), is(false));
        // precondition: add the URL to the jar
        loadJar(new File("testdata/staticLoadingTest/invalidClassName.jar"));
        
        // execute
        StaticClassLoader.loadClasses();
        
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
