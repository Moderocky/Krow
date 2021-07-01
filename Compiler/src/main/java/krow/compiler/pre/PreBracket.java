package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;

public class PreBracket {
    
    public List<WriteInstruction> instructions = new ArrayList<>();
    public Type exitType;
    public CompileState state;
    public boolean swap;
    
    public void close(final CompileContext context) {
        context.statementRaw((codeWriter, methodVisitor) -> {
            for (WriteInstruction instruction : instructions) {
                instruction.accept(codeWriter, methodVisitor);
            }
        });
    }
    
}
