package krow.compiler.handler.instatement.maths;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

public class EqualsHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case DEAD_END, SMALL, OBJECT, PRIMITIVE, DOWN, UP -> false;
            default -> (statement.startsWith("==") || statement.startsWith("!=")) && context.child.point != null;
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Label store = new Label(), alt = new Label();
        final Type type = context.child.point;
        assert type != null;
        final WriteInstruction comp = statement.startsWith("=") ? gamma(type, alt) : lemma(type, alt);
        final WriteInstruction instruction = (writer, method) -> {
            comp.accept(writer, method);
            method.visitInsn(4);
            method.visitJumpInsn(167, store);
            method.visitLabel(alt);
            method.visitInsn(3);
            method.visitLabel(store);
        };
        context.child.statement(instruction);
        context.expectation = CompileExpectation.OBJECT;
        context.child.point = null;
        context.child.swap(true);
        return new HandleResult(null, statement.substring(2).trim(), state);
    }
    
    private WriteInstruction gamma(final Type type, final Label alt) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> (writer, method) -> method.visitJumpInsn(IF_ICMPNE, alt);
            case "long" -> (writer, method) -> {
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFNE, alt);
            };
            case "float" -> (writer, method) -> {
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFNE, alt);
            };
            case "double" -> (writer, method) -> {
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFNE, alt);
            };
            default -> (writer, method) -> method.visitJumpInsn(IF_ACMPNE, alt);
        };
    }
    
    private WriteInstruction lemma(final Type type, final Label alt) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> (writer, method) -> method.visitJumpInsn(IF_ICMPEQ, alt);
            case "long" -> (writer, method) -> {
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFEQ, alt);
            };
            case "float" -> (writer, method) -> {
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFEQ, alt);
            };
            case "double" -> (writer, method) -> {
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFEQ, alt);
            };
            default -> (writer, method) -> method.visitJumpInsn(IF_ACMPEQ, alt);
        };
    }
    
    @Override
    public String debugName() {
        return "EQUALS";
    }
}
