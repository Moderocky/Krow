package krow.compiler.handler.incall;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class MethodSplitParameterHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CALL);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
