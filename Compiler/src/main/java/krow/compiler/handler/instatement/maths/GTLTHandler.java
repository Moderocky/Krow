package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

public class GTLTHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP:
                return false;
        }
        return (statement.startsWith(">") || statement.startsWith("<")) && context.child.point != null;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Label store = new Label(), alt = new Label();
        final Type type = context.child.point;
        assert type != null;
        final WriteInstruction comp = statement.startsWith(">") ? gamma(type, alt) : lemma(type, alt);
        final WriteInstruction instruction = (writer, method) -> {
            comp.accept(writer, method);
            method.visitInsn(3);
            method.visitJumpInsn(167, store);
            method.visitLabel(alt);
            method.visitInsn(4);
            method.visitLabel(store);
        };
        context.child.statement(instruction);
        context.expectation = CompileExpectation.OBJECT;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(1).trim(), state);
    }
    
    private WriteInstruction gamma(final Type type, final Label alt) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> (writer, method) -> method.visitJumpInsn(IF_ICMPGT, alt);
            case "long" -> (writer, method) -> {
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFGT, alt);
            };
            case "float" -> (writer, method) -> {
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFGT, alt);
            };
            case "double" -> (writer, method) -> {
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFGT, alt);
            };
            default -> throw new RuntimeException("Operating on non-binary type.");
        };
    }
    
    private WriteInstruction lemma(final Type type, final Label alt) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> (writer, method) -> method.visitJumpInsn(IF_ICMPLT, alt);
            case "long" -> (writer, method) -> {
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFLT, alt);
            };
            case "float" -> (writer, method) -> {
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFLT, alt);
            };
            case "double" -> (writer, method) -> {
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFLT, alt);
            };
            default -> throw new RuntimeException("Operating on non-binary type.");
        };
    }
    
    @Override
    public String debugName() {
        return "GT/LT";
    }
}
