package de.uni_hildesheim.sse.kernel_haven.util.logic.parser;

import java.util.HashMap;
import java.util.Map;

import de.uni_hildesheim.sse.kernel_haven.util.logic.Variable;

/**
 * A cache to help ensure that a {@link Formula} does not contain duplicate
 * {@link Variable} objects with the same name. Each instance of {@link Variable}
 * should always be obtained through {@link #getVariable(String)}.
 * 
 * @author Adam (from KernelMiner project)
 */
public class VariableCache {

    private Map<String, Variable> variables;
    
    /**
     * Initializes an empty cache.
     */
    public VariableCache() {
        variables = new HashMap<>();
    }
    
    /**
     * Retrieves a variable from the cache. If no variable with this name is present
     * yet, then a variable with this name is created and added to the cache.
     * 
     * @param name The name of the variable.
     * @return The instance of the variable with the given name.
     */
    public Variable getVariable(String name) {
        Variable var = variables.get(name);
        if (var == null) {
            var = new Variable(name);
            variables.put(name, var);
        }
        return var;
    }
    
    /**
     * Clears this cache.
     */
    public void clear() {
        variables.clear();
    }
    
    /**
     * Returns the number of variables in this cache.
     * 
     * @return The number of cached variables.
     */
    public int getNumVariables() {
        return variables.size();
    }
    
}
