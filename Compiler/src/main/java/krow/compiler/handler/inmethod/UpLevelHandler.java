package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

public class UpLevelHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("}");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        if (context.method != null && context.currentMethod != null) {
            context.method.emergencyExit(context.child.statement());
            context.currentMethod.writeCode(context.child.statement().toArray(new WriteInstruction[0]));
        }
        context.child = new CompileContext();
        context.variables.clear();
        context.preparing.clear();
        context.currentField = null;
        context.currentMethod = null;
        context.method = null;
        context.modifiersUpcoming = 0;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "EXIT_METHOD";
    }
}
