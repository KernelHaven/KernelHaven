package net.ssehub.kernel_haven.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.ssehub.kernel_haven.PipelineConfigurator;
import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.variability_model.FiniteIntegerVariable;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Converts a given non Boolean expression into a Boolean expression.
 * @author Adam
 * @author El-Sharkawy
 */
public class NonBooleanConditionConverter {
    
    private static final String GROUP_NAME_VARIABLE = "variable";
    private static final String GROUP_NAME_OPERATOR = "operator";
    private static final String GROUP_NAME_VALUE = "value";
    
    
    private Pattern leftSideFinder;
    private VariabilityModel varModel;

    public NonBooleanConditionConverter(CodeExtractorConfiguration config) throws SetUpException {
        String variableRegex = config.getProperty("code.extractor.variable_regex");
        try {
            leftSideFinder = Pattern.compile(
                createdNamedCaptureGroup(GROUP_NAME_VARIABLE, variableRegex)
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_OPERATOR, "==|!=|<|>|<=|>=")
                + "\\s*"
                + createdNamedCaptureGroup(GROUP_NAME_VALUE, "-?[0-9]+"));
        } catch (PatternSyntaxException e) {
            throw new SetUpException(e);
        }
        
        varModel = PipelineConfigurator.instance().getVmProvider().getResult();
        if (null == varModel) {
            throw new SetUpException("No Variability Model Provider specified.");
        }
        
        boolean usedFiniteIntegers = false;
        for (VariabilityVariable var : varModel.getVariables()) {
            if (var instanceof FiniteIntegerVariable) {
                usedFiniteIntegers = true;
                break;
            }
        }
        if (!usedFiniteIntegers) {
            throw new SetUpException("Variability Model does not contain "
                + FiniteIntegerVariable.class.getSimpleName() + "s.");
        }
    }
    
    public String replaceInLine(String line) {
        
        String result = line;
        Matcher m = leftSideFinder.matcher(line);
        
        while (m.find()) {
            String whole = m.group();
            String name = m.group(GROUP_NAME_VARIABLE);
            String op = m.group(GROUP_NAME_OPERATOR);
            int value = Integer.parseInt(m.group(GROUP_NAME_VALUE));
            
            String replacement = "ERROR_WHILE_REPLACING";
            FiniteIntegerVariable var = (FiniteIntegerVariable) varModel.getVariableMap().get(name);
            
            if (var != null) {
                switch (op) {
                case "==":
                    replacement = "defined(" + toConstantExpression(var, value) + ")";
                    break;
                    
                case "!=":
                    replacement = "!defined(" + toConstantExpression(var, value) + ")";
                    break;
                    
                case ">":
                    value++;
                    // fall through
                case ">=":
                    List<Integer> greaterValuesToAdd = new ArrayList<>(var.getSizeOfRange());
                    
                    for (int c : var) {
                        if (c >= value) {
                            greaterValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = "(defined(" + toConstantExpression(var, greaterValuesToAdd.get(0)) + ")";
                    for (int i = 1; i < greaterValuesToAdd.size(); i++) {
                        replacement += "|| defined(" + toConstantExpression(var, greaterValuesToAdd.get(i)) + ")";
                    }
                    
                    replacement += ")";
                    break;
                    
                case "<":
                    value--;
                    // fall through
                case "<=":
                    List<Integer> lesserValuesToAdd = new ArrayList<>(var.getSizeOfRange());
                    
                    for (int c : var) {
                        if (c <= value) {
                            lesserValuesToAdd.add(c);
                        }
                    }
                    
                    replacement = "(defined(" + toConstantExpression(var, lesserValuesToAdd.get(0)) + ")";
                    for (int i = 1; i < lesserValuesToAdd.size(); i++) {
                        replacement += "|| defined(" + toConstantExpression(var, lesserValuesToAdd.get(i)) + ")";
                    }
                    
                    replacement += ")";
                    break;
                }
            }
            
            result = result.replace(whole, replacement);
        }
    }
    
    private String toConstantExpression(VariabilityVariable var, int value) {
        return var.getName() + "_eq_" + value;
    }
    
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
}
