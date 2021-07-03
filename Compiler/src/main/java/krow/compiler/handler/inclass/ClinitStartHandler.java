package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

public class ClinitStartHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^void\\s+<clinit>\\s*\\(");
    
    @Override
    public boolean accepts(String statement) {
        if (!statement.startsWith("void")) return false;
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreMethod method = new PreMethod();
        method.owner = data.path;
        method.name = "<clinit>";
        method.returnType = new Type(void.class);
        if (!context.upcoming(Modifier.STATIC)) throw new RuntimeException("Class <clinit> not declared static.");
        method.modifiers |= context.upcoming();
        context.method = method;
        context.availableMethods.add(method);
        return new HandleResult(null, statement.substring(statement.indexOf('(') + 1)
            .trim(), CompileState.METHOD_HEADER_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_CLINIT";
    }
}
