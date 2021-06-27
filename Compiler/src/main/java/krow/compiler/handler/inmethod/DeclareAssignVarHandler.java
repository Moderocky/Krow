package krow.compiler.handler.inmethod;

import krow.compiler.*;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeclareAssignVarHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?:final |var )?(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*=");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String target = matcher.group("type");
        final String name = matcher.group("name");
        final Type type = Resolver.resolveType(target, context.availableTypes().toArray(new Type[0]));
        assert (context.getVariable(name) == null);
        context.child.variables.add(new PreVariable(name, type));
        context.expectation = CompileExpectation.OBJECT;
        int throwback = (input.startsWith("final ") ? 6 : input.startsWith("var ") ? 4 : 0) + target.length();
        return new HandleResult(null, statement.substring(throwback).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_VARIABLE";
    }
}
