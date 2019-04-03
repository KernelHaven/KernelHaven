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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Collects all used variables from the Formula.
 * @author El-Sharkawy
 *
 */
public class VariableFinder implements IFormulaVisitor<Set<Variable>> {

    private @NonNull Set<@NonNull Variable> variables = new HashSet<>();
    
    @Override
    public @NonNull Set<Variable> visitFalse(@NonNull False falseConstant) {
        // Not needed
        return variables;
    }

    @Override
    public @NonNull Set<Variable> visitTrue(@NonNull True trueConstant) {
        // Not needed
        return variables;
    }

    @Override
    public @NonNull Set<Variable> visitVariable(@NonNull Variable variable) {
        variables.add(variable);
        return variables;
    }

    @Override
    public @NonNull Set<Variable> visitNegation(@NonNull Negation formula) {
        formula.getFormula().accept(this);
        return variables;
    }

    @Override
    public @NonNull Set<Variable> visitDisjunction(@NonNull Disjunction formula) {
        formula.getLeft().accept(this);
        formula.getRight().accept(this);
        return variables;
    }

    @Override
    public @NonNull Set<Variable> visitConjunction(@NonNull Conjunction formula) {
        formula.getLeft().accept(this);
        formula.getRight().accept(this);
        return variables;
    }
    
    /**
     * Returns the distinct list of involved variables.
     * @return The variables, which are involved in the visited {@link Formula}.
     */
    public @NonNull Set<@NonNull Variable> getVariables() {
        return variables;
    }
    
    /**
     * Returns the distinct list of involved variable <b>names</b>.
     * @return The names of the used variables.
     */
    public @NonNull List<String> getVariableNames() {
        List<String> names = new ArrayList<>();
        for (Variable variable: variables) {
            names.add(variable.getName());
        }
        return names;
    }
    
    /**
     * Facilitate reuse of this instance as it clears all collected elements.
     */
    public void clear() {
        variables.clear();
    }

}
