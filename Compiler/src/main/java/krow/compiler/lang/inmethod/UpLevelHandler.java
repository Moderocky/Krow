package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

@SuppressWarnings("ALL")
public class UpLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("}") && !context.inBlock();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        if (context.method != null && context.currentMethod != null) {
            context.method.emergencyExit(context.child.statement());
            context.currentMethod.writeCode(context.child.statement().toArray(new WriteInstruction[0]));
        }
        context.createChild();
        context.variables.clear();
        context.preparing.clear();
        context.currentField = null;
        context.currentMethod = null;
        context.method = null;
        context.modifiersUpcoming = 0;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "EXIT_METHOD";
    }
}
