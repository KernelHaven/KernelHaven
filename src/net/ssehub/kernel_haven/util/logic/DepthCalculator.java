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
