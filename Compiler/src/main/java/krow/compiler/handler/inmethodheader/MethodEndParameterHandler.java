package krow.compiler.handler.inmethodheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Modifier;

public class MethodEndParameterHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(")");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreMethod method = context.method;
        assert method.name != null;
        assert method.returnType != null;
        final MethodBuilder builder = context.builder.addMethod(method.name).setModifiers(0);
        if (context.child.exported) builder.addModifiers(Modifier.PUBLIC);
        if (context.child.isStatic) builder.addModifiers(Modifier.STATIC);
        if (method.isAbstract) builder.addModifiers(Modifier.ABSTRACT);
        else if (method.isFinal) builder.addModifiers(Modifier.FINAL);
        builder.addParameter(method.parameters.toArray(new Type[0]));
        builder.setReturnType(method.returnType);
        context.currentMethod = builder;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "END_OF_PARAMS";
    }
}
