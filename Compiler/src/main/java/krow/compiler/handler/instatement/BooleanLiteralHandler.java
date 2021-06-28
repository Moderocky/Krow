package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.handler.PostAssignment;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class BooleanLiteralHandler implements Handler, PostAssignment {
    
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
        context.child.statement.add(value ? WriteInstruction.push1() : WriteInstruction.push0());
        context.expectation = CompileExpectation.NONE;
        attemptAssignment(context, state);
        if (state == CompileState.IN_CALL) {
            context.child.preparing.get(0).addParameter(new Type(boolean.class));
        }
        return new HandleResult(null, statement.substring(value ? 4 : 5).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "BIPUSH_Z";
    }
}
