package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

@SuppressWarnings("ALL")
public class ThrowHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("throw");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.method != null;
        context.expectation = CompileExpectation.OBJECT;
        context.child.skip(WriteInstruction.throwException());
        context.duplicate = true;
        return new HandleResult(null, statement.substring(5).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "THROW";
    }
}
