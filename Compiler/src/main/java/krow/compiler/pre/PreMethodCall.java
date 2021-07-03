package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.api.CompileState;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;

public class PreMethodCall {
    
    public boolean dynamic;
    public boolean indy;
    public Type owner;
    public String name;
    public Type returnType;
    public List<Type> parameters = new ArrayList<>();
    public CompileState state;
    
    public void addParameter(final Type type) {
        parameters.add(type);
    }
    
    public void ensureReturnType(final CompileContext context) {
        PreMethod method = context.findMethod(owner, name, parameters);
        if (method == null) method = context.findMethod(owner, name, parameters.size());
        returnType = method.returnType;
    }
    
    public WriteInstruction execute(final CompileContext context) {
        PreMethod method = context.findMethod(owner, name, parameters);
        if (method == null) method = context.findMethod(owner, name, parameters.size());
        if (method == null) {
            if ("1".equals(System.getProperty("TEST_STATE")))
                throw new RuntimeException("Method unavailable: '" + this
                    + "'\nAvailable: " + context.availableMethods);
            else
                throw new RuntimeException("Method unavailable: '" + this + "'");
        }
        returnType = method.returnType;
        if (indy) return method.invokeDynamic(dynamic);
        else return method.execute(context, dynamic);
    }
    
    @Override
    public String toString() {
        final List<String> params = new ArrayList<>();
        for (final Type parameter : parameters) {
            params.add(parameter.getSimpleName());
        }
        return owner.getSimpleName() + "::" + name + "(" + String.join(",", params) + ")" + (returnType != null ? returnType.getSimpleName() : "?");
    }
}
