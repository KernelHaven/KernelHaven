package net.ssehub.kernel_haven.util.logic;

/**
 * Visitor interface for {@link Formula}s.
 * @author El-Sharkawy
 *
 */
public interface IFormulaVisitor {
    
    /**
     * Visits a <tt>FALSE</tt> constant.
     * @param falseConstant The constant expression to visit.
     */

    public void visitFalse(False falseConstant);
    
    /**
     * Visits a <tt>TRUE</tt> constant.
     * @param trueConstant The constant expression to visit.
     */
    public void visitTrue(True trueConstant);
    
    /**
     * Visits a variable.
     * @param variable The variable to visit.
     */
    public void visitVariable(Variable variable);

    /**
     * Visits a negated formula.
     * @param formula The formula to visit.
     */ 
    public void visitNegation(Negation formula);
    /**
     * Visits an <tt>OR</tt> formula.
     * @param formula The formula to visit.
     */
    public void visitDisjunction(Disjunction formula);
    
    /**
     * Visits an <tt>AND</tt> formula.
     * @param formula The formula to visit.
     */
    public void visitConjunction(Conjunction formula);

}
