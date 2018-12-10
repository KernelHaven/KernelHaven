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
