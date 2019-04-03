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
 * Visitor interface for {@link Formula}s.
 * 
 * @author El-Sharkawy
 * @author Adam
 */
public interface IVoidFormulaVisitor {
    
    /**
     * Visits a <tt>FALSE</tt> constant.
     * 
     * @param falseConstant The constant expression to visit.
     */
    public void visitFalse(@NonNull False falseConstant);
    
    /**
     * Visits a <tt>TRUE</tt> constant.
     * @param trueConstant The constant expression to visit.
     */
    public void visitTrue(@NonNull True trueConstant);
    
    /**
     * Visits a variable.
     * 
     * @param variable The variable to visit.
     */
    public void visitVariable(@NonNull Variable variable);

    /**
     * Visits a negated formula.
     * 
     * @param formula The formula to visit.
     */ 
    public void visitNegation(@NonNull Negation formula);
    /**
     * Visits an <tt>OR</tt> formula.
     * 
     * @param formula The formula to visit.
     */
    public void visitDisjunction(@NonNull Disjunction formula);
    
    /**
     * Visits an <tt>AND</tt> formula.
     * 
     * @param formula The formula to visit.
     */
    public void visitConjunction(@NonNull Conjunction formula);
    
    /**
     * Visits the given formula.
     * 
     * @param formula The formula to visit.
     */
    public default void visit(@NonNull Formula formula) {
        formula.accept(this);
    }

}
