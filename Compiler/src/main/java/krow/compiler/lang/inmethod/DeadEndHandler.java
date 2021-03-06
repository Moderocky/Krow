package krow.compiler.lang.inmethod;

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
        context.child.setBlockAllowed(false);
        context.decayConditionPhase();
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "END_OF_EMPTY_STATEMENT";
    }
}
