package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class UpLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("}");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child = null;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "EXIT_CLASS";
    }
}
