package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

import java.lang.reflect.Modifier;

public class FinalHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("final");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.addUpcoming(Modifier.FINAL);
        return new HandleResult(null, statement.substring(5).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_FINAL";
    }
}
