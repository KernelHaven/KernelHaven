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

import java.util.function.Function;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Static utility method for simplifying {@link Formula}s. By default, this only does some simple simplifications.
 * However, a more complex simplifier can be registered via {@link #setSimplifier(Function)} (e.g. LogicUtils in
 * CnfUtils plugin does this).
 *
 * @author Adam
 */
public class FormulaSimplifier {

    private static @NonNull Function<@NonNull Formula, @NonNull Formula> simplifier
            = FormulaSimplifier::defaultSimplifier;
    
    /**
     * Don't allow any instances.
     */
    private FormulaSimplifier() {
    }
    
    /**
     * Changes the simplifier to be used. Overrides the previous one.
     * 
     * @param simplifier The new simplifier to use.
     */
    public static void setSimplifier(@NonNull Function<@NonNull Formula, @NonNull Formula> simplifier) {
        FormulaSimplifier.simplifier = simplifier;
    }
    
    /**
     * Simplifies the given formula. Uses the previously set simplifier (see {@link #setSimplifier(Function)}).
     * 
     * @param formula The formla to simplify.
     * @return The simplified formula.
     */
    public static @NonNull Formula simplify(@NonNull Formula formula) {
        return notNull(simplifier.apply(formula));
    }
    
    /**
     * The default simplifier. Simplifies boolean formulas a bit. The following simplification rules are done:
     * <ul>
     *      <li>{@code NOT(NOT(a)) -> a}</li>
     *      <li>{@code NOT(true) -> false}</li>
     *      <li>{@code NOT(false) -> true}</li>
     *      
     *      <li>{@code true OR a -> true}</li>
     *      <li>{@code a OR true -> true}</li>
     *      <li>{@code false OR false -> false}</li>
     *      <li>{@code a OR false -> a}</li>
     *      <li>{@code false OR a -> a}</li>
     *      <li>{@code a OR a -> a}</li>
     *      
     *      <li>{@code false AND a -> false}</li>
     *      <li>{@code a AND false -> false}</li>
     *      <li>{@code true AND true -> true}</li>
     *      <li>{@code a AND true -> a}</li>
     *      <li>{@code true AND a -> a}</li>
     *      <li>{@code a AND a -> a}</li>
     * </ul>
     * 
     * This ensures that no constants are left after the simplification (except if the whole Formula becomes True or
     * False).
     * 
     * @param formula The formula to simplify.
     * @return A new formula equal to the original, but simplified.
     */
    public static @NonNull Formula defaultSimplifier(@NonNull Formula formula) {
        return DefaultSimplifierVisitor.INSTANCE.visit(formula);
    }
    
    /**
     * The visitor implementing {@link FormulaSimplifier#defaultSimplifier(Formula)}.
     */
    private static class DefaultSimplifierVisitor implements IFormulaVisitor<@NonNull Formula> {

        static final @NonNull DefaultSimplifierVisitor INSTANCE = new DefaultSimplifierVisitor();
        
        @Override
        public @NonNull Formula visitFalse(@NonNull False falseConstant) {
            return falseConstant;
        }

        @Override
        public @NonNull Formula visitTrue(@NonNull True trueConstant) {
            return trueConstant;
        }

        @Override
        public @NonNull Formula visitVariable(@NonNull Variable variable) {
            return variable;
        }

        @Override
        public @NonNull Formula visitNegation(@NonNull Negation formula) {
            Formula nested = formula.getFormula().accept(this);
            
            Formula result;
            
            if (nested instanceof Negation) {
                result = ((Negation) nested).getFormula();
                
            } else if (nested instanceof True) {
                result = False.INSTANCE;
                
            } else if (nested instanceof False) {
                result = True.INSTANCE;
                
            } else {
                // only create new instance if nested actually changed
                if (formula.getFormula() != nested) {
                    result = new Negation(nested);
                } else {
                    result = formula;
                }
            }
            
            return result;
        }

        @Override
        public @NonNull Formula visitDisjunction(@NonNull Disjunction formula) {
            Formula left = formula.getLeft().accept(this);
            Formula right = formula.getRight().accept(this);
            
            Formula result;
            
            if (left instanceof True || right instanceof True) {
                result = True.INSTANCE;
                
            } else if (left instanceof False && right instanceof False) {
                result = False.INSTANCE;
                
            } else if (left instanceof False) {
                result = right;
                
            } else if (right instanceof False) {
                result = left;
                
            } else if (left.equals(right)) {
                result = left;
                
            } else {
                // only create new instance if nested actually changed
                if (formula.getLeft() != left || formula.getRight() != right) {
                    result = new Disjunction(left, right);
                } else {
                    result = formula;
                }
            }
            
            return result;
        }

        @Override
        public @NonNull Formula visitConjunction(@NonNull Conjunction formula) {
            Formula left = formula.getLeft().accept(this);
            Formula right = formula.getRight().accept(this);
            
            Formula result;
            
            if (left instanceof False || right instanceof False) {
                result = False.INSTANCE;
             
            } else if (left instanceof True && right instanceof True) {
                result = True.INSTANCE;
                
            } else if (left instanceof True) {
                result = right;
                
            } else if (right instanceof True) {
                result = left;
                
            } else if (left.equals(right)) {
                result = left;
                
            } else {
                // only create new instance if nested actually changed
                if (formula.getLeft() != left || formula.getRight() != right) {
                    result = new Conjunction(left, right);
                } else {
                    result = formula;
                }
            }
            
            return result;
        }
        
    }
    
}
