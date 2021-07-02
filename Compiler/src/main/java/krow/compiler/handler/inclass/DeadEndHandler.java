package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class DeadEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child = new CompileContext();
        context.variables.clear();
        context.currentField = null;
        context.currentMethod = null;
        context.method = null;
        context.clearUpcoming();
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "END_OF_MEMBER";
    }
}
