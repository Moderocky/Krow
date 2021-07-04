package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;

import static mx.kenzie.foundation.WriteInstruction.*;

@SuppressWarnings("ALL")
public class ReturnHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("return");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        assert context.method != null;
        final Type type = context.method.returnType;
        assert type != null;
        context.lookingFor = type;
        if (type.getSimpleName().equals("Structure"))
            context.child.awaitAdjustedType = true;
        context.child.inReturnPhase = true;
        if (type.matches(void.class)) {
            context.child.skip.add(0, returnEmpty());
            context.expectation = CompileExpectation.DEAD_END;
        } else if (type.words() == 1) {
            context.child.skip.add(0, returnSmall());
            context.expectation = CompileExpectation.PRIMITIVE;
        } else if (type.dotPath().equals("long")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip.add(0, returnLong());
        } else if (type.dotPath().equals("float")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip.add(0, returnFloat());
        } else if (type.dotPath().equals("double")) {
            context.expectation = CompileExpectation.PRIMITIVE;
            context.child.skip.add(0, returnDouble());
        } else {
            context.expectation = CompileExpectation.OBJECT;
            context.child.skip.add(0, returnObject());
        }
        return new HandleResult(null, statement.substring(6).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_RETURN";
    }
}
