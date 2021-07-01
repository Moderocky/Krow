package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class AssertHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("assert");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.method != null;
        context.lookingFor = new Type(boolean.class);
        context.expectation = CompileExpectation.PRIMITIVE;
        context.child.skip = WriteInstruction.assertTrue();
        return new HandleResult(null, statement.substring(6).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "ASSERT";
    }
}
