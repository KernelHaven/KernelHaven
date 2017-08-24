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
import java.util.regex.PatternSyntaxException;
import java.util.stream.LongStream;

import net.ssehub.kernel_haven.PipelineConfigurator;
import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.variability_model.FiniteIntegerVariable;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

//TODO: tidy up this temporary hack
//CHECKSTYLE:OFF

/**
 * @author Adam
 * @author El-Sharkawy
 */
// TODO SE: @Adam please check whether and how NonBooleanConditionConverter can be used/integrated
public class NonBooleanPreperation {
    
    static final String DESTINATION_CONFIG = "prepare_non_boolean.destination";
    static final String VAR_IDENTIFICATION_REGEX_CONFIG = "code.extractor.variable_regex";
    
    private static final String GROUP_NAME_VARIABLE = "variable";
    private static final String GROUP_NAME_OPERATOR = "operator";
    private static final String GROUP_NAME_VALUE = "value";
    private static final String SUPPORTED_OPERATORS_REGEX = "==|!=|<|>|<=|>=";
    
    private static final Logger LOGGER = Logger.get();
    
    private File originalSourceTree;
    
    private File copiedSourceTree;
    
    private Map<String, Set<NonBooleanOperation>> nonBooleanOperations;
    
    private Map<String, NonBooleanVariable> variables;
    
    private Set<String> burntVariables;
    
    private Pattern variableNamePattern;
    private Pattern leftSide;
    
    /**
     * Checks that a variable stands on the <b>left</b> side of an expression.<br/>
     * <tt> &lt;variable&gt; &lt;operator&gt; * </tt>
     */
    private Pattern comparisonLeft;
    
    /**
     * Checks that a variable stands on the <b>right</b> side of an expression.<br/>
     * <tt> * &lt;operator&gt; &lt;variable&gt; </tt>
     */
    private Pattern comparisonRight;
    private Pattern leftSideFinder;
    private Pattern twoVariablesExpression;
    
    private boolean nonBooleanModelRead = false;
    
    /**
     * Creates a named capturing group.
     * @param groupName The name of the captured group.
     * @param groupContents The sub RegEx of the group without parenthesis.
     * 
     * @return A named capturing group, with enclosing parenthesis.
     */
    private static String createdNamedCaptureGroup(String groupName, String groupContents) {
        return "(?<" + groupName + ">" + groupContents + ")";
    }
    
    public void run(CodeExtractorConfiguration config) throws SetUpException {
        copiedSourceTree = new File(config.getProperty(DESTINATION_CONFIG));
        originalSourceTree = config.getSourceTree();
        
        String variableRegex = config.getProperty(VAR_IDENTIFICATION_REGEX_CONFIG);
        if (variableRegex == null) {
            throw new SetUpException(VAR_IDENTIFICATION_REGEX_CONFIG + " not defined");
        }
        
        try {
            variableNamePattern = Pattern.compile(variableRegex);
            
            leftSide = Pattern.compile("^"
                + createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, SUPPORTED_OPERATORS_REGEX)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_VALUE, "-?[0-9]+")
                + ".*");

            comparisonLeft = Pattern.compile("^"
                + createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, SUPPORTED_OPERATORS_REGEX)
                + ".*");
            
            comparisonRight = Pattern.compile(".*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, SUPPORTED_OPERATORS_REGEX)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "$");
            
            leftSideFinder = Pattern.compile(
                createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, SUPPORTED_OPERATORS_REGEX)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_VALUE, "-?[0-9]+"));
            
            twoVariablesExpression = Pattern.compile(
                createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, SUPPORTED_OPERATORS_REGEX)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_VALUE, variableRegex));
        } catch (PatternSyntaxException e) {
            throw new SetUpException(e);
        }
        
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
        
        private long[] constants;
        
        public NonBooleanVariable(String name, Set<Long> constants) {
            this.name = name;
            this.constants = new long[constants.size()];
            int i = 0;
            for (Long c : constants) {
                this.constants[i++] = c;
            }
        }
        
        public long[] getConstants() {
            return constants;
        }
        
        public String getConstantName(long constant) {
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
        
        private long value;

        /**
         * .
         * @param operator .
         * @param value .
         */
        public NonBooleanOperation(String operator, long value) {
            this.operator = operator;
            this.value = value;
        }
        
        @Override
        public int hashCode() {
            return operator.hashCode() + Long.hashCode(value);
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
        
        gatherConstantValues();
        
        LOGGER.logInfo("Burnt variables: " + burntVariables);
        LOGGER.logInfo("Variables: " + variables);
        
        
        LOGGER.logDebug("Copying from " + originalSourceTree.getAbsolutePath() + " to " + copiedSourceTree.getAbsolutePath());
        copy(originalSourceTree, copiedSourceTree);
    }

    private void gatherConstantValues() {
        VariabilityModel varModel = PipelineConfigurator.instance().getVmProvider().getResult();
        nonBooleanModelRead = false;
        
        // Try to use information of variability model -> exact approach
        if (null != varModel) {
            for (VariabilityVariable variable : varModel.getVariables()) {
                Set<Long> requiredConstants = new HashSet<>();
                if (null != variable && variable instanceof FiniteIntegerVariable) {
                    nonBooleanModelRead = true;
                    FiniteIntegerVariable intVar = (FiniteIntegerVariable) variable;
                    for (int i = 0; i < intVar.getSizeOfRange(); i++) {
                        requiredConstants.add((long) intVar.getValue(i));
                    }
                    variables.put(variable.getName(), new NonBooleanVariable(variable.getName(), requiredConstants));
                }
            }
        }
        
        if (!nonBooleanModelRead) {
            // No variability model available -> use heuristic (use gathered values from code)
            for (Map.Entry<String, Set<NonBooleanOperation>> entry : nonBooleanOperations.entrySet()) {
                Set<Long> requiredConstants = new HashSet<>();
                
                // SE: Integration of non-Boolean VarModel
                if (null != varModel) {
                    VariabilityVariable var = varModel.getVariableMap().get(entry.getKey());
                    if (null != var && var instanceof FiniteIntegerVariable) {
                        nonBooleanModelRead = true;
                        FiniteIntegerVariable intVar = (FiniteIntegerVariable) var;
                        for (int i = 0; i < intVar.getSizeOfRange(); i++) {
                            requiredConstants.add((long) intVar.getValue(i));
                        }
                    }
                }
                
                if (!nonBooleanModelRead) {
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
                }
                
                variables.put(entry.getKey(), new NonBooleanVariable(entry.getKey(), requiredConstants));
            }
        }
    }
    
    private NonBooleanVariable getVariableForced(String name) {
        NonBooleanVariable variable = variables.get(name);
        if (null == variable) {
            variable = new NonBooleanVariable(name, new HashSet<Long>());
            variables.put(name, variable);
        }
        
        return variable;
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
                        
                        // Replace variable occurrences of #if's and #elif's
                        if (CPPUtils.isIfOrElifStatement(line)) {
                            
                            // Consider continuation
                            while (line.charAt(line.length() - 1) == '\\') {
                                String tmp = in.readLine();
                                if (null != tmp) {
                                    line += tmp;
                                } else {
                                    break;
                                }
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
    
    private String replaceInLine(String line) {
        String result = line;
        Matcher m = leftSideFinder.matcher(line);
        while (m.find()) {
            String whole = m.group();
            String name = m.group(GROUP_NAME_VARIABLE);
            String op = m.group(GROUP_NAME_OPERATOR);
            long value = Long.parseLong(m.group(GROUP_NAME_VALUE));
            
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
                    List<Long> greaterValuesToAdd = new ArrayList<>(var.getConstants().length);
                    
                    for (long c : var.getConstants()) {
                        if (c >= value) {
                            greaterValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = expandComparison(var, greaterValuesToAdd);
                    
                    break;
                    
                case "<":
                    value--;
                    // fall through
                case "<=":
                    List<Long> lesserValuesToAdd = new ArrayList<>(var.getConstants().length);
                    
                    for (long c : var.getConstants()) {
                        if (c <= value) {
                            lesserValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = expandComparison(var, lesserValuesToAdd);
                    
                    break;
                }
            }
            
            result = result.replace(whole, replacement);
        }
        
        
        // Check if it is a comparison between two variables and try it again
        m = twoVariablesExpression.matcher(line);
        while (m.find()) {
            String whole = m.group();
            String firstVar = m.group(GROUP_NAME_VARIABLE);
            String op = m.group(GROUP_NAME_OPERATOR);
            String secondVar = m.group(GROUP_NAME_VALUE);
            
            NonBooleanVariable var1 = getVariableForced(firstVar);
            NonBooleanVariable var2 = getVariableForced(secondVar);
            String replacement = whole;
            
            if (var1.constants.length > 0 || var2.constants.length > 0) {
                switch (op) {
                case "==":
                    String expaned = expandComparison(var1, var2);
                    if (null != expaned) {
                        replacement = expaned;
                    }
                    
                    LOGGER.logDebug("Exchanged", whole, "to", replacement);
                    break;
                    
                default :
                    LOGGER.logWarning("Could not prepare non boolean expression because of unsuppoted type: " + whole);
                    break;
                }
            }
            result = result.replace(whole, replacement);
        }
        
        return result;
    }

    /**
     * Creates a disjunction constraints containing comparisons for all values passed to this method.
     * @param var A variable for which multiple comparisons shall be created for.
     * @param legalValues The values which shall be added to the comparison.
     * @return One Boolean disjunction expression.
     */
    private String expandComparison(NonBooleanVariable var, List<Long> legalValues) {
        String replacement;
        if (!legalValues.isEmpty()) {
            replacement = "(defined(" + var.getConstantName(legalValues.get(0)) + ")";
            for (int i = 1; i < legalValues.size(); i++) {
                replacement += " || defined(" + var.getConstantName(legalValues.get(i)) + ")";
            }
            replacement += ")";
        } else {
            replacement = "0";
            // I think an exception would be more appropriate
            LOGGER.logWarning("Could not replace values for variable: " + var.name);
        }
        return replacement;
    }
    
    private String expandComparison(NonBooleanVariable var1, NonBooleanVariable var2) {
        StringBuffer replacement = null;
        List<Long> sameConstants = new ArrayList<>();
        List<Long> constantsOfVar1 = new ArrayList<>();
        List<Long> constantsOfVar2 = new ArrayList<>();
        
        for (int i = 0; i < var1.constants.length; i++) {
            long c = var1.constants[i];
            boolean contains = LongStream.of(var2.constants).anyMatch(x -> x == c);
            if (contains) {
                sameConstants.add(c);
            } else {
                constantsOfVar1.add(c);
            }
        }
        for (int i = 0; i < var2.constants.length; i++) {
            long c = var2.constants[i];
            if (!sameConstants.contains(c)) {
                constantsOfVar2.add(c);
                
            }
        }
        
        if (nonBooleanModelRead) {
            /*
             * We can expect that our list is complete, if the variability model was imported
             * Prohibit illegal values
             */
            if (!sameConstants.isEmpty()) {
                // There exist an overlapping
                
                replacement = new StringBuffer("((");
                appendTwoEqualValues(replacement, var1, var2, sameConstants.get(0));
                // Add supported equalities
                for (int i = 1; i < sameConstants.size(); i++) {
                    replacement.append(" || ");
                    appendTwoEqualValues(replacement, var1, var2, sameConstants.get(i));
                }
                
                replacement.append(")"); // First bracket
                // Disallow elements which are not part of the intersection
                for (int i = 0; i < constantsOfVar1.size(); i++) {
                    replacement.append(" && !defined(");
                    replacement.append(var1.getConstantName(constantsOfVar1.get(i)));
                    replacement.append(")");
                }
                for (int i = 0; i < constantsOfVar2.size(); i++) {
                    replacement.append(" && !defined(");
                    replacement.append(var2.getConstantName(constantsOfVar2.get(i)));
                    replacement.append(")");
                }
                
                replacement.append(")"); // Second bracket
            } else {
                // There exist no overlapping
                replacement = new StringBuffer(" 0 ");
                LOGGER.logWarning(var1.name + " == " + var2.name + " could not be replaced, since the ranges do not"
                    + " overlap!");
            }
            
        } else {
            /*
             * Heuristic was used to gather constants, maybe we don't know all constants.
             * Create union of all constants and allow all combinations
             * TODO: This algorithm is ultra ugly
             */
            Set<Long> tmpUnion = new HashSet<>(sameConstants);
            tmpUnion.addAll(constantsOfVar1);
            tmpUnion.addAll(constantsOfVar2);
            sameConstants.clear();
            sameConstants.addAll(tmpUnion);
            
            replacement = new StringBuffer("(");
            appendTwoEqualValues(replacement, var1, var2, sameConstants.get(0));
            // Add supported equalities
            for (int i = 1; i < sameConstants.size(); i++) {
                replacement.append(" || ");
                appendTwoEqualValues(replacement, var1, var2, sameConstants.get(i));
            }
            replacement.append(")");
        }
        
        return null != replacement ? replacement.toString() : null;
    }
    
    private void appendTwoEqualValues(StringBuffer replacement, NonBooleanVariable var1, NonBooleanVariable var2,
        long constant) {
        
        replacement.append("(defined(");
        replacement.append(var1.getConstantName(constant));
        replacement.append(") && defined(");
        replacement.append(var2.getConstantName(constant));
        replacement.append("))");
        
    }
    
    private void printErr(String line, int index) {
        System.err.println(line);
        for (int i = 0; i < index; i++) {
            System.err.print(' ');
        }
        System.err.println('^');
    }
    
    private void putNonBooleanOperation(String variable, String operator, long value) {
        Set<NonBooleanOperation> l = nonBooleanOperations.get(variable);
        if (l == null) {
            l = new HashSet<>();
            nonBooleanOperations.put(variable, l);
        }
        l.add(new NonBooleanOperation(operator, value));
    }
    
    /**
     * Preparation phase: Collects variables and required constants.
     * @param line A CPP expression (e.g. if expression).
     * @throws IOException
     */
    private void collectNonBooleanFromLine(String line) throws IOException {
        Matcher variableNameMatcher = variableNamePattern.matcher(line);
        
        while (variableNameMatcher.find()) {
            int index = variableNameMatcher.start();
            String left = line.substring(index);
            
            String name = variableNameMatcher.group();
            
            String right = line.substring(0, index + name.length());
            
            Matcher m = leftSide.matcher(left);
            if (m.matches()) {
                // Expression is in form of: <variable> <operator> <constant>
                putNonBooleanOperation(m.group(GROUP_NAME_VARIABLE), m.group(GROUP_NAME_OPERATOR),
                    Long.parseLong(m.group(GROUP_NAME_VALUE)));
            } else {
                boolean leftMatch = comparisonLeft.matcher(left).matches();
                boolean rightMatch = comparisonRight.matcher(right).matches();
                if (leftMatch || rightMatch) {
                    burntVariables.add(name);
                    printErr(line, index);
                }
                
            }
            
        }
        
    }

}
