package krow.compiler.api;

/**
 * An expectation for the upcoming element, used for easier and more accurate filtering of handlers.
 * The value in context should never be null.
 * <p>
 * Use {@link CompileExpectation#NONE} for no specific expectation.
 *
 * @author Moderocky
 */
public enum CompileExpectation {
    
    /**
     * No specific expectation.
     */
    NONE,
    /**
     * Expects a named variable.
     */
    VARIABLE,
    /**
     * Expects a DEAD_END `;` line ending.
     */
    DEAD_END,
    /**
     * Expects a literal value, such as a `"string"` or integer. Would not allow variables or values from fields.
     * May permit compile constants.
     */
    LITERAL,
    /**
     * Expects a primitive value, but not necessarily a literal one.
     */
    PRIMITIVE,
    /**
     * Expects a value. NOTE: this value *can* be primitive. 'Object' here means any value from any source.
     */
    OBJECT,
    /**
     * Expects a small primitive value that takes up one machine word, such as an integer, char, boolean or byte.
     */
    SMALL,
    /**
     * Expects a raw type, such as `org/example/Thing`. NOTE: types are not class literals.
     */
    TYPE,
    /**
     * Expects some invocation of a member, such as accessing a field or calling a method.
     */
    MEMBER,
    /**
     * Expects the invocation of a method.
     */
    METHOD,
    /**
     * Expects a reference to a field.
     */
    FIELD,
    /**
     * Expects a metadata `&lt;&gt;` block.
     */
    META,
    /**
     * Expects a DROP_LEVEL `{`.
     */
    DOWN,
    /**
     * Expects an UP_LEVEL `}`.
     */
    UP
    
}
