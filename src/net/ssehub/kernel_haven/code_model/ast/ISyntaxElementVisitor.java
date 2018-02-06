package net.ssehub.kernel_haven.code_model.ast;

/**
 * A visitor for traversing an {@link SyntaxElement}-based AST. The default operation will visit all nested elements,
 * but won't do any other operation.
 * @author El-Sharkawy
 *
 */
public interface ISyntaxElementVisitor {
    
    // C-Preprocessor
    
    public default void visitCppBlock(CppBlock block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    // C-Code
    
    public default void visitFile(File file) {
        for (int i = 0; i < file.getNestedElementCount(); i++) {
            file.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitFunction(Function function) {
        function.getHeader().accept(this);
        for (int i = 0; i < function.getNestedElementCount(); i++) {
            function.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitSingleStatement(SingleStatement statement) {
        statement.getCode().accept(this);
    }
    
    public default void visitCompoundStatement(CompoundStatement block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitCodeList(CodeList code) {
        for (int i = 0; i < code.getNestedElementCount(); i++) {
            code.getNestedElement(i).accept(this);
        }
    }
    
    public void visitCode(Code code);
    
    public default void visitTypeDefinition(TypeDefinition typeDef) {
        typeDef.getDeclaration().accept(this);
        for (int i = 0; i < typeDef.getNestedElementCount(); i++) {
            typeDef.getNestedElement(i).accept(this);
        }
    }

    public default void visitLabel(Label label) {
        label.getCode().accept(this);
    }
    
    // C Control structures
    
    public default void visitBranchStatement(BranchStatement branchStatement) {
        SyntaxElement condition = branchStatement.getIfCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < branchStatement.getNestedElementCount(); i++) {
            branchStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitSwitchStatement(SwitchStatement switchStatement) {
        switchStatement.getHeader().accept(this);
        
        for (int i = 0; i < switchStatement.getNestedElementCount(); i++) {
            switchStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitCaseStatement(CaseStatement caseStatement) {
        SyntaxElement condition = caseStatement.getCaseCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < caseStatement.getNestedElementCount(); i++) {
            caseStatement.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitLoopStatement(LoopStatement loop) {
        loop.getLoopCondition().accept(this);
        
        for (int i = 0; i < loop.getNestedElementCount(); i++) {
            loop.getNestedElement(i).accept(this);
        }
    }
    
    public default void visitComment(Comment comment) {
        comment.getComment().accept(this);
    }

    public default void visitCppStatement(CppStatement cppStatement) {
        SyntaxElement expression = cppStatement.getExpression();
        if (expression != null) {
            expression.accept(this);
        }
    }
    
}
