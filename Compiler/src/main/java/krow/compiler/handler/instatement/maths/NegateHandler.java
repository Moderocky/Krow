package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class NegateHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case DEAD_END, META, DOWN, UP -> false;
            default -> statement.startsWith("-");
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.expectation = CompileExpectation.OBJECT;
        context.child.point = null;
        context.child.doExpecting = this::gamma;
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    private WriteInstruction gamma(final Type type) {
        return switch (type.dotPath()) {
            case "int", "byte", "short", "char" -> WriteInstruction.negateSmall();
            case "float" -> WriteInstruction.negateFloat();
            case "double" -> WriteInstruction.negateDouble();
            case "long" -> WriteInstruction.negateLong();
            default -> throw new RuntimeException("Operating on non-number type.");
        };
    }
    
    @Override
    public String debugName() {
        return "NEGATE";
    }
}
