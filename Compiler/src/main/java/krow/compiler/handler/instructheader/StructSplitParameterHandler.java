package krow.compiler.handler.instructheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class StructSplitParameterHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_STRUCT_HEADER);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
