package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class ArrayLengthHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith(".length")) return false;
        switch (context.expectation) {
            case TYPE, DEAD_END, METHOD, DOWN, UP, LITERAL, VARIABLE, SMALL:
                return false;
        }
        if (context.child.point == null) return false;
        return (context.child.point.isArray());
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        assert context.child.point != null;
        context.child.point = new Type(int.class);
        context.child.statement(WriteInstruction.arrayLength());
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(7).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "ARRAY_LENGTH";
    }
}
