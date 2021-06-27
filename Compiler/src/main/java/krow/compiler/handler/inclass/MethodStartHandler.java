package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.Resolver;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodStartHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("^(?:(?:abstract|final)\\s+)?(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*\\(");
    
    @Override
    public boolean accepts(String statement) {
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final boolean isFinal = statement.startsWith("final"), isAbstract = statement.startsWith("abstract");
        final String type = matcher.group("type");
        final String name = matcher.group("name");
        final PreMethod method = new PreMethod();
        method.owner = data.path;
        method.name = name;
        method.returnType = Resolver.resolveType(type, context.availableTypes().toArray(new Type[0]));
        method.isAbstract = isAbstract;
        method.isFinal = isFinal;
        method.isStatic = context.child.isStatic;
        context.method = method;
        context.availableMethods.add(method);
        if (!context.child.isStatic) {
            context.child.variables.add(new PreVariable("this", data.path));
        }
        return new HandleResult(null, statement.substring(statement.indexOf('(') + 1)
            .trim(), CompileState.IN_METHOD_HEADER);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_METHOD";
    }
}
