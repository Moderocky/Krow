package krow.compiler.util;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {
    
    public static List<WriteInstruction> getSource(final Method method) {
        final ClassReader reader = new ClassReader(getSource(method.getDeclaringClass()));
        final List<WriteInstruction> instructions = new ArrayList<>();
        reader.accept(new MethodFinder(instructions, method.getName(), new Type(method.getReturnType()), Type.of(method.getParameterTypes())), ClassReader.SKIP_DEBUG);
        return instructions;
    }
    
    public static byte[] getSource(final Class<?> cls) {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream(cls.getName()
            .replace('.', '/') + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static class MethodFinder extends ClassVisitor {
        final String signature;
        final String name;
        final List<WriteInstruction> instructions;
        
        public MethodFinder(final List<WriteInstruction> instructions, final String name, Type returnType, Type[] parameters) {
//            super(Opcodes.ASM9, cv);
            super(Opcodes.ASM9);
            this.instructions = instructions;
            final StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (Type type : parameters) {
                builder.append(type.descriptorString());
            }
            builder.append(")").append(returnType.descriptor());
            this.name = name;
            signature = builder.toString();
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(this.name) && this.signature.equals(desc))
                return new CloneMethodVisitor(instructions, Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions));
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
    
}
