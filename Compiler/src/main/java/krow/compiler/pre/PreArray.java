package krow.compiler.pre;

import krow.compiler.CompileContext;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.BIPUSH;

public class PreArray extends PreMethodCall {
    
    public Type type;
    public int dimensions;
    public int length;
    protected int argNumber = 0;
    public boolean mode;
    
    public WriteInstruction create(int[] dims) {
        assert type.isArray();
        final Type component = type;
        return (writer, method) -> {
            for (int dim : dims) {
                method.visitIntInsn(BIPUSH, dim);
            }
            method.visitMultiANewArrayInsn(component.internalName(), dims.length);
        };
    }
    
    @Override
    public WriteInstruction execute(CompileContext context) {
        return create();
    }
    
    public WriteInstruction create() {
        assert type.isArray();
        final Type component = type.componentType();
        return (writer, method) -> {
            method.visitIntInsn(BIPUSH, length());
            method.visitTypeInsn(ANEWARRAY, component.internalName());
        };
    }
    
    public int[] incrementMatrixSlotCoordinates() {
        final int[] coords = new int[]{ Math.floorMod(argNumber, dimensions), argNumber/dimensions};
        argNumber++;
        return coords;
    }
    
    public int length() {
        return length;
    }
    
    @Override
    public String toString() {
        return "PreArray{" +
            "type=" + type +
            ", dimensions=" + dimensions +
            ", length=" + length +
            '}';
    }
}
