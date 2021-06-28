package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class DeadEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_METHOD);
    }
    
    @Override
    public String debugName() {
        return "END_OF_EMPTY_STATEMENT";
    }
}