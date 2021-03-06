package krow.compiler.lang.literal;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class NullLiteralHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case TYPE, DEAD_END, DOWN, UP, METHOD, FIELD -> false;
            default -> statement.startsWith("null");
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.child.point = new Type(void.class);
        context.child.statement(WriteInstruction.pushNull());
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(4).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "ACONST_NULL";
    }
}
