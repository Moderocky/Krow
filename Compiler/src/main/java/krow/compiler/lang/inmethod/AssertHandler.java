package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

@SuppressWarnings("ALL")
public class AssertHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("assert");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.method != null;
        context.lookingFor = new Type(boolean.class);
        context.expectation = CompileExpectation.PRIMITIVE;
        context.child.skip(WriteInstruction.assertTrue());
        return new HandleResult(null, statement.substring(6).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "ASSERT";
    }
}
