package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.WriteInstruction;

public class MultiplyHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP:
                return false;
        }
        return (statement.startsWith("*") || statement.startsWith("×") || statement.startsWith("·")) && context.child.point != null;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final WriteInstruction instruction;
        switch (context.child.point.dotPath()) {
            case "int", "short", "byte", "char", "boolean" -> instruction = WriteInstruction.multiplySmall();
            case "long" -> instruction = WriteInstruction.multiplyLong();
            case "double" -> instruction = WriteInstruction.multiplyDouble();
            case "float" -> instruction = WriteInstruction.multiplyFloat();
            default -> throw new RuntimeException("Multiplication of non-primitive type.");
        }
        context.child.statement(instruction);
        context.expectation = CompileExpectation.PRIMITIVE;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "MULTIPLY";
    }
}
