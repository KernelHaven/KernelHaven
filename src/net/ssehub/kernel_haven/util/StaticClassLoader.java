package net.ssehub.kernel_haven.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
 * <code>public static void initialize()</code> method of the class will be called (if it is available).
 * </p>
 *
 * @author Adam
 */
public class StaticClassLoader {

    private static final Logger LOGGER = Logger.get();
    
    private static final String LOAD_CLASSES_FILENAME = "loadClasses.txt";
    
    private static final String INIT_METHOD_NAME = "initialize";
    
    /**
     * Searches for all "loadClasses.txt" in all class loader URLs and loads the specified classes.
     */
    public static void loadClasses() {
        LOGGER.logInfo("Loading all classes specified in " + LOAD_CLASSES_FILENAME + " ...");
        
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        
        String javaHome = System.getProperty("java.home");
        List<@NonNull File> files = new LinkedList<>();
        
        for (URL url : classLoader.getURLs()) {
            if (url.getProtocol().equals("file")) {
                File file = new File(url.getPath());
                
                if (!file.getAbsolutePath().startsWith(javaHome)) {
                    files.add(file);
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
                // load the class
                Class<?> clazz = Class.forName(className);
                
                // try to call the initialize() method
                try {
                    clazz.getMethod(INIT_METHOD_NAME).invoke(null);
                    
                } catch (ReflectiveOperationException | SecurityException e) {
                    LOGGER.logExceptionDebug("Can't execute " + INIT_METHOD_NAME + " for class " + clazz.getName(), e);
                }
                
                loaded++;
            } catch (ClassNotFoundException e) {
                LOGGER.logExceptionWarning("Can't load class name " + className + " specified in loadClasses.txt", e);
            }
        }
        
        LOGGER.logInfo("Loaded " + loaded + " classes specified in loadClasses.txt files");
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
