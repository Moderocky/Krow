package krow.compiler.handler.inmethod;

import krow.compiler.*;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCallStartHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<target>" + Signature.TYPE_STRING + "|" + Signature.IDENTIFIER + ")\\s*\\.\\s*(?<name>" + Signature.IDENTIFIER + ")\\s*\\(");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String target = matcher.group("target");
        final String name = matcher.group("name");
        final Type type;
        boolean dynamic = true;
        if (target.equals("this")) {
            type = data.path;
            context.child.statement.add(WriteInstruction.loadThis());
        } else if (target.equals("super")) {
            type = data.extend;
            context.child.statement.add(WriteInstruction.loadThis());
        } else {
            final PreVariable variable = context.child.getVariable(target);
            if (variable != null) {
                type = variable.type();
                context.child.statement.add(variable.load(context.child.variables.indexOf(variable)));
            } else {
                type = Resolver.resolveType(target, context.availableTypes().toArray(new Type[0]));
                dynamic = false;
            }
        }
        if (!context.child.preparing.isEmpty()) {
            context.child.preparing.get(0).addParameter(type);
        }
        final PreMethodCall call;
        context.child.preparing.add(0, call = new PreMethodCall());
        call.dynamic = dynamic;
        call.owner = type;
        call.name = name;
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_CALL);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_CALL";
    }
}
