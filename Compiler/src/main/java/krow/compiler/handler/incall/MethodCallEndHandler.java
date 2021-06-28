package krow.compiler.handler.incall;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.handler.PostAssignment;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;

public class MethodCallEndHandler implements Handler, PostAssignment {
    
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
        final boolean inCall = context.child.preparing.size() > 0;
        attemptAssignment(context, inCall ? CompileState.IN_CALL : CompileState.IN_STATEMENT);
        return new HandleResult(null, statement.substring(1)
            .trim(), inCall ? CompileState.IN_CALL : CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "END_CALL";
    }
}
