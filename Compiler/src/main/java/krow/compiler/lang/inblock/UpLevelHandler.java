package krow.compiler.lang.inblock;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.lang.instatement.DeadEndHandler;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class UpLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("}") && context.inBlock();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child.closeBlock();
        context.child.setBlockAllowed(false);
        context.expectation = CompileExpectation.NONE;
        DeadEndHandler.deadEndStatement(context); // Close everything: this is basically a dead-end
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "EXIT_BLOCK";
    }
}
