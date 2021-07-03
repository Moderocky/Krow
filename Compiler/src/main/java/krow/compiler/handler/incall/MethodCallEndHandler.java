package krow.compiler.handler.incall;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;

public class MethodCallEndHandler implements DefaultHandler {
    
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
        final PreMethodCall call = context.child.preparing.remove(0);
        assert call != null;
        assert !(call instanceof PreArray);
        if (context.child.point != null) call.addParameter(context.child.point);
        call.ensureReturnType(context);
        context.child.point = call.returnType;
        if (call.returnType.matches(void.class)) context.child.point = null;
        context.child.statement(call.execute(context));
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        final CompileState state = context.child.nested.remove(0);
        return new HandleResult(null, statement.substring(1)
            .trim(), state);
    }
    
    @Override
    public String debugName() {
        return "END_CALL";
    }
}
