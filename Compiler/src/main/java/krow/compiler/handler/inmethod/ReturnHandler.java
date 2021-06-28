package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;

import static mx.kenzie.foundation.WriteInstruction.*;

public class ReturnHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("return");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.method != null;
        final Type type = context.method.returnType;
        assert type != null;
        if (type.matches(void.class)) {
            context.child.skip = (returnEmpty());
            context.expectation = CompileExpectation.DEAD_END;
        } else if (type.words() == 1) {
            context.child.skip = (returnSmall());
            context.expectation = CompileExpectation.PRIMITIVE;
        } else if (type.dotPath().equals("long")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip = (returnLong());
        } else if (type.dotPath().equals("float")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip = (returnFloat());
        } else if (type.dotPath().equals("double")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip = (returnDouble());
        } else {
            context.expectation = CompileExpectation.OBJECT;
            context.child.skip = (returnObject());
        }
        return new HandleResult(null, statement.substring(6).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_RETURN";
    }
}
