package krow.compiler.pre;

import krow.compiler.CompileContext;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;

public class PreMethodCall {
    
    public boolean dynamic;
    public Type owner;
    public String name;
    public List<Type> parameters = new ArrayList<>();
    
    public void addParameter(final Type type) {
        parameters.add(type);
    }
    
    public WriteInstruction execute(final CompileContext context) {
        PreMethod method = context.findMethod(owner, name, parameters);
        if (method == null) method = context.findMethod(owner, name, parameters.size());
        if (method.isAbstract) {
            return WriteInstruction.invokeInterface(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        } else if (dynamic) {
            return WriteInstruction.invokeVirtual(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        } else {
            return WriteInstruction.invokeStatic(method.owner, method.returnType, method.name, method.parameters.toArray(new Type[0]));
        }
    }
    
}
