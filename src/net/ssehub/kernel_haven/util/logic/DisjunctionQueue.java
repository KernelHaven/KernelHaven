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
package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Queue to create balanced (and simplified) {@link Disjunction} terms.
 * <p>
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
    
    /**
     * The global singleton {@link Logger}.
     */
    protected static final Logger LOGGER = Logger.get();
    
    /**
     * Whether a {@link True} element was {@link #add(Formula)}ed.
     */
    protected boolean isTrue = false;
    
    /**
     * The queue containing the {@link #add(Formula)}ed formulas.
     */
    protected Queue<@NonNull Formula> queue = new ArrayDeque<>();
    
    private Set<Formula> conditions = new HashSet<>();
    private boolean simplify;
    private Function<@NonNull Formula, @NonNull Formula> simplifier;
    
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
     * {@code f -> LogicUtils.simplify(f)}
     * @see #DisjunctionQueue(boolean, Function)
     */
    public DisjunctionQueue(Function<@NonNull Formula, @NonNull Formula> simplifier) {
        this(true, simplifier);
    }
    
    /**
     * Allows specify a <tt>simplifier</tt> to be used on the final results as well as to specify if simplifications
     * should be applied at all.
     * @param simplify Specification if the rules from the class comment shall be applied.
     * @param simplifier A function specifying how {@link Formula}s can be further simplified (will be ignored if
     *     <tt>simplifier = false</tt>). Probably use:
     * <pre>{@code f -> LogicUtils.simplify(f)}</pre>
     */
    public DisjunctionQueue(boolean simplify, Function<@NonNull Formula, @NonNull Formula> simplifier) {
        this.simplify = simplify;
        this.simplifier = this.simplify ? simplifier : null;
    }
    
    /**
     * Adds a new formula to the queue.
     * @param condition The formula to add (<tt>null</tt> will be ignored).
     */
    public void add(@Nullable Formula condition) {
        if (null != condition) {
            if (simplify) {
                // Default case: Avoid doubled formulas and handle TRUE/FALSE literals
                if (!isTrue && !conditions.contains(condition)) {
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
    }
    
    /**
     * Creates one disjunction term based on the elements passed to the queue. This will also clear all contents from
     * this queue.
     * 
     * @return The disjunction, maybe {@link True} if one of the passed elements was {@link True},
     *     never be <tt>null</tt>.
     * @see #getDisjunction(String)
     */
    public @NonNull Formula getDisjunction() {
        return getDisjunction(null);
    }
    
    /**
     * Creates one disjunction term based on the elements passed to the queue. This will also clear all contents from
     * this queue.
     * 
     * @param varName Optional: The name of the variable for which the disjunction is currently be created, this is only
     *     used to create an error log in case of an error.
     * @return The disjunction, maybe {@link True} if one of the passed elements was {@link True},
     *     never be <tt>null</tt>.
     */
    public @NonNull Formula getDisjunction(@Nullable String varName) {
        Formula result;
        
        // Create disjunction of all elements
        if (isTrue) {
            result = True.INSTANCE;
        } else if (queue.isEmpty()) {
            result = False.INSTANCE;
        } else {
            // Try to create a balanced, flat tree
            while (queue.size() > 1) {
                queue.add(new Disjunction(queue.poll(), queue.poll()));
            }
            result = notNull(queue.poll()); // this can't be null, since queue.size()==1
            
            // Simplification if wished and if possible
            if (null != simplifier) {
                result = notNull(simplifier.apply(result));
            }
        }
        
        // Reset
        reset();
        
        return result;
    }
    
    /**
     * Resets this {@link DisjunctionQueue}. After this, the queue is in the same state as if it was newly created.
     */
    public void reset() {
        queue.clear();
        conditions.clear();
        isTrue = false;
    }
    
}
