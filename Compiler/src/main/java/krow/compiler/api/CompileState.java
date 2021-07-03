package krow.compiler.api;

/**
 * The available states that the ReKrow compiler can be in.
 * This is used to filter handlers by position, in conjunction with compile expectations.
 *
 * @author Moderocky
 */
public enum CompileState {
    /**
     * The file root, typically before the first DROP_LEVEL `{`
     */
    FILE_ROOT,
    /**
     * The class body, where field and method members go.
     */
    CLASS_BODY,
    /**
     * Inside the `(` and `)` of the method header in its declaration.
     */
    METHOD_HEADER_DECLARATION,
    /**
     * Inside the method body `{...}` at the *beginning* of a statement.
     * Typically reserved for keywords that start a statement.
     */
    METHOD_BODY,
    /**
     * Within a statement - between the start and the DEAD_END `;` where most functional code goes.
     * This is also implicitly used by `()` brackets.
     */
    STATEMENT,
    /**
     * Within the `(...)` header of a method call.
     */
    METHOD_CALL_HEADER,
    /**
     * Within the `(...)` header of the implicit `struct(a, b, ...)` structure constructor.
     * Note: this is not the same as a method header since it allows only named elements.
     */
    IMPLICIT_STRUCT_HEADER,
    /**
     * Within the `(...)` header of the implicit `new Type[](a, b, ...)` array constructor.
     * Note: this is not the same as a method header since it allows only elements of the type.
     */
    IMPLICIT_ARRAY_HEADER,
    /**
     * Before the DEAD_END `;` of a constant declaration.
     * Note: this typically allows only literal values.
     */
    CONST_DECLARATION
}
