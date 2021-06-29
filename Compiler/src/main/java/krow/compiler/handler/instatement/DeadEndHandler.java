package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

import java.util.Collections;

public class DeadEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        if (context.child.inverted)
            Collections.reverse(context.child.statement);
        if (context.child.skip != null)
            context.child.statement.add(context.child.skip);
        context.currentMethod.writeCode(context.child.statement.toArray(new WriteInstruction[0]));
        context.child.preparing.clear();
        context.child.statement.clear();
        context.child.expectation = CompileExpectation.NONE;
        context.child.store = null;
        context.child.skip = null;
        context.child.staticState = false;
        context.child.point = null;
        context.duplicate = false;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_METHOD);
    }
    
    @Override
    public String debugName() {
        return "END_OF_STATEMENT";
    }
}
