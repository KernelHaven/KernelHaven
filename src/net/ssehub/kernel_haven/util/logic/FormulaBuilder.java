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

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * Static utility methods for creating {@link Conjunction}s, {@link Disjunction}s and {@link Negation}s with less code
 * to type. These are only shorthands. It is recommended to statically import these methods for extra brevity.
 * </p>
 * <p>
 * Usage example:
 * <code><pre>
 * import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
 * import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;
 * import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
 * 
 * // ...
 * 
 * Formula formula = or("A", and(not("B"), "C"));
 * // equal to: new Disjunction(new Variable("A"),new Conjunction(new Negation(new Variable("B")), new Variable("C"))
 * </pre></code>
 * </p>
 * 
 *
 * @author Adam
 */
public class FormulaBuilder {

    /**
     * Don't allow any instance.
     */
    private FormulaBuilder() {
    }
    
    /**
     * Shorthand for <code>new Conjunction(left, right)</code>.
     * 
     * @param left The left side of the conjunction.
     * @param right The right side of the conjunction.
     * 
     * @return A conjunction of the two terms.
     */
    public static @NonNull Conjunction and(@NonNull Formula left, @NonNull Formula right) {
        return new Conjunction(left, right);
    }
    
    /**
     * Shorthand for <code>new Conjunction(new Variable(left), right)</code>.
     * 
     * @param left The left side of the conjunction.
     * @param right The right side of the conjunction.
     * 
     * @return A conjunction of the two terms.
     */
    public static @NonNull Conjunction and(@NonNull String left, @NonNull Formula right) {
        return new Conjunction(new Variable(left), right);
    }
    
    /**
     * Shorthand for <code>new Conjunction(left, new Variable(right))</code>.
     * 
     * @param left The left side of the conjunction.
     * @param right The right side of the conjunction.
     * 
     * @return A conjunction of the two terms.
     */
    public static @NonNull Conjunction and(@NonNull Formula left, @NonNull String right) {
        return new Conjunction(left, new Variable(right));
    }
    
    /**
     * Shorthand for <code>new Conjunction(new Variable(left), new Variable(right))</code>.
     * 
     * @param left The left side of the conjunction.
     * @param right The right side of the conjunction.
     * 
     * @return A conjunction of the two terms.
     */
    public static @NonNull Conjunction and(@NonNull String left, @NonNull String right) {
        return new Conjunction(new Variable(left), new Variable(right));
    }
    
    /**
     * Shorthand for <code>new Disjunction(left, right)</code>.
     * 
     * @param left The left side of the disjunction.
     * @param right The right side of the disjunction.
     * 
     * @return A disjunction of the two terms.
     */
    public static @NonNull Disjunction or(@NonNull Formula left, @NonNull Formula right) {
        return new Disjunction(left, right);
    }
    
    /**
     * Shorthand for <code>new Disjunction(new Variable(left), right)</code>.
     * 
     * @param left The left side of the disjunction.
     * @param right The right side of the disjunction.
     * 
     * @return A disjunction of the two terms.
     */
    public static @NonNull Disjunction or(@NonNull String left, @NonNull Formula right) {
        return new Disjunction(new Variable(left), right);
    }
    
    /**
     * Shorthand for <code>new Disjunction(left, new Variable(right))</code>.
     * 
     * @param left The left side of the disjunction.
     * @param right The right side of the disjunction.
     * 
     * @return A disjunction of the two terms.
     */
    public static @NonNull Disjunction or(@NonNull Formula left, @NonNull String right) {
        return new Disjunction(left, new Variable(right));
    }
    
    /**
     * Shorthand for <code>new Disjunction(new Variable(left), new Variable(right))</code>.
     * 
     * @param left The left side of the disjunction.
     * @param right The right side of the disjunction.
     * 
     * @return A disjunction of the two terms.
     */
    public static @NonNull Disjunction or(@NonNull String left, @NonNull String right) {
        return new Disjunction(new Variable(left), new Variable(right));
    }
    
    /**
     * Shorthand for <code>new Negation(formula)</code>.
     * 
     * @param formula The formula to be negated.
     * 
     * @return A negation of the formula.
     */
    public static @NonNull Negation not(@NonNull Formula formula) {
        return new Negation(formula);
    }
    
    /**
     * Shorthand for <code>new Negation(new Variable(formula))</code>.
     * 
     * @param formula The formula to be negated.
     * 
     * @return A negation of the formula.
     */
    public static @NonNull Negation not(@NonNull String formula) {
        return new Negation(new Variable(formula));
    }
    
}
