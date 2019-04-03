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
 * Determines the depth of a {@link Formula}. The depth is the number of operators for the longest arm of the formula
 * tree.
 * 
 * @author Adam
 */
public class DepthCalculator implements IFormulaVisitor<@NonNull Integer> {

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
        return 0;
    }

    @Override
    public @NonNull Integer visitNegation(@NonNull Negation formula) {
        return 1 + visit(formula.getFormula());
    }

    @Override
    public @NonNull Integer visitDisjunction(@NonNull Disjunction formula) {
        return 1 + Math.max(visit(formula.getLeft()), visit(formula.getRight()));
    }

    @Override
    public @NonNull Integer visitConjunction(@NonNull Conjunction formula) {
        return 1 + Math.max(visit(formula.getLeft()), visit(formula.getRight()));
    }

}
