package net.ssehub.kernel_haven.code_model.ast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.Variable;

/**
 * Tests the default propagation of {@link ISyntaxElementVisitor}.
 *
 * @author Adam
 */
public class SyntaxElementVisitorTest implements ISyntaxElementVisitor {

    private int cppBlocks;
    
    private int files;
    
    private int functions;
    
    private int singleStatements;
    
    private int compoundStatements;
    
    private int codeLists;
    
    private int typedefs;
    
    private int labels;
    
    private int branchStatements;
    
    private int switchs;
    
    private int cases;
    
    private int loops;
    
    private int comments;
    
    private int cppStatements;
    
    private int codes;
    
    /**
     * Tests the {@link ISyntaxElementVisitor} with the {@link AllAstTests#createFullAst()}.
     */
    @Test
    public void testFullAst() {
        AllAstTests.createFullAst().accept(this);
        
        assertThat(cppBlocks, is(2));
        assertThat(files, is(1));
        assertThat(functions, is(1));
        assertThat(singleStatements, is(6));
        assertThat(compoundStatements, is(1));
        assertThat(codeLists, is(1));
        assertThat(typedefs, is(1));
        assertThat(labels, is(1));
        assertThat(branchStatements, is(3));
        assertThat(switchs, is(1));
        assertThat(cases, is(2));
        assertThat(loops, is(1));
        assertThat(comments, is(1));
        assertThat(cppStatements, is(1));
        assertThat(codes, is(19));
    }

    @Override
    public void visitCppBlock(CppBlock block) {
        cppBlocks++;
        
        if (cppBlocks == 1) {
            assertThat(block.getType(), is(CppBlock.Type.IFDEF));
            assertThat(block.getCondition(), is(new Variable("A")));
        } else if (cppBlocks == 2) {
            assertThat(block.getType(), is(CppBlock.Type.ELSE));
            assertThat(block.getCondition(), is(new Negation(new Variable("A"))));
        }
        
        ISyntaxElementVisitor.super.visitCppBlock(block);
    }
    
    @Override
    public void visitFile(File file) {
        files++;
        ISyntaxElementVisitor.super.visitFile(file);
    }
    
    @Override
    public void visitFunction(Function function) {
        functions++;
        ISyntaxElementVisitor.super.visitFunction(function);
    }

    @Override
    public void visitSingleStatement(SingleStatement statement) {
        singleStatements++;
        ISyntaxElementVisitor.super.visitSingleStatement(statement);
    }

    @Override
    public void visitCompoundStatement(CompoundStatement block) {
        compoundStatements++;
        ISyntaxElementVisitor.super.visitCompoundStatement(block);
    }

    @Override
    public void visitCodeList(CodeList code) {
        codeLists++;
        ISyntaxElementVisitor.super.visitCodeList(code);
    }
    

    @Override
    public void visitTypeDefinition(TypeDefinition typeDef) {
        typedefs++;
        
        assertThat(typeDef.getType(), is(TypeDefinition.TypeDefType.TYPEDEF));
        
        ISyntaxElementVisitor.super.visitTypeDefinition(typeDef);
    }

    @Override
    public void visitLabel(Label label) {
        labels++;
        ISyntaxElementVisitor.super.visitLabel(label);
    }
    
    @Override
    public void visitBranchStatement(BranchStatement branchStatement) {
        branchStatements++;
        
        if (branchStatements == 1) {
            assertThat(branchStatement.getType(), is(BranchStatement.Type.IF));
        } else if (branchStatements == 2) {
            assertThat(branchStatement.getType(), is(BranchStatement.Type.ELSE_IF));
        } else if (branchStatements == 3) {
            assertThat(branchStatement.getType(), is(BranchStatement.Type.ELSE));
        }
        
        assertThat(branchStatement.getSiblingCount(), is(3));
        assertThat(branchStatement.getSibling(0).getType(), is(BranchStatement.Type.IF));
        assertThat(branchStatement.getSibling(1).getType(), is(BranchStatement.Type.ELSE_IF));
        assertThat(branchStatement.getSibling(2).getType(), is(BranchStatement.Type.ELSE));
        
        ISyntaxElementVisitor.super.visitBranchStatement(branchStatement);
    }

    @Override
    public void visitSwitchStatement(SwitchStatement switchStatement) {
        switchs++;
        ISyntaxElementVisitor.super.visitSwitchStatement(switchStatement);
    }

    @Override
    public void visitCaseStatement(CaseStatement caseStatement) {
        cases++;
        
        if (cases == 1) {
            assertThat(caseStatement.getType(), is(CaseStatement.CaseType.CASE));
        } else if (cases == 2) {
            assertThat(caseStatement.getType(), is(CaseStatement.CaseType.DEFAULT));
        }
        
        ISyntaxElementVisitor.super.visitCaseStatement(caseStatement);
    }

    @Override
    public void visitLoopStatement(LoopStatement loop) {
        loops++;
        ISyntaxElementVisitor.super.visitLoopStatement(loop);
    }

    @Override
    public void visitComment(Comment comment) {
        comments++;
        ISyntaxElementVisitor.super.visitComment(comment);
    }

    @Override
    public void visitCppStatement(CppStatement cppStatement) {
        cppStatements++;
        
        assertThat(cppStatement.getType(), is(CppStatement.Type.INCLUDE));
        
        ISyntaxElementVisitor.super.visitCppStatement(cppStatement);
    }
    
    @Override
    public void visitCode(Code code) {
        codes++;
//        ISyntaxElementVisitor.super.visitCode(code);
    }
    
}
