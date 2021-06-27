package krow.compiler.handler.inmethodheader;

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

public class MethodParameterHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*(?=[,)])");
    
    @Override
    public boolean accepts(String statement) {
        return !statement.startsWith(",") && !statement.startsWith(")") &&
            PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final Type type = Resolver.resolveType(matcher.group("type"), context.availableTypes().toArray(new Type[0]));
        final String name = matcher.group("name");
        final PreMethod method = context.method;
        final PreVariable variable = new PreVariable(name, type);
        method.parameters.add(type);
        method.variables.add(variable);
        context.child.variables.add(variable);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_METHOD_HEADER);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_METHOD_PARAM";
    }
}
