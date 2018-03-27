package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.not;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.code_model.ast.BranchStatement.Type;
import net.ssehub.kernel_haven.code_model.ast.TypeDefinition.TypeDefType;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests for code_model package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AbstractSyntaxElementTest.class,
    SyntaxElementVisitorTest.class,
    })
public class AllAstTests {

    /**
     * Creates an AST containing all available classes.
     * 
     * @return An AST with all available classes.
     */
    // CHECKSTYLE:OFF // method is too long...
    public static @NonNull ISyntaxElement createFullAst() {
    //CHECKSTYLE:ON
        java.io.File sourceFile = new java.io.File("dummy_test.c");
        
        // File
        File file = new File(True.INSTANCE, sourceFile);
        file.setSourceFile(sourceFile);
        file.setCondition(True.INSTANCE);
        
        CppStatement include = new CppStatement(True.INSTANCE, CppStatement.Type.INCLUDE,
                makeCode("< stdio.h >", sourceFile, True.INSTANCE, True.INSTANCE));
        include.setCondition(True.INSTANCE);
        include.setSourceFile(sourceFile);
        file.addNestedElement(include);
        
        // Function 1
        Function f1 = new Function(True.INSTANCE, "simpleFunction",
                makeCode("void simpleFunction ( )", sourceFile, True.INSTANCE, True.INSTANCE));
        f1.setSourceFile(sourceFile);
        f1.setCondition(True.INSTANCE);
        file.addNestedElement(f1);
        
        // If
        BranchStatement ifStmt = new BranchStatement(True.INSTANCE, Type.IF, 
                makeCode("if ( 1 > 2 )", sourceFile, True.INSTANCE, True.INSTANCE));
        ifStmt.setSourceFile(sourceFile);
        ifStmt.setCondition(True.INSTANCE);
        
        SingleStatement insideIf = new SingleStatement(True.INSTANCE,
                makeCode("return 1 ;", sourceFile, True.INSTANCE, True.INSTANCE));
        insideIf.setSourceFile(sourceFile);
        insideIf.setCondition(True.INSTANCE);
        
        CompoundStatement ifBody = new CompoundStatement(True.INSTANCE);
        ifBody.setSourceFile(sourceFile);
        ifBody.setCondition(True.INSTANCE);
        ifBody.addNestedElement(insideIf);
        ifStmt.addNestedElement(ifBody);

        // Elif
        BranchStatement elif = new BranchStatement(True.INSTANCE, Type.ELSE_IF, 
                makeCode("else if ( 1 < 2 )", sourceFile, True.INSTANCE, True.INSTANCE));
        elif.setSourceFile(sourceFile);
        elif.setCondition(True.INSTANCE);
        
        SingleStatement elifBody = new SingleStatement(True.INSTANCE, 
                makeCode(";", sourceFile, True.INSTANCE, True.INSTANCE));
        elifBody.setSourceFile(sourceFile);
        elifBody.setCondition(True.INSTANCE);
        elif.addNestedElement(elifBody);
        
        ifStmt.addNestedElement(elif);
        
        // Else
        BranchStatement elseStmt = new BranchStatement(True.INSTANCE, Type.ELSE, null);
        elseStmt.setSourceFile(sourceFile);
        elseStmt.setCondition(True.INSTANCE);
        SingleStatement elseBody = new SingleStatement(True.INSTANCE, 
                makeCode(";", sourceFile, True.INSTANCE, True.INSTANCE));
        elseBody.setSourceFile(sourceFile);
        elseBody.setCondition(True.INSTANCE);
        elseStmt.addNestedElement(elseBody);
        
        ifStmt.addNestedElement(elseStmt);
        
        // siblings
        ifStmt.addSibling(ifStmt);
        ifStmt.addSibling(elif);
        ifStmt.addSibling(elseStmt);
        elif.addSibling(ifStmt);
        elif.addSibling(elif);
        elif.addSibling(elseStmt);
        elseStmt.addSibling(ifStmt);
        elseStmt.addSibling(elif);
        elseStmt.addSibling(elseStmt);
        f1.addNestedElement(ifStmt);
        
        // label
        Label label = new Label(True.INSTANCE, makeCode("lbl:", sourceFile, True.INSTANCE, True.INSTANCE));
        label.setSourceFile(sourceFile);
        label.setCondition(True.INSTANCE);
        f1.addNestedElement(label);
        
        // swtich
        SwitchStatement switchStmt = new SwitchStatement(True.INSTANCE,
                makeCode("switch ( 3 )", sourceFile, True.INSTANCE, True.INSTANCE));
        switchStmt.setCondition(True.INSTANCE);
        switchStmt.setSourceFile(sourceFile);
        f1.addNestedElement(switchStmt);
        
        // case 1
        CaseStatement case1 = new CaseStatement(True.INSTANCE, makeCode("1", sourceFile, True.INSTANCE, True.INSTANCE),
                CaseStatement.CaseType.CASE, switchStmt);
        case1.setCondition(True.INSTANCE);
        case1.setSourceFile(sourceFile);
        
        SingleStatement caseBody = new SingleStatement(True.INSTANCE, 
                makeCode(";", sourceFile, True.INSTANCE, True.INSTANCE));
        caseBody.setSourceFile(sourceFile);
        caseBody.setCondition(True.INSTANCE);
        case1.addNestedElement(caseBody);
        switchStmt.addNestedElement(case1);
        switchStmt.addCase(case1);
        
        // default
        CaseStatement defaultStmt = new CaseStatement(True.INSTANCE, null,  CaseStatement.CaseType.DEFAULT, switchStmt);
        defaultStmt.setCondition(True.INSTANCE);
        defaultStmt.setSourceFile(sourceFile);
        
        SingleStatement defaultBody = new SingleStatement(True.INSTANCE, 
                makeCode(";", sourceFile, True.INSTANCE, True.INSTANCE));
        defaultBody.setSourceFile(sourceFile);
        defaultBody.setCondition(True.INSTANCE);
        defaultStmt.addNestedElement(defaultBody);
        switchStmt.addNestedElement(defaultStmt);
        switchStmt.addCase(defaultStmt);
        
        // assignment with CppBlock
        CodeList codeList = new CodeList(True.INSTANCE);
        codeList.setCondition(True.INSTANCE);
        codeList.setSourceFile(sourceFile);
        
        CppBlock ifdef = new CppBlock(new Variable("A"), new Variable("A"), CppBlock.Type.IFDEF);
        ifdef.setCondition(new Variable("A"));
        ifdef.setSourceFile(sourceFile);
        
        ifdef.addNestedElement(makeCode("1", sourceFile, new Variable("A"), new Variable("A")));
        
        CppBlock elsedef = new CppBlock(not("A"), not("A"), CppBlock.Type.ELSE);
        elsedef.setCondition(not("A"));
        elsedef.setSourceFile(sourceFile);
        
        elsedef.addNestedElement(makeCode("2", sourceFile, not("A"), not("A")));
        
        codeList.addNestedElement(makeCode("int a = ", sourceFile, True.INSTANCE, True.INSTANCE));
        codeList.addNestedElement(ifdef);
        codeList.addNestedElement(elsedef);
        codeList.addNestedElement(makeCode(";", sourceFile, True.INSTANCE, True.INSTANCE));
        
        SingleStatement assignment = new SingleStatement(True.INSTANCE, codeList);
        assignment.setCondition(True.INSTANCE);
        assignment.setSourceFile(sourceFile);
        f1.addNestedElement(assignment);
        
        // comment
        Comment comment = new Comment(True.INSTANCE, makeCode("/* test */", sourceFile, True.INSTANCE, True.INSTANCE));
        comment.setCondition(True.INSTANCE);
        comment.setSourceFile(sourceFile);
        f1.addNestedElement(comment);
        
        // loop
        LoopStatement loop = new LoopStatement(True.INSTANCE,
                makeCode("while ( 1 )", sourceFile, True.INSTANCE, True.INSTANCE), LoopStatement.LoopType.WHILE);
        loop.setCondition(True.INSTANCE);
        loop.setSourceFile(sourceFile);
        
        TypeDefinition typedef = new TypeDefinition(True.INSTANCE,
                makeCode("typedef int int_32 ;", sourceFile, True.INSTANCE, True.INSTANCE), TypeDefType.TYPEDEF);
        typedef.setCondition(True.INSTANCE);
        typedef.setSourceFile(sourceFile);
        loop.addNestedElement(typedef);
        
        f1.addNestedElement(loop);
        
        return file;
    }
    
    /**
     * Creates a {@link Code} instance.
     * 
     * @param text The code string.
     * @param sourceFile The source file to set.
     * @param pc The presence condition to set.
     * @param condition The condition to set.
     * 
     * @return The {@link Code} instance.
     */
    private static @NonNull Code makeCode(@NonNull String text, java.io.@NonNull File sourceFile, Formula pc, 
            Formula condition) {
        
        Code code = new Code(True.INSTANCE, text);
        code.setCondition(condition);
        code.setSourceFile(sourceFile);
        return code;
    }
    
}
