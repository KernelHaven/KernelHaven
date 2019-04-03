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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>Utility class for loading and executing a static initialization method of all classes specified in
 * "loadClasses.txt" files. This mechanism allows plugins to dynamically set themselves up, without being hard-coded
 * in the infrastructure. This can be used to e.g. register handlers for certain factories.
 * </p>
 * <p>
 * The "loadClasses.txt" files can be located in any location accessible by the system class loader. They contain
 * one fully qualified class name per line. Empty lines (ignoring whitespace characters) and lines starting with a hash
 * (&#35;) are ignored. 
 * </p>
 * <p>
 * Classes that are specified in these files will be loaded (via {@link Class#forName(String)}) and the static
 * <code>public static void initialize(@NonNull {@link Configuration} config)</code> method of the class will be called.
 * If loaded classes do not define this method, a warning is printed out.
 * </p>
 *
 * @author Adam
 */
public class StaticClassLoader {
    
    /**
     * The file name that text files that specify classes to load must have.
     */
    public static final String LOAD_CLASSES_FILENAME = "loadClasses.txt";
    
    /**
     * The method name that is called if it exists in the loaded classes. Must be public static void and take no
     * parameters. 
     */
    public static final String INIT_METHOD_NAME = "initialize";
    
    private static final Logger LOGGER = Logger.get();

    /**
     * Don't allow any instances.
     */
    private StaticClassLoader() {
    }
    
    /**
     * Searches for all "loadClasses.txt" in all class loader URLs and loads the specified classes.
     * 
     * @param config The configuration to pass to the initialize methods.
     */
    public static void loadClasses(@NonNull Configuration config) {
        LOGGER.logInfo("Loading all classes specified in " + LOAD_CLASSES_FILENAME + " ...");
        
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        
        String javaHome = System.getProperty("java.home");
        List<@NonNull File> files = new LinkedList<>();
        
        for (URL url : classLoader.getURLs()) {
            if (url.getProtocol().equals("file")) {
                try {
                    File file = new File(url.toURI());
                    
                    if (!file.getAbsolutePath().startsWith(javaHome)) {
                        files.add(file);
                    }
                } catch (IllegalArgumentException | URISyntaxException e) {
                    LOGGER.logExceptionWarning("Can't convert classpath URL to file", e);
                }
                
            } else {
                LOGGER.logWarning("Can't handle class loader URL with protocol " + url.getProtocol());
            }
        }
        
        Set<@NonNull String> classesToLoad = new HashSet<>();
        for (File file : files) {
            
            if (file.isDirectory()) {
                findClassesToLoadInDir(file, classesToLoad);
                
            } else if (file.isFile() && file.getName().endsWith(".jar")) {
                findClassesToLoadInJar(file, classesToLoad);
            }
        }

        int loaded = 0;
        for (@NonNull String className : classesToLoad) {
            try {
                LOGGER.logDebug("Loading class " + className + "...");
                
                // load the class
                // don't use ClassLoader.loadClass(), because it doesn't initialize
                Class<?> clazz = Class.forName(className, true, ClassLoader.getSystemClassLoader());
                
                // try to call the initialize(Configuration) method
                try {
                    clazz.getMethod(INIT_METHOD_NAME, Configuration.class).invoke(null, config);
                    
                } catch (NoSuchMethodException e) {
                    LOGGER.logWarning(clazz.getName() + " has no " + INIT_METHOD_NAME + "(Configuration) method");
                    callLegacyMethod(clazz);
                    
                } catch (ReflectiveOperationException | SecurityException e) {
                    LOGGER.logException("Can't execute " + INIT_METHOD_NAME + " for class " + clazz.getName(), e);
                }
                
                loaded++;
            } catch (ClassNotFoundException e) {
                LOGGER.logExceptionWarning("Can't load class name " + className + " specified in loadClasses.txt", e);
            }
        }
        
        LOGGER.logInfo("Loaded " + loaded + " classes specified in loadClasses.txt files");
    }
    
    /**
     * Calls the old version of the initialize() method without a parameter. This was old behavior, before the
     * configuration object was passed to it as a parameter.
     * 
     * @param clazz The class to call the method for.
     */
    private static void callLegacyMethod(@NonNull Class<?> clazz) {
        // try to call the initialize() method
        try {
            clazz.getMethod(INIT_METHOD_NAME).invoke(null);
            
        } catch (NoSuchMethodException e) {
            // ignore that class has no initialize method; it maybe only want to execute the static block
            LOGGER.logDebug(clazz.getName() + " has no " + INIT_METHOD_NAME + " method");
            
        } catch (ReflectiveOperationException | SecurityException e) {
            LOGGER.logException("Can't execute " + INIT_METHOD_NAME + " for class " + clazz.getName(), e);
        }
    }
    
    /**
     * Finds all "loadClasses.txt" in the specified directory (or sub directories). Calls
     * {@link #readClassesToLoad(BufferedReader, Set)} on each of these file contents.
     * 
     * @param dir The directory to search in.
     * @param result The set where the resulting class names should be added.
     */
    private static void findClassesToLoadInDir(File dir, @NonNull Set<@NonNull String> result) {
        try {
            Iterator<File> files = Files.walk(dir.toPath())
                .filter(f -> Files.isRegularFile(f))
                .filter(f -> f.getFileName().toString().equals(LOAD_CLASSES_FILENAME))
                .map(f -> f.toFile())
                .iterator();
            
            while (files.hasNext()) {
                File loadClassesFile = files.next();
                try (BufferedReader in = new BufferedReader(new FileReader(loadClassesFile))) {
                    readClassesToLoad(in, result);
                    
                } catch (IOException e) {
                    LOGGER.logException("Unable to read file " + loadClassesFile, e); 
                }
            }
            
            
        } catch (IOException e) {
            LOGGER.logException("Unable to search for " + LOAD_CLASSES_FILENAME + " in " + dir, e);
        }
    }

    /**
     * Finds all "loadClasses.txt" in the specified jar file. Calls
     * {@link #readClassesToLoad(BufferedReader, Set)} on each of these file contents.
     * 
     * @param jar The jar file to search in.
     * @param result The set where the resulting class names should be added.
     */
    private static void findClassesToLoadInJar(@NonNull File jar, @NonNull Set<@NonNull String> result) {
        try (ZipArchive archive = new ZipArchive(jar)) {
            
            for (File file : archive.listFiles()) {
                if (file.getName().equals(LOAD_CLASSES_FILENAME)) {
                    
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(archive.getInputStream(file)))) {
                        readClassesToLoad(in, result);
                        
                    } catch (IOException e) {
                        LOGGER.logException("Unable to read file " + file + " in jar " + jar, e); 
                    }
                }
            }
            
        } catch (IOException e) {
            LOGGER.logException("Unable to search for " + LOAD_CLASSES_FILENAME + " in jar " + jar, e);
        }
    }
    
    /**
     * Reads all class names from the specified input stream.
     * 
     * @param in The input stream to read from.
     * @param result The set to add the result class names to.
     * 
     * @throws IOException If reading the stream fails.
     */
    private static void readClassesToLoad(@NonNull BufferedReader in, @NonNull Set<@NonNull String> result)
            throws IOException {
        
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                result.add(line);
            }
        }
    }
    
}
