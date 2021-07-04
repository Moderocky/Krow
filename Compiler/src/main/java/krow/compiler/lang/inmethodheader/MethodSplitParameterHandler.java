package krow.compiler.handler.inmethodheader;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class MethodSplitParameterHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_HEADER_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "NEXT_PARAM";
    }
}
