package krow.compiler.lang.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

public class DivideHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP -> false;
            default -> (statement.startsWith("/") || statement.startsWith("÷")) && context.child.point != null;
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final WriteInstruction instruction;
        switch (context.child.point.dotPath()) {
            case "int", "short", "byte", "char", "boolean" -> instruction = WriteInstruction.divideSmall();
            case "long" -> instruction = WriteInstruction.divideLong();
            case "double" -> instruction = WriteInstruction.divideDouble();
            case "float" -> instruction = WriteInstruction.divideFloat();
            default -> throw new RuntimeException("Division of non-primitive type.");
        }
        context.child.statement(instruction);
        context.expectation = CompileExpectation.PRIMITIVE;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "DIVIDE";
    }
}
