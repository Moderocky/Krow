package krow.compiler.handler.inconst;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreConstant;

public class DeadEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final CompileState state = context.exitTo;
        final PreConstant constant = context.saveConstant;
        assert constant != null;
        if (context.child != null) context.child.constants.put(constant.name, constant.value);
        else context.constants.put(constant.name, constant.value);
        context.child.preparing.clear();
        context.child.statement().clear();
        context.child.expectation = CompileExpectation.NONE;
        context.child.store = null;
        context.child.skip = null;
        context.child.staticState = false;
        context.child.point = null;
        context.duplicate = false;
        context.exitTo = null;
        context.saveConstant = null;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "END_OF_CONSTANT";
    }
}
