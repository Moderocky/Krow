package krow.compiler.handler.incall;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;

public class MethodCallEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL:
                return false;
        }
        return statement.startsWith(")");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreMethodCall call = context.child.preparing.get(0);
        assert call != null;
        context.child.statement.add(call.execute(context));
        context.child.preparing.remove(0);
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1)
            .trim(), context.child.preparing.size() > 0 ? CompileState.IN_CALL : CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "END_CALL";
    }
}
