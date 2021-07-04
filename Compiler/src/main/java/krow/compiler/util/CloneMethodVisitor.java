package krow.compiler.util;

import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class CloneMethodVisitor extends MethodVisitor {
    
    final List<WriteInstruction> instructions;
    
    public CloneMethodVisitor(List<WriteInstruction> instructions, int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
        this.instructions = instructions;
    }
    
    @Override
    public void visitAttribute(Attribute attribute) {
        instructions.add((writer, visitor) -> visitor.visitAttribute(attribute));
    }
    
    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        instructions.add((writer, visitor) -> visitor.visitFrame(type, numLocal, local, numStack, stack));
    }
    
    @Override
    public void visitInsn(int opcode) {
        instructions.add((writer, visitor) -> visitor.visitInsn(opcode));
    }
    
    @Override
    public void visitIntInsn(int opcode, int operand) {
        instructions.add((writer, visitor) -> visitor.visitIntInsn(opcode, operand));
    }
    
    @Override
    public void visitVarInsn(int opcode, int var) {
        instructions.add((writer, visitor) -> visitor.visitVarInsn(opcode, var));
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type) {
        instructions.add((writer, visitor) -> visitor.visitTypeInsn(opcode, type));
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        instructions.add((writer, visitor) -> visitor.visitFieldInsn(opcode, owner, name, descriptor));
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        instructions.add((writer, visitor) -> visitor.visitMethodInsn(opcode, owner, name, descriptor));
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        instructions.add((writer, visitor) -> visitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface));
    }
    
    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        instructions.add((writer, visitor) -> visitor.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }
    
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        instructions.add((writer, visitor) -> visitor.visitJumpInsn(opcode, label));
    }
    
    @Override
    public void visitLabel(Label label) {
        instructions.add((writer, visitor) -> visitor.visitLabel(label));
    }
    
    @Override
    public void visitLdcInsn(Object value) {
        instructions.add((writer, visitor) -> visitor.visitLdcInsn(value));
    }
    
    @Override
    public void visitIincInsn(int var, int increment) {
        instructions.add((writer, visitor) -> visitor.visitIincInsn(var, increment));
    }
    
    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        instructions.add((writer, visitor) -> visitor.visitTableSwitchInsn(min, max, dflt, labels));
    }
    
    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        instructions.add((writer, visitor) -> visitor.visitLookupSwitchInsn(dflt, keys, labels));
    }
    
    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        instructions.add((writer, visitor) -> visitor.visitMultiANewArrayInsn(descriptor, numDimensions));
    }
    
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        instructions.add((writer, visitor) -> visitor.visitTryCatchBlock(start, end, handler, type));
    }
    
}
