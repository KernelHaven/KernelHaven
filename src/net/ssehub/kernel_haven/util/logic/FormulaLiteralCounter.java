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
 * Counts the number of literals (the number of involved variables).
 * If a variable is multiple times involved, it will also counted multiple times.
 * 
 * @author Bargfeldt
 */
public class FormulaLiteralCounter implements IFormulaVisitor<@NonNull Integer> {

    @Override
    public @NonNull Integer visitFalse(@NonNull False falseConstant) {
        return 0;
    }

    @Override
    public @NonNull Integer visitTrue(@NonNull True trueConstant) {
        return 0;
    }

    @Override
    public @NonNull Integer visitVariable(@NonNull Variable variable) {
        return 1;
    }

    @Override
    public @NonNull Integer visitNegation(@NonNull Negation formula) {
        return formula.getFormula().accept(this);
    }

    @Override
    public @NonNull Integer visitDisjunction(@NonNull Disjunction formula) {
        int left = formula.getLeft().accept(this);
        int right = formula.getRight().accept(this);
        return left + right;
    }

    @Override
    public @NonNull Integer visitConjunction(@NonNull Conjunction formula) {
        int left = formula.getLeft().accept(this);
        int right = formula.getRight().accept(this);
        return left + right;
    }
    
}
