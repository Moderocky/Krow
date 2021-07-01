package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class BooleanLiteralHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, DOWN, UP, METHOD, FIELD:
                return false;
        }
        return statement.startsWith("true") || statement.startsWith("false");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final boolean value = statement.startsWith("true");
        context.child.point = new Type(boolean.class);
        context.child.statement(value ? WriteInstruction.push1() : WriteInstruction.push0());
        context.expectation = CompileExpectation.NONE;
        if (state == CompileState.IN_CONST) {
            context.saveConstant.value = value;
            context.expectation = CompileExpectation.DEAD_END;
        }
        return new HandleResult(null, statement.substring(value ? 4 : 5).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "BIPUSH_Z";
    }
}
