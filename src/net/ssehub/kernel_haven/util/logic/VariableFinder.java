package net.ssehub.kernel_haven.util.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collects all used variables from the Formula.
 * @author El-Sharkawy
 *
 */
public class VariableFinder implements IFormulaVisitor<Set<Variable>> {

    private Set<Variable> variables = new HashSet<>();
    
    @Override
    public Set<Variable> visitFalse(False falseConstant) {
        // Not needed
        return variables;
    }

    @Override
    public Set<Variable> visitTrue(True trueConstant) {
        // Not needed
        return variables;
    }

    @Override
    public Set<Variable> visitVariable(Variable variable) {
        variables.add(variable);
        return variables;
    }

    @Override
    public Set<Variable> visitNegation(Negation formula) {
        formula.getFormula().accept(this);
        return variables;
    }

    @Override
    public Set<Variable> visitDisjunction(Disjunction formula) {
        formula.getLeft().accept(this);
        formula.getRight().accept(this);
        return variables;
    }

    @Override
    public Set<Variable> visitConjunction(Conjunction formula) {
        formula.getLeft().accept(this);
        formula.getRight().accept(this);
        return variables;
    }
    
    /**
     * Returns the distinct list of involved variables.
     * @return The variables, which are involved in the visited {@link Formula}.
     */
    public Set<Variable> getVariables() {
        return variables;
    }
    
    /**
     * Returns the distinct list of involved variable <b>names</b>.
     * @return The names of the used variables.
     */
    public List<String> getVariableNames() {
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
