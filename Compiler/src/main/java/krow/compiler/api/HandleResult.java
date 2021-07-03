package krow.compiler.api;

/**
 * The handler result - used for multiple returns.
 */
public record HandleResult(Instruction instruction, String remainder, CompileState future) {
}
