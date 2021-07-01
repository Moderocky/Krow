package krow.compiler;

public enum CompileState {
    ROOT,
    IN_CLASS,
    IN_METHOD_HEADER,
    IN_METHOD,
    IN_STATEMENT,
    IN_CALL,
    IN_STRUCT_HEADER,
    IN_BLOCK,
    IN_CONST,
    IN_FIELD
}
