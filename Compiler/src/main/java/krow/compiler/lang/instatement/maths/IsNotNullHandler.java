package krow.compiler.lang.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;

public class IsNullHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case DEAD_END, DOWN, UP -> false;
            default -> (statement.startsWith("?")) && context.child.point == null;
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final WriteInstruction first, second;
        final Label label = new Label(), end = new Label();
        first = (writer, method) -> {
            method.visitJumpInsn(198, label);
            method.visitInsn(4);
            method.visitJumpInsn(167, end);
            method.visitLabel(label);
            method.visitInsn(3);
        };
        second = (writer, method) -> method.visitLabel(end);
        context.child.statement(first);
        context.child.statement(second);
        context.expectation = CompileExpectation.OBJECT;
        context.lookingFor = context.child.point;
        context.child.point = null;
        context.child.pointAfter = new Type(boolean.class);
        context.child.swap(true);
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "IS_NULL";
    }
}
