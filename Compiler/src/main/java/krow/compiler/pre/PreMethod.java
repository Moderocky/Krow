package krow.compiler.pre;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;

public class PreMethod {
    
    public Type owner;
    public Type returnType;
    public List<Type> parameters = new ArrayList<>();
    public List<PreVariable> variables = new ArrayList<>();
    public String name;
    public boolean isAbstract, isFinal, isStatic;
    
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
    
    public void emergencyExit(final List<WriteInstruction> list) {
        switch (returnType.dotPath()) {
            case "int":
            case "boolean":
            case "char":
            case "short":
            case "byte":
                list.add(WriteInstruction.push0());
                list.add(WriteInstruction.convert(int.class, float.class));
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
    
}
