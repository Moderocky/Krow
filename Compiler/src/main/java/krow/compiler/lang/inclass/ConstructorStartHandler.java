package krow.compiler.lang.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.PreVariable;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ConstructorStartHandler implements DefaultHandler {
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
        if (context.isInterface) throw new RuntimeException("Cannot place constructor in interface.");
        context.method = method;
        context.availableMethods.add(method);
        if (context.upcoming(Modifier.STATIC)) throw new RuntimeException("Class <init> declared static.");
        context.child.variables.add(new PreVariable("this", data.path));
        return new HandleResult(null, statement.substring(statement.indexOf('(') + 1)
            .trim(), CompileState.METHOD_HEADER_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_CONSTRUCTOR";
    }
}
