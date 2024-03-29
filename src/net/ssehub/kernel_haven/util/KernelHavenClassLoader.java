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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link ClassLoader} to be used by KernelHaven. Starting with Java 9, it is required that this is set as the
 * <code>java.system.class.loader</code> in order for the plugin system to work.
 *
 * @author Adam
 */
public class KernelHavenClassLoader extends URLClassLoader {
          
    /**
     * Creates this class loader. This will be called by the JVM if <code>java.system.class.loader</code> is set to
     * this class.
     * 
     * @param parent The parent class loader to delegate to.
     */
    public KernelHavenClassLoader(@NonNull ClassLoader parent) {
        super(urlsForInitialization(parent) , parent);
    }
    
    /**
     * Reads the URL classpath from the parent constructor to be set in this constructor.
     * An empty array will override the classpath settings and, thus, the URLClassLoader may loose important
     * information, required to process our <tt>loadClasses.txt</tt> files.
     * @param parent The parent class loader to delegate to.
     * @return The URLs of the parent if possible or an empty array.
     */
    private static URL[] urlsForInitialization(@NonNull ClassLoader parent) {
        URL[] urls;
        System.out.println(parent.getClass());
        if (parent instanceof URLClassLoader) {
            urls = ((URLClassLoader) parent).getURLs();
        } else {
            String[] pathElements = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
            urls = new URL[pathElements.length];
            for (int i = 0; i < pathElements.length; i++) {
                try {
                    urls[i] = Paths.get(pathElements[i]).toAbsolutePath().toUri().toURL();
                } catch (MalformedURLException e) {
                    urls = new URL[0];
                }
            }
        }
        
        return urls;
    }
    
    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }

    // make this method public
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
    /**
     * Support for java-Agents (Coverage by Jacoco).
     * Reverse delegation order to test first if class can be loaded by our custom class loader (default in Java 8)
     * or loaded by the system loader (new default in Java 9+). <p>
     * Based on: <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a> <p>
     * {@inheritDoc} <p>
     * @see <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a>
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    c = super.loadClass(name, false);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    /**
     * Support for java-Agents (Coverage by Jacoco).
     * Reverse delegation order to test first if class can be loaded by our custom class loader (default in Java 8)
     * or loaded by the system loader (new default in Java 9+). <p>
     * Based on: <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a> <p>
     * {@inheritDoc} <p>
     * @see <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a>
     */
    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }

    /**
     * Support for java-Agents (Coverage by Jacoco).
     * Reverse delegation order to test first if class can be loaded by our custom class loader (default in Java 8)
     * or loaded by the system loader (new default in Java 9+). <p>
     * Based on: <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a> <p>
     * {@inheritDoc} <p>
     * @see <a href="https://stackoverflow.com/a/62245101">https://stackoverflow.com/a/62245101</a>
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> urls = Collections.list(findResources(name));
        urls.addAll(Collections.list(getParent().getResources(name)));
        return Collections.enumeration(urls);
    }
    
    /**
     * This class loader supports dynamic additions to the class path
     * at runtime.
     * @param path Path to java agent JAR
     * @throws MalformedURLException If a protocol handler for the URL could not be found,
     *                               or if some other error occurred while constructing the URL
     *
     * @see java.lang.instrument.Instrumentation#appendToSystemClassPathSearch
     */
    public void appendToClassPathForInstrumentation(String path) throws MalformedURLException {
        Thread.holdsLock(this);

        super.addURL(Paths.get(path).toUri().toURL());
    }
}
