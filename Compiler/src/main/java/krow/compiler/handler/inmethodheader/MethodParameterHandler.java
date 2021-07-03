package krow.compiler.handler.inmethodheader;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class MethodParameterHandler implements DefaultHandler {
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
        return new HandleResult(null, statement.substring(input.length())
            .trim(), CompileState.METHOD_HEADER_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_METHOD_PARAM";
    }
}
