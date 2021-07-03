package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class BooleanLiteralHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case TYPE, DEAD_END, DOWN, UP, METHOD, FIELD -> false;
            default -> statement.startsWith("true") || statement.startsWith("false");
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final boolean value = statement.startsWith("true");
        context.child.point = new Type(boolean.class);
        context.child.statement(value ? WriteInstruction.push1() : WriteInstruction.push0());
        context.expectation = CompileExpectation.NONE;
        if (state == CompileState.CONST_DECLARATION) {
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
