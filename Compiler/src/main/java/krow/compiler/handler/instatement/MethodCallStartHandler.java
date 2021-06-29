package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCallStartHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\.\\s*(?<name>" + Signature.IDENTIFIER + ")\\s*\\(");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith(".")) return false;
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, DOWN, UP, FIELD, PRIMITIVE:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String name = matcher.group("name");
        final Type type = context.child.point;
        boolean dynamic = !context.child.staticState;
        final PreMethodCall call;
        context.child.preparing.add(0, call = new PreMethodCall());
        context.child.point = null;
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