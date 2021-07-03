package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

import java.lang.reflect.Modifier;

public class NativeHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("native");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.addUpcoming(Modifier.NATIVE);
        return new HandleResult(null, statement.substring(6).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_NATIVE";
    }
}
