package krow.compiler.util;

import krow.compiler.pre.PreMethod;
import krow.lang.Runtime;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Handle;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

public class Handles {
    
    public static Handle getPrivateBootstrap(boolean dynamic) {
        try {
            if (dynamic)
                return Handles.getHandle(Runtime.class.getMethod("bootstrapPrivateDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
            return Handles.getHandle(Runtime.class.getMethod("bootstrapPrivate", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static Handle getBootstrap(final PreMethod method) {
        try {
            if (Modifier.isPrivate(method.modifiers)) {
                if (Modifier.isStatic(method.modifiers))
                    return Handles.getHandle(Runtime.class.getMethod("bootstrapPrivateDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                return Handles.getHandle(Runtime.class.getMethod("bootstrapPrivate", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
            } else {
                if (Modifier.isStatic(method.modifiers))
                    return Handles.getHandle(Runtime.class.getMethod("bootstrapDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                return Handles.getHandle(Runtime.class.getMethod("bootstrap", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static Handle getHandle(final Method method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, new Type(method.getDeclaringClass()).internalName(), method.getName(), getDescriptor(new Type(method.getReturnType()), Type.of(method.getParameterTypes())), code == H_INVOKEINTERFACE);
    }
    
    public static String getDescriptor(final PreMethod method) {
        return getDescriptor(method.returnType, method.parameters.toArray(new Type[0]));
    }
    
    public static String getDescriptor(final Type ret, final Type... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Type type : params) {
            builder.append(type.descriptorString());
        }
        builder
            .append(")")
            .append(ret.descriptorString());
        return builder.toString();
    }
    
}
