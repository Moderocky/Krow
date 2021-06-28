package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;

import java.lang.reflect.Modifier;

public class SynchronizedHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("synchronized");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.addUpcoming(Modifier.SYNCHRONIZED);
        return new HandleResult(null, statement.substring(12).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_SYNCHRONISED";
    }
}
