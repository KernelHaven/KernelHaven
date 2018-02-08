package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A visitor for traversing an {@link ISyntaxElement}-based AST. The default operation will visit all nested elements,
 * but won't do any other operation.
 * 
 * @author El-Sharkawy
 */
public interface ISyntaxElementVisitor {
    
    // C-Preprocessor
    
    public default void visitCppBlock(@NonNull CppBlock block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    // C-Code
    
    public default void visitFile(@NonNull File file) {
        for (int i = 0; i < file.getNestedElementCount(); i++) {
            file.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitFunction(@NonNull Function function) {
        function.getHeader().accept(this);
        for (int i = 0; i < function.getNestedElementCount(); i++) {
            function.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitSingleStatement(@NonNull SingleStatement statement) {
        statement.getCode().accept(this);
    }
    
    public default void visitCompoundStatement(@NonNull CompoundStatement block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitCodeList(@NonNull CodeList code) {
        for (int i = 0; i < code.getNestedElementCount(); i++) {
            code.getNestedElement(i).accept(this);
        }
    }
    
    public void visitCode(@NonNull Code code);
    
    public default void visitTypeDefinition(@NonNull TypeDefinition typeDef) {
        typeDef.getDeclaration().accept(this);
        for (int i = 0; i < typeDef.getNestedElementCount(); i++) {
            typeDef.getNestedElement(i).accept(this);
        }
    }

    public default void visitLabel(@NonNull Label label) {
        label.getCode().accept(this);
    }
    
    // C Control structures
    
    public default void visitBranchStatement(@NonNull BranchStatement branchStatement) {
        ICode condition = branchStatement.getIfCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < branchStatement.getNestedElementCount(); i++) {
            branchStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitSwitchStatement(@NonNull SwitchStatement switchStatement) {
        switchStatement.getHeader().accept(this);
        
        for (int i = 0; i < switchStatement.getNestedElementCount(); i++) {
            switchStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitCaseStatement(@NonNull CaseStatement caseStatement) {
        ICode condition = caseStatement.getCaseCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < caseStatement.getNestedElementCount(); i++) {
            caseStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitLoopStatement(@NonNull LoopStatement loop) {
        loop.getLoopCondition().accept(this);
        
        for (int i = 0; i < loop.getNestedElementCount(); i++) {
            loop.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitComment(@NonNull Comment comment) {
        comment.getComment().accept(this);
    }

    public default void visitCppStatement(@NonNull CppStatement cppStatement) {
        ICode expression = cppStatement.getExpression();
        if (expression != null) {
            expression.accept(this);
        }
    }
    
}
