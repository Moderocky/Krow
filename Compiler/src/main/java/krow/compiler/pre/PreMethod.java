package krow.compiler.pre;

import krow.compiler.CompileContext;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
        parameters = List.of(signature.inside);
        name = signature.name;
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
        PreMethod method = context.findMethod(owner, name, parameters);
        if (method == null) method = context.findMethod(owner, name, parameters.size());
        if (context.upcoming(Modifier.ABSTRACT)) {
            return WriteInstruction.invokeInterface(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        } else if (dynamic) {
            return WriteInstruction.invokeVirtual(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        } else {
            return WriteInstruction.invokeStatic(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        }
    }
    
    public void emergencyExit(final List<WriteInstruction> list) {
        switch (returnType.dotPath()) {
            case "int":
            case "boolean":
            case "char":
            case "short":
            case "byte":
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnSmall());
                break;
            case "long":
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnLong());
                break;
            case "double":
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnDouble());
                break;
            case "float":
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.returnFloat());
                break;
            case "void":
                list.add(WriteInstruction.returnEmpty());
                break;
            default:
                list.add(WriteInstruction.pushNull());
                list.add(WriteInstruction.returnObject());
                break;
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
        return "PreMethod{" +
            "owner=" + owner +
            ", returnType=" + returnType +
            ", parameters=" + parameters +
            ", variables=" + variables +
            ", name='" + name + '\'' +
            ", modifiers=" + modifiers +
            '}';
    }
}
