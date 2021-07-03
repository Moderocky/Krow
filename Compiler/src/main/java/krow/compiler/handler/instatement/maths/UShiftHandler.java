package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class UShiftHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP -> false;
            default -> (statement.startsWith(">>>")) && context.child.point != null;
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Type type = context.child.point;
        assert type != null;
        final WriteInstruction instruction = gamma(type);
        context.child.statement(instruction);
        context.expectation = CompileExpectation.OBJECT;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(3).trim(), state);
    }
    
    private WriteInstruction gamma(final Type type) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> WriteInstruction.uShiftSmall();
            case "long" -> WriteInstruction.uShiftLong();
            default -> throw new RuntimeException("Illegal bit shift of non-small/long types.");
        };
    }
    
    @Override
    public String debugName() {
        return "U_SHIFT";
    }
}
