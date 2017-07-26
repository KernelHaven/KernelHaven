package net.ssehub.kernel_haven.todo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;

// TODO: tidy up this temporary hack
// CHECKSTYLE:OFF

public abstract class PreprocessorConditionVisitor {
    
    public void visitAllFiles(File directory) throws IOException {
        try {
            Files.walk(directory.toPath(), FileVisitOption.FOLLOW_LINKS).forEach((file) -> {
                try {
                    visiFile(file.toFile());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public void visiFile(File file) throws IOException {
        if (!file.isFile() || (!file.getName().endsWith(".c") && !file.getName().endsWith(".h"))) {
            return;
        }
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                
                if (!line.startsWith("#if ") && !line.startsWith("#elif ")) {
                    continue;
                }
                
                // Consider continuation
                while (line.charAt(line.length() - 1) == '\\') {
                    String tmp = in.readLine();
                    if (null != tmp) {
                        line += tmp;
                    } else {
                        break;
                    }
                }
                
                visit(line);
            }
            
        }
    }
    
    public abstract void visit(String line);
    
}
