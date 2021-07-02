package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.util.Handles;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Handle;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class PreMethod {
    
    public Type owner;
    public Type returnType;
    public List<Type> parameters = new ArrayList<>();
    public List<PreVariable> variables = new ArrayList<>();
    public String name;
    public int modifiers;
    
    public PreMethod() {
    }
    
    public PreMethod(final Signature signature) {
        owner = signature.owner;
        returnType = signature.bound;
        parameters = (signature.inside == null ? new ArrayList<>() : List.of(signature.inside));
        name = signature.name;
    }
    
    public PreMethod(final Method method) {
        owner = new Type(method.getDeclaringClass());
        returnType = new Type(method.getReturnType());
        name = method.getName();
        for (final Class<?> type : method.getParameterTypes()) {
            parameters.add(new Type(type));
        }
        modifiers = method.getModifiers();
    }
    
    public Signature getSignature() {
        final Signature signature = new Signature();
        signature.inside = parameters.toArray(new Type[0]);
        signature.bound = returnType;
        signature.owner = owner;
        signature.name = name;
        signature.mode = Signature.Mode.METHOD;
        return signature;
    }
    
    public WriteInstruction execute(final CompileContext context, boolean dynamic) {
        if (name.equals("<init>")) {
            return WriteInstruction.invokeSpecial(owner, parameters.toArray(new Type[0]));
        } else if (Modifier.isAbstract(modifiers)) {
            return WriteInstruction.invokeInterface(owner, returnType, name, parameters.toArray(new Type[0]));
        } else if (dynamic) {
            return WriteInstruction.invokeVirtual(owner, returnType, name, parameters.toArray(new Type[0]));
        } else {
            return WriteInstruction.invokeStatic(owner, returnType, name, parameters.toArray(new Type[0]));
        }
    }
    
    public Handle handle() {
        if (name.equals("<init>")) {
            return new Handle(H_INVOKESPECIAL, this.owner.internalName(), this.name, getDescriptor(returnType, parameters.toArray(new Type[0])), false);
        }
        final int code;
        if (Modifier.isStatic(this.modifiers)) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(this.modifiers)) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(this.modifiers)) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, this.owner.internalName(), this.name, getDescriptor(returnType, parameters.toArray(new Type[0])), code == H_INVOKEINTERFACE);
    }
    
    public WriteInstruction invokeDynamic(boolean dynamic) {
        final Handle bootstrap = Handles.getPrivateBootstrap(dynamic);
        if (!dynamic)
            return WriteInstruction.invokeDynamic(returnType, name, parameters.toArray(new Type[0]), bootstrap, org.objectweb.asm.Type.getType(owner.descriptor()));
        else {
            List<Type> params = new ArrayList<>(parameters);
            params.add(0, owner);
            return WriteInstruction.invokeDynamic(returnType, name, params.toArray(new Type[0]), bootstrap, org.objectweb.asm.Type.getType(owner.descriptor()));
        }
    }
    
    public void emergencyExit(final List<WriteInstruction> list) {
        switch (returnType.dotPath()) {
            case "int", "boolean", "char", "short", "byte" -> {
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnSmall());
            }
            case "long" -> {
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnLong());
            }
            case "double" -> {
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnDouble());
            }
            case "float" -> {
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnFloat());
            }
            case "void" -> list.add(WriteInstruction.returnEmpty());
            default -> {
                list.add(WriteInstruction.pushNull());
                list.add(WriteInstruction.returnObject());
            }
        }
    }
    
    public WriteInstruction invoke() {
        return this.invoke(false);
    }
    
    public WriteInstruction invoke(boolean isInterface) {
        if (Modifier.isStatic(modifiers)) {
            return WriteInstruction.invokeStatic(owner, returnType, name, parameters.toArray(new Type[0]));
        } else if (isInterface) {
            return WriteInstruction.invokeInterface(owner, returnType, name, parameters.toArray(new Type[0]));
        } else {
            return WriteInstruction.invokeVirtual(owner, returnType, name, parameters.toArray(new Type[0]));
        }
    }
    
    @Override
    public String toString() {
        final List<String> params = new ArrayList<>();
        for (final Type parameter : parameters) {
            params.add(parameter.getSimpleName());
        }
        return owner.getSimpleName() + "::" + name + "(" + String.join(",", params) + ")" + returnType.getSimpleName();
    }
    
    
    static String getDescriptor(final Type ret, final Type... params) {
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
