package krow.compiler.lang.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class DeadEndHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        return new HandleResult(null, "", CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "END_OF_DAYS";
    }
}
