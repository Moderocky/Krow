package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

public class AddHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP:
                return false;
        }
        return statement.startsWith("+") && context.child.point != null;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final WriteInstruction instruction;
        switch (context.child.point.dotPath()) {
            case "int", "short", "char", "boolean" -> instruction = WriteInstruction.addSmall();
            case "long" -> instruction = WriteInstruction.addLong();
            case "double" -> instruction = WriteInstruction.addDouble();
            case "float" -> instruction = WriteInstruction.addFloat();
            default -> throw new RuntimeException("Summing of non-primitive type.");
        }
        context.child.statement(instruction);
        context.expectation = CompileExpectation.PRIMITIVE;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "ADD";
    }
}
