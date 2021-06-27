package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

public class DropLevelHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("{");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.child != null;
        assert context.method != null;
        assert context.hasBody;
        assert !context.method.isAbstract;
        context.child.hasBody = true;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_METHOD);
    }
    
    @Override
    public String debugName() {
        return "ENTER_METHOD";
    }
}
