package net.ssehub.kernel_haven.util.logic;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import net.ssehub.kernel_haven.util.Logger;

/**
 * Queue to create balanced (and simplified) {@link Disjunction} terms. <br/>
 * Supported simplification optimizations:
 * <ul>
 * <li>Won't store any new formulas if it stores already an {@link True}</li>
 * <li>Won't store {@link Formula}s twice</li>
 * <li>Won't store a {@link False}</li>
 * </ul>
 * @author El-Sharkawy
 *
 */
public class DisjunctionQueue {
    
    private Queue<Formula> queue = new ArrayDeque<>();
    private boolean isTrue = false;
    private Set<Formula> conditions = new HashSet<>();
    private boolean simplify;
    private Function<Formula, Formula> simplifier;
    
    /**
     * Allows to specify if the above mentioned simplification rules shall be applied, won't use a simplifier for
     * further simplifications of the result.
     * @param simplify Specification if the rules from the class comment shall be applied.
     * @see #DisjunctionQueue(boolean, Function)
     */
    public DisjunctionQueue(boolean simplify) {
        this(simplify, null);
    }
    
    /**
     * Will turn all simplifications on and use the specified <tt>simplifier</tt> to simplify the results of this
     * queue.
     * @param simplifier A function specifying how {@link Formula}s can be further simplified. Probably use:
     * <pre><code>f -> LogicUtils.simplify(f)</code></pre>
     * @see #DisjunctionQueue(boolean, Function)
     */
    public DisjunctionQueue(Function<Formula, Formula> simplifier) {
        this(true, simplifier);
    }
    
    /**
     * Allows specify a <tt>simplifier</tt> to be used on the final results as well as to specify if simplifications
     * should be applied at all.
     * @param simplify Specification if the rules from the class comment shall be applied.
     * @param simplifier A function specifying how {@link Formula}s can be further simplified (will be ignored if
     *     <tt>simplifier = false</tt>). Probably use:
     * <pre><code>f -> LogicUtils.simplify(f)</code></pre>
     */
    public DisjunctionQueue(boolean simplify, Function<Formula, Formula> simplifier) {
        this.simplify = simplify;
        this.simplifier = this.simplify ? simplifier : null;
    }
    
    /**
     * Adds a new formula to the queue.
     * @param condition The formula to add, should not be <tt>null</tt>.
     */
    public void add(Formula condition) {
        if (simplify) {
            // Default case: Avoid doubled formulas and handle TRUE/FALSE literals
            if (!isTrue && !conditions.contains(condition) && !(condition instanceof False)) {
                if (condition instanceof True) {
                    isTrue = true;
                } else {
                    conditions.add(condition);
                    queue.add(condition);
                }
            }
        } else {
            // No optimization
            queue.add(condition);
        }
    }
    
    /**
     * Creates one disjunction term based on the elements passed to the queue. This will also clear all contents from
     * this queue.
     * @return The disjunction, maybe {@link True} if one of the passed elements was {@link True},
     *     never be <tt>null</tt>.
     * @see #getDisjunction(String)
     */
    public Formula getDisjunction() {
        return getDisjunction(null);
    }
    
    /**
     * Creates one disjunction term based on the elements passed to the queue. This will also clear all contents from
     * this queue.
     * @param varName Optional: The name of the variable for which the disjunction is currently be created, this is only
     *     used to create an error log in case of an error.
     * @return The disjunction, maybe {@link True} if one of the passed elements was {@link True},
     *     never be <tt>null</tt>.
     */
    public Formula getDisjunction(String varName) {
        Formula result;
        
        // Create disjunction of all elements
        if (isTrue) {
            result = True.INSTANCE;
        } else {
            // Try to create a balanced, flat tree
            while (queue.size() > 1) {
                queue.add(new Disjunction(queue.poll(), queue.poll()));
            }
            result = queue.poll();
            
            // Simplification if wished and if possible
            if (null != simplifier) {
                result = simplifier.apply(result);
            }
        }
        
        // Check if all elements have been processed
        if (!queue.isEmpty()) {
            if (null != varName) {
                Logger.get().logError("Error while aggregating conditions for " + varName);                
            } else {
                Logger.get().logError("Error while aggregating conditions.");
            }
        }
        
        // Reset
        queue.clear();
        conditions.clear();
        isTrue = false;
        
        return result;
    }
}
