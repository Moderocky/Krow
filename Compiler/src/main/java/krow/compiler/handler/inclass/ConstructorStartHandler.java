package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.PreVariable;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

public class ConstructorStartHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("^void\\s+<init>\\s*\\(");
    
    @Override
    public boolean accepts(String statement) {
        if (!statement.startsWith("void")) return false;
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreMethod method = new PreMethod();
        method.owner = data.path;
        method.name = "<init>";
        method.returnType = new Type(void.class);
        method.modifiers |= context.upcoming();
        context.method = method;
        context.availableMethods.add(method);
        if (!context.upcoming(Modifier.STATIC)) {
            context.child.variables.add(new PreVariable("this", data.path));
        }
        return new HandleResult(null, statement.substring(statement.indexOf('(') + 1)
            .trim(), CompileState.IN_METHOD_HEADER);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_CONSTRUCTOR";
    }
}
