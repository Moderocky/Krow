package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class NullLiteralHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, DOWN, UP, METHOD, FIELD:
                return false;
        }
        return statement.startsWith("null");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.child.statement(WriteInstruction.pushNull());
        context.expectation = CompileExpectation.NONE;
        context.child.point = new Type(void.class);
        return new HandleResult(null, statement.substring(4).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "ACONST_NULL";
    }
}
