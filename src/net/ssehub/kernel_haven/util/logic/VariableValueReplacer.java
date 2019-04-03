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
 * Replaces each occurrence of a variable with a constant.
 * Returns a new Formula equal to the given formula, but with each occurrence of the variable replaced.
 * The Formula is not altered.
 * 
 * @author Bargfeldt
 */
public class VariableValueReplacer implements IFormulaVisitor<@NonNull Formula> {

    private @NonNull String variable;
    private boolean value;
    private boolean exactMatch;
   
    /**
     * Constructor for VariableValueReplacer.
     * @param variable The variable to replace.
     * @param value Which constant the variable should be replaced with.
     * @param exactMatch Whether the variable name has to match exactly. If <code>false</code>, then startsWith()
     *      is used to find matches to replace.
     */
    public VariableValueReplacer(@NonNull String variable, boolean value, boolean exactMatch) {
        this.variable = variable;
        this.value = value;
        this.exactMatch = exactMatch;
    }

    @Override
    public @NonNull Formula visitFalse(@NonNull False falseConstant) {
        return falseConstant;
    }

    @Override
    public @NonNull Formula visitTrue(@NonNull True trueConstant) {
        return trueConstant;
    }

    @Override
    public @NonNull Formula visitVariable(@NonNull Variable var) {
        Formula result;
        boolean replace;
        if (exactMatch) {
            replace = var.getName().equals(variable);
        } else {
            replace = var.getName().startsWith(variable);
        }
        
        if (replace) {
            result = (value ? True.INSTANCE : False.INSTANCE);
        } else {
            result = var;
        }
        return result;
    }

    @Override
    public @NonNull Formula visitNegation(@NonNull Negation formula) {
        return new Negation(formula.getFormula().accept(this));
    }

    @Override
    public @NonNull Formula visitDisjunction(@NonNull Disjunction formula) {
        return new Disjunction(
                formula.getLeft().accept(this),
                formula.getRight().accept(this));
    }

    @Override
    public @NonNull Formula visitConjunction(@NonNull Conjunction formula) {
        return new Conjunction(
                formula.getLeft().accept(this),
                formula.getRight().accept(this));
    }
    
}
