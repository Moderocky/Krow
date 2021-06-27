package krow.compiler;

import krow.compiler.instruction.Instruction;

public record HandleResult(Instruction instruction, String remainder, CompileState future) {
}
