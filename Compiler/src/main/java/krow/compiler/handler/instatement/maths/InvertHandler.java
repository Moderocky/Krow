package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class InvertHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DEAD_END, META, DOWN, UP:
                return false;
        }
        return (statement.startsWith("~") || statement.startsWith("!"));
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
            case "int", "byte", "short", "char" -> WriteInstruction.flipSmall();
            case "boolean" -> (writer, method) -> {
                method.visitInsn(4);
                method.visitInsn(130);
            };
            case "long" -> WriteInstruction.flipLong();
            default -> throw new RuntimeException("Operating on non-binary type.");
        };
    }
    
    @Override
    public String debugName() {
        return "INVERT";
    }
}
