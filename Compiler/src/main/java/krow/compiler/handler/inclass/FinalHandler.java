package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

import java.lang.reflect.Modifier;

public class FinalHandler implements DefaultHandler {
    
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
