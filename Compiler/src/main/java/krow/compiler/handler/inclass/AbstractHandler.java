package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

import java.lang.reflect.Modifier;

public class AbstractHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("abstract");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.addUpcoming(Modifier.ABSTRACT);
        return new HandleResult(null, statement.substring(8).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_ABSTRACT";
    }
}
