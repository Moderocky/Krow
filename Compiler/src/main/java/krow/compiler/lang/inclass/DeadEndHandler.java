package krow.compiler.lang.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class DeadEndHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.createChild();
        context.variables.clear();
        context.throwables.clear();
        context.currentField = null;
        context.currentMethod = null;
        context.method = null;
        context.clearUpcoming();
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "END_OF_MEMBER";
    }
}
