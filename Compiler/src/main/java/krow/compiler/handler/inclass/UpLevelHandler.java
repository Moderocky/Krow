package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class UpLevelHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("}");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child = null;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.ROOT);
    }
    
    @Override
    public String debugName() {
        return "EXIT_CLASS";
    }
}
