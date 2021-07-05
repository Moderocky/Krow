package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.SystemLibrary;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;

@SuppressWarnings("ALL")
public class DropLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return (statement.startsWith("{") && context.child.isBlockAllowed());
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.child.createBlock(SystemLibrary.SYSTEM_LIBRARY);
        context.child.setBlockAllowed(false);
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "ENTER_BLOCK";
    }
}
