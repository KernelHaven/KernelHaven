package net.ssehub.kernel_haven.code_model;

import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An enumeration of all syntax elements used in the AST.
 * 
 * @author Adam
 */
public enum SyntaxElementTypes implements ISyntaxElementType {
    
    ALIGN_OF_EXPR_T("AlignOfExprT"),
    ALIGN_OF_EXPR_U("AlignOfExprU"),
    ARRAY_ACCESS("ArrayAccess"),
    ATOMIC_ABSTRACT_DECLARATOR("AtomicAbstractDeclarator"),
    ATOMIC_ATTRIBUTE("AtomicAttribute"),
    ATTRIBUTE_SEQUENCE("AttributeSequence"),
    ASM_ATTRIBUTE_SPECIFIER("AsmAttributeSpecifier"),
    ASSIGN_EXPR("AssignExpr"),
    ATOMIC_NAMED_DECLARATOR("AtomicNamedDeclarator"),
    BREAK_STATEMENT("BreakStatement"),
    BUILTIN_OFFSETOF("BuiltinOffsetof"),
    BUILTIN_TYPES_COMPATIBLE("BuiltinTypesCompatible"),
    CASE_STATEMENT("CaseStatement"),
    CAST_EXPR("CastExpr"),
    COMPOUND_ATTRIBUTE("CompoundAttribute"),
    COMPOUND_STATEMENT("CompoundStatement"),
    COMPOUND_STATEMENT_EXPR("CompoundStatementExpr"),
    CONDITIONAL_EXPR("ConditionalExpr"),
    CONSTANT("Constant"),
    CONTINUE_STATEMENT("ContinueStatement"),
    DECLARATION("Declaration"),
    DECLARATION_STATEMENT("DeclarationStatement"),
    DECL_IDENTIFIER_LIST("DeclIdentifierList"),
    DECL_ARRAY_ACCESS("DeclArrayAccess"),
    DECL_PARAMETER_DECL_LIST("DeclParameterDeclList"),
    DEFAULT_STATEMENT("DefaultStatement"),
    DO_STATEMENT("DoStatement"),
    ELIF_STATEMENT("ElifStatement"),
    EMPTY_EXTERNAL_DEF("EmptyExternalDef"),
    EMPTY_STATEMENT("EmptyStatement"),
    ENUMERATOR("Enumerator"),
    ENUM_SPECIFIER("EnumSpecifier"),
    EXPR_LIST("ExprList"),
    EXPR_STATEMENT("ExprStatement"),
    FOR_STATEMENT("ForStatement"),
    FUNCTION_CALL("FunctionCall"),
    FUNCTION_DEF("FunctionDef"),
    GNU_ASM_EXPR("GnuAsmExpr"),
    GNU_ATTRIBUTE_SPECIFIER("GnuAttributeSpecifier"),
    GOTO_STATEMENT("GotoStatement"),
    ID("Id"),
    SIMPLE_POSTFIX_SUFFIX("SimplePostfixSuffix"),
    IF_STATEMENT("IfStatement"),
    INITIALIZER("Initializer"),
    INITIALIZER_ARRAY_DESIGNATOR("InitializerArrayDesignator"),
    INITIALIZER_ASSIGMENT("InitializerAssigment"),
    INITIALIZER_DESIGNATOR_C("InitializerDesignatorC"),
    INITIALIZER_DESIGNATOR_D("InitializerDesignatorD"),
    INIT_DECLARATOR_E("InitDeclaratorE"),
    INIT_DECLARATOR_I("InitDeclaratorI"),
    LABEL_STATEMENT("LabelStatement"),
    LCURLY_INITIALIZER("LcurlyInitializer"),
    LOCAL_LABEL_DECLARATION("LocalLabelDeclaration"),
    N_ARY_EXPR("NAryExpr"),
    N_ARY_SUB_EXPR("NArySubExpr"),
    NESTED_ABSTRACT_DECLARATOR("NestedAbstractDeclarator"),
    NESTED_FUNCTION_DEF("NestedFunctionDef"),
    NESTED_NAMED_DECLARATOR("NestedNamedDeclarator"),
    OFFSETOF_MEMBER_DESIGNATOR_EXPR("OffsetofMemberDesignatorExpr"),
    OFFSETOF_MEMBER_DESIGNATOR_ID("OffsetofMemberDesignatorID"),
    OTHER_PRIMITIVE_TYPE_SPECIFIER("OtherPrimitiveTypeSpecifier"),
    PARAMETER_DECLARATION_A_D("ParameterDeclarationAD"),
    PARAMETER_DECLARATION_D("ParameterDeclarationD"),
    PLAIN_PARAMETER_DECLARATION("PlainParameterDeclaration"),
    POINTER("Pointer"),
    POINTER_CREATION_EXPR("PointerCreationExpr"),
    POINTER_DEREF_EXPR("PointerDerefExpr"),
    POINTER_POSTFIX_SUFFIX("PointerPostfixSuffix"),
    POSTFIX_EXPR("PostfixExpr"),
    RANGE_EXPR("RangeExpr"),
    RETURN_STATEMENT("ReturnStatement"),
    SIGNED_SPECIFIER("SignedSpecifier"),
    SIZE_OF_EXPR_T("SizeOfExprT"),
    SIZE_OF_EXPR_U("SizeOfExprU"),
    STRING_LIT("StringLit"),
    STRUCT_DECLARATION("StructDeclaration"),
    STRUCT_DECLARATOR("StructDeclarator"),
    STRUCT_INITIALIZER("StructInitializer"),
    STRUCT_SPECIFIER("StructSpecifier"),
    SWITCH_STATEMENT("SwitchStatement"),
    TRANSLATION_UNIT("TranslationUnit"),
    TYPE_DEF_TYPE_SPECIFIER("TypeDefTypeSpecifier"),
    TYPE_NAME("TypeName"),
    TYPE_OF_SPECIFIER_T("TypeOfSpecifierT"),
    TYPE_OF_SPECIFIER_U("TypeOfSpecifierU"),
    TYPEDEF_SPECIFIER("TypedefSpecifier"),
    UNARY_EXPR("UnaryExpr"),
    UNARY_OP_EXPR("UnaryOpExpr"),
    UNION_SPECIFIER("UnionSpecifier"),
    UNSIGNED_SPECIFIER("UnsignedSpecifier"),
    VAR_ARGS("VarArgs"),
    WHILE_STATEMENT("WhileStatement");
    
    
    /**
     * A wrapper around the static attribute. This is needed, because static attributes cannot be accessed in the ctor.
     */
    private static class StaticHelper {
        private static @NonNull Map<String, SyntaxElementTypes> nameMapping = new HashMap<>();
    }
    
    private @NonNull String name;
    
    /**
     * Constructor for elements.
     * 
     * @param name A string representation of the name of this element.
     */
    private SyntaxElementTypes(@NonNull String name) {
        this.name = name;
        StaticHelper.nameMapping.put(name, this);
    }
    
    /**
     * The name of this syntax element.
     * 
     * @return The name; never null.
     */
    public @NonNull String getName() {
        return name;
    }
    
    /**
     * Returns the syntax element for the given name. Inverse function to {@link #getName()}.
     * 
     * @param name The name of the syntax element to return.
     * @return The syntax element with the given name; or <code>null</code> if no such element exists.
     */
    public static @Nullable SyntaxElementTypes getByName(@NonNull String name) {
        return StaticHelper.nameMapping.get(name);
    }
    
    @Override
    public @NonNull String toString() {
        return name;
    }

}
