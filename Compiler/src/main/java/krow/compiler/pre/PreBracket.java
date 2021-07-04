package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.WorkContext;
import krow.compiler.api.CompileState;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

@SuppressWarnings("ALL")
public class PreBracket extends WorkContext {
    
    public Type exitType;
    public CompileState state;
    
    public void close(final CompileContext context) {
        context.child.statementRaw((codeWriter, methodVisitor) -> {
            for (WriteInstruction instruction : statement) {
                instruction.accept(codeWriter, methodVisitor);
            }
        });
    }
    
    @Override
    public WorkContext child() {
        return null;
    }
}
