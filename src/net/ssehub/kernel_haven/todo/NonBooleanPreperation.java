package net.ssehub.kernel_haven.todo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ssehub.kernel_haven.PipelineConfigurator;
import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;

//TODO: tidy up this temporary hack
//CHECKSTYLE:OFF

/**
 * @author Adam
 */
public class NonBooleanPreperation {
    
    private static final Logger LOGGER = Logger.get();
    
    private File originalSourceTree;
    
    private File copiedSourceTree;
    
    private Map<String, Set<NonBooleanOperation>> nonBooleanOperations;
    
    private Map<String, NonBooleanVariable> variables;
    
    private Set<String> burntVariables;
    
    public void run(CodeExtractorConfiguration config) throws SetUpException {
        copiedSourceTree = new File(config.getProperty("prepare_non_boolean.destination"));
        originalSourceTree = config.getSourceTree();
        
        try {
            prepare();
        } catch (IOException e) {
            throw new SetUpException(e);
        }
        
        config.setSourceTree(copiedSourceTree);
        PipelineConfigurator.instance().getCmProvider().setConfig(config);
    }

    private static final class NonBooleanVariable {
        
        private String name;
        
        private int[] constants;
        
        public NonBooleanVariable(String name, Set<Integer> constants) {
            this.name = name;
            this.constants = new int[constants.size()];
            int i = 0;
            for (Integer c : constants) {
                this.constants[i++] = c;
            }
        }
        
        public int[] getConstants() {
            return constants;
        }
        
        public String getConstantName(int constant) {
            return name + "_eq_" + constant;
        }
        
        @Override
        public String toString() {
            return name + Arrays.toString(constants);
        }
        
    }
    
    /**
     * A non boolean operation on a variability variable.
     * E.g. <code>>= 3</code>.
     */
    private static final class NonBooleanOperation {
        
        private String operator;
        
        private int value;

        /**
         * .
         * @param operator .
         * @param value .
         */
        public NonBooleanOperation(String operator, int value) {
            this.operator = operator;
            this.value = value;
        }
        
        @Override
        public int hashCode() {
            return operator.hashCode() + Integer.hashCode(value);
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean equal = false;
            if (obj instanceof NonBooleanOperation) {
                NonBooleanOperation other = (NonBooleanOperation) obj;
                equal = this.operator.equals(other.operator) && this.value == other.value;
            }
            return equal;
        }
        
    }
    
    /**
     * Prepare the source tree.
     * 
     * @throws IOException IF reading files fails.
     */
    private synchronized void prepare() throws IOException {
        LOGGER.logDebug("Starting preperation...");
        
        if (copiedSourceTree.exists()) {
            Util.deleteFolder(copiedSourceTree);
        }
        copiedSourceTree.mkdir();
        
        nonBooleanOperations = new HashMap<>();
        burntVariables = new HashSet<>();
        
        new PreprocessorConditionVisitor() {
            
            @Override
            public void visit(String line) {
                try {
                    collectNonBooleanFromLine(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.visitAllFiles(originalSourceTree);
        
        
        variables = new HashMap<>();
        
        for (Map.Entry<String, Set<NonBooleanOperation>> entry : nonBooleanOperations.entrySet()) {
            Set<Integer> requiredConstants = new HashSet<>();
            
            
            for (NonBooleanOperation op : entry.getValue()) {
                switch (op.operator) {
                case "==":
                case "!=":
                case ">=":
                case "<=":
                    requiredConstants.add(op.value);
                    break;
                    
                case ">":
                    requiredConstants.add(op.value + 1);
                    break;
                    
                case "<":
                    requiredConstants.add(op.value - 1);
                    break;
                    
                default:
                    System.err.println("Unkown operator: " + op.operator);
                    break;
                }
            }
            
            variables.put(entry.getKey(), new NonBooleanVariable(entry.getKey(), requiredConstants));
        }
        
        System.out.println("Burnt variables: " + burntVariables);
        System.out.println("Variables: " + variables);
        
        
        LOGGER.logDebug("Copying from " + originalSourceTree.getAbsolutePath() + " to " + copiedSourceTree.getAbsolutePath());
        copy(originalSourceTree, copiedSourceTree);
    }
    
    private void copy(File from, File to) throws IOException {
        for (File f : from.listFiles()) {
            
            File newF = new File(to, f.getName());
            
            if (f.isDirectory()) {
                newF.mkdir();
                copy(f, newF);
            } else {
                copyFile(f, newF);
            }
        }
    }
    
    private void copyFile(File from, File to) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(from))) {
            
            try (BufferedWriter out = new BufferedWriter(new FileWriter(to))) {
                
                String line;
                while ((line = in.readLine()) != null) {
                    
                    if (from.getName().endsWith(".c") || from.getName().endsWith(".h")) {
                        if (line.isEmpty() || line.charAt(0) != '#' || line.startsWith("#if ") || line.startsWith("#elif ")) {
                            while (line.charAt(line.length() - 1) == '\\') {
                                line += in.readLine();
                            }
                            
                            line = line.trim();
                            line = replaceInLine(line);
                        }
                        
                    }
                    
                    out.write(line);
                    out.write("\n");
                }
                
            }
        }
    }
    
    private static final Pattern leftSideFinder = Pattern.compile("(VarTypeC_[0-9]+)\\s*(==|!=|<|>|<=|>=)\\s*(-?[0-9]+)");
    
    private String replaceInLine(String line) {
        
        String result = line;
        Matcher m = leftSideFinder.matcher(line);
        
        while (m.find()) {
            String whole = m.group();
            String name = m.group(1);
            String op = m.group(2);
            int value = Integer.parseInt(m.group(3));
            
            String replacement = "ERROR_WHILE_REPLACING";
            NonBooleanVariable var = variables.get(name);
            
            if (var != null) {
                switch (op) {
                case "==":
                    replacement = "defined(" + var.getConstantName(value) + ")";
                    break;
                    
                case "!=":
                    replacement = "!defined(" + var.getConstantName(value) + ")";
                    break;
                    
                case ">":
                    value++;
                    // fall through
                case ">=":
                    List<Integer> greaterValuesToAdd = new ArrayList<>(var.getConstants().length);
                    
                    for (int c : var.getConstants()) {
                        if (c >= value) {
                            greaterValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = "(defined(" + var.getConstantName(greaterValuesToAdd.get(0)) + ")";
                    for (int i = 1; i < greaterValuesToAdd.size(); i++) {
                        replacement += "|| defined(" + var.getConstantName(greaterValuesToAdd.get(i)) + ")";
                    }
                    
                    replacement += ")";
                    break;
                    
                case "<":
                    value--;
                    // fall through
                case "<=":
                    List<Integer> lesserValuesToAdd = new ArrayList<>(var.getConstants().length);
                    
                    for (int c : var.getConstants()) {
                        if (c <= value) {
                            lesserValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = "(defined(" + var.getConstantName(lesserValuesToAdd.get(0)) + ")";
                    for (int i = 1; i < lesserValuesToAdd.size(); i++) {
                        replacement += "|| defined(" + var.getConstantName(lesserValuesToAdd.get(i)) + ")";
                    }
                    
                    replacement += ")";
                    break;
                }
            }
            
            result = result.replace(whole, replacement);
        }
        
        
        return result;
    }
    
    private static final Pattern leftSide = Pattern.compile("^(VarTypeC_[0-9]+)\\s*(==|!=|<|>|<=|>=)\\s*(-?[0-9]+).*");
    private static final Pattern comparisonLeft = Pattern.compile("^(VarTypeC_[0-9]+)\\s*(==|!=|<|>|<=|>=).*");
    private static final Pattern comparisonRight = Pattern.compile(".*(==|!=|<|>|<=|>=)\\s*(VarTypeC_[0-9]+)$");
    private static final Pattern name = Pattern.compile("^(VarTypeC_[0-9]+)");
    
    private void printErr(String line, int index) {
        System.err.println(line);
        for (int i = 0; i < index; i++) {
            System.err.print(' ');
        }
        System.err.println('^');
    }
    
    private void putNonBooleanOperation(String variable, String operator, int value) {
        Set<NonBooleanOperation> l = nonBooleanOperations.get(variable);
        if (l == null) {
            l = new HashSet<>();
            nonBooleanOperations.put(variable, l);
        }
        l.add(new NonBooleanOperation(operator, value));
    }
    
    private void collectNonBooleanFromLine(String line) throws IOException {
        int findStart = 0;
        
        int index;
        
        while ((index = line.indexOf("VarTypeC_", findStart)) != -1) {
            findStart = index + 1;
            String left = line.substring(index);
            
            Matcher nameMatcher = name.matcher(left);
            if (!nameMatcher.find()) {
                printErr(line, index);
                findStart = index + 1;
                continue;
            }
            String name = nameMatcher.group(1);
            
            String right = line.substring(0, index + name.length());
            
            Matcher m = leftSide.matcher(left);
            if (m.matches()) {
                putNonBooleanOperation(m.group(1), m.group(2), Integer.parseInt(m.group(3)));
            } else {
                
                if (comparisonLeft.matcher(left).matches() || comparisonRight.matcher(right).matches()) {
                    burntVariables.add(name);
                    printErr(line, index);
                }
                
            }
            
        }
        
    }

}
