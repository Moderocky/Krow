package krow.compiler.handler.incall;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

public class MethodSplitParameterHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child.preparing.get(0).addParameter(context.child.point);
        context.child.point = null;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_CALL_HEADER);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
