package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.util.HiddenModifier;

import java.lang.reflect.Modifier;

public class DropLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case UP, DEAD_END, TYPE, OBJECT, PRIMITIVE, SMALL, META, METHOD:
                return false;
        }
        return statement.startsWith("{");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.child != null;
        assert context.method != null;
        assert context.hasBody;
        assert !context.upcoming(Modifier.ABSTRACT);
        assert !context.upcoming(Modifier.NATIVE);
        assert !context.upcoming(HiddenModifier.BRIDGE);
        context.child.hasBody = true;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "ENTER_METHOD";
    }
}
