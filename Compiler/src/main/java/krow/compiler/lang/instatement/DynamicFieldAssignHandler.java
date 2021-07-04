package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreFieldCall;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class DynamicFieldAssignHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^#\\s*(?<name>" + Signature.IDENTIFIER + ")\\s*=");
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
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String input = matcher.group();
        final String name = matcher.group("name");
        assert context.child.point != null;
        final PreFieldCall call = new PreFieldCall();
        call.owner = context.child.point;
        call.name = name;
        call.dynamic = true;
        context.child.skip.add(0, call.set(context));
        context.child.point = null;
        context.child.staticState = false;
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "PREPARE_DYNAMIC_ASSIGN_FIELD";
    }
}
