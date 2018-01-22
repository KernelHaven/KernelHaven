package net.ssehub.kernel_haven.util.logic;

import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Evaluates a {@link Formula} based on a mapping <code>variable name -&gt; boolean value</code>.
 * <code>null</code> values indicate an undefined value.
 * 
 * @author Adam
 */
public class FormulaEvaluator implements IFormulaVisitor<@Nullable Boolean> {

    private @NonNull Map<String, Boolean> variableValues;
    
    /**
     * Creates this {@link FormulaEvaluator} with the given variable value mapping.
     * 
     * @param variableValues The mapping variable name -&gt; boolean value.
     */
    public FormulaEvaluator(@NonNull Map<String, @NonNull Boolean> variableValues) {
        this.variableValues = variableValues;
    }
    
    @Override
    public @Nullable Boolean visitFalse(@NonNull False falseConstant) {
        return false;
    }

    @Override
    public @Nullable Boolean visitTrue(@NonNull True trueConstant) {
        return true;
    }

    @Override
    public @Nullable Boolean visitVariable(@NonNull Variable variable) {
        return variableValues.get(variable.getName());
    }

    @Override
    public @Nullable Boolean visitNegation(@NonNull Negation formula) {
        Boolean result = formula.getFormula().accept(this);
        return result == null ? null : !result;
    }

    @Override
    public @Nullable Boolean visitDisjunction(@NonNull Disjunction formula) {
        Boolean left = formula.getLeft().accept(this);
        Boolean right = formula.getRight().accept(this);
        
        Boolean result;
        
        if (left == Boolean.TRUE || right == Boolean.TRUE) {
            result = true;
        } else if (left == null || right == null) {
            result = null;
        } else {
            result = false;
        }
        
        return result;
    }

    @Override
    public @Nullable Boolean visitConjunction(@NonNull Conjunction formula) {
        Boolean left = formula.getLeft().accept(this);
        Boolean right = formula.getRight().accept(this);
        
        Boolean result;
        
        if (left == Boolean.FALSE || right == Boolean.FALSE) {
            result = false;
        } else if (left == null || right == null) {
            result = null;
        } else {
            result = true;
        }
        
        return result;
    }

}
