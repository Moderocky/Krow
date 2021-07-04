package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.Resolver;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class MethodStartHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*\\(");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String type = matcher.group("type");
        final String name = matcher.group("name");
        final PreMethod method = new PreMethod();
        method.owner = data.path;
        method.name = name;
        method.returnType = Resolver.resolveType(type, context.availableTypes().toArray(new Type[0]));
        method.modifiers |= context.upcoming();
        context.method = method;
        context.availableMethods.add(method);
        if (!context.upcoming(Modifier.STATIC)) {
            context.child.variables.add(new PreVariable("this", data.path));
        }
        return new HandleResult(null, statement.substring(statement.indexOf('(') + 1)
            .trim(), CompileState.METHOD_HEADER_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_METHOD";
    }
}
