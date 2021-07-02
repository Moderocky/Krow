package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreFieldCall;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicFieldAccessHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^#\\s*(?<name>" + Signature.IDENTIFIER + ")\\s*(?![(=])");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, METHOD, DOWN, UP, LITERAL, VARIABLE, SMALL:
                return false;
        }
        if (context.child.point == null) return false;
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String name = matcher.group("name");
        assert context.child.point != null;
        final PreFieldCall call = new PreFieldCall();
        call.owner = context.child.point;
        call.name = name;
        call.dynamic = true;
        context.child.point = call.getType(context);
        context.child.statement(call.get(context));
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "RETRIEVE_FIELD_DYNAMIC";
    }
}
